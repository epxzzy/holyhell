package com.dead_comedian.holyhell.client.renderer.block_entity;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.server.block.CoffinBlock;
import com.dead_comedian.holyhell.server.block.entity.CoffinBlockEntity;
import com.dead_comedian.holyhell.server.helper.CoffinAnimationStates;
import com.dead_comedian.holyhell.server.registries.HolyHellModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CoffinRenderer<T extends CoffinBlockEntity> implements BlockEntityRenderer<T> {

    private ResourceLocation DEACTIVATED = ResourceLocation.fromNamespaceAndPath(HolyHell.MOD_ID, "textures/entity/blockentity/coffin_deactivated.png");
    private ResourceLocation ACTIVATED = ResourceLocation.fromNamespaceAndPath(HolyHell.MOD_ID, "textures/entity/blockentity/coffin_activated.png");


    private ModelPart group;
    private ModelPart model;
    private ModelPart lid;
    public int counter = 0;

    public CoffinRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(HolyHellModelLayers.COFFIN);
        this.group = root.getChild("group");
        this.model = this.group.getChild("model");
        this.lid = this.model.getChild("lid");
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition group = partdefinition.addOrReplaceChild("group", CubeListBuilder.create(), PartPose.offset(0.0F, 7.0F, 16.0F));

        PartDefinition model = group.addOrReplaceChild("model", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -7.0F, -16.0F, 16.0F, 11.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 0.0F, -16.0F));

        PartDefinition lid = model.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 43).addBox(-8.0F, -1.5F, -16.0F, 16.0F, 3.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(DEACTIVATED));
        Level level = blockEntity.getLevel();
        Block block = level.getBlockState(blockEntity.getBlockPos()).getBlock();
        BlockState state = level.getBlockState(blockEntity.getBlockPos());


        poseStack.pushPose();
        if (block instanceof CoffinBlock) {
            if (state.getValue(CoffinBlock.ACTIVATED)) {
                vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(ACTIVATED));
            }

            switch (state.getValue(CoffinBlock.FACING)) {
                case Direction.SOUTH:
                    poseStack.mulPose(Axis.YN.rotationDegrees(180));
                    poseStack.translate(-1, 0, -1);
                    break;
                case Direction.EAST:
                    poseStack.translate(1, 0, 0);
                    poseStack.mulPose(Axis.YN.rotationDegrees(90));
                    break;
                case Direction.WEST:
                    poseStack.translate(0, 0, 1);
                    poseStack.mulPose(Axis.YN.rotationDegrees(270));
                    break;
            }

            if (state.getValue(CoffinBlock.STATE) == CoffinAnimationStates.NONE.getId()) {
                counter = 0;
            }
            if (state.getValue(CoffinBlock.STATE) == CoffinAnimationStates.CHARGE.getId()) {
                if (counter < 10) {
                    counter++;
                    poseStack.mulPose(Axis.YN.rotationDegrees((float) Math.asin((float) 360 / counter)));
                } else {
                    state.setValue(CoffinBlock.STATE, CoffinAnimationStates.NONE.getId());
                    counter = 0;
                }

            }

        }
        group.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }


    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(T blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return net.minecraft.world.phys.AABB.encapsulatingFullBlocks(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
    }
}
