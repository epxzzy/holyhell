package com.dead_comedian.holyhell.client.renderer.block_entity;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.server.block.CoffinBlock;
import com.dead_comedian.holyhell.server.block.entity.CoffinBlockEntity;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class CoffinRenderer<T extends CoffinBlockEntity> implements BlockEntityRenderer<T> {

    private ResourceLocation DEACTIVATED = ResourceLocation.fromNamespaceAndPath(HolyHell.MOD_ID, "textures/entity/blockentity/coffin_deactivated.png");
    private ResourceLocation ACTIVATED = ResourceLocation.fromNamespaceAndPath(HolyHell.MOD_ID, "textures/entity/blockentity/coffin_activated.png");


    private final ModelPart group;
    private final ModelPart lid;
    private final ModelPart coffin;

    public CoffinRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(HolyHellModelLayers.COFFIN);
        this.group = root.getChild("group");
        this.lid = this.group.getChild("lid");
        this.coffin = this.group.getChild("coffin");
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition group = partdefinition.addOrReplaceChild("group", CubeListBuilder.create(), PartPose.offset(6.0F, 11.9797F, -15.8228F));

        PartDefinition lid = group.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 43).mirror().addBox(-8.0F, -1.5F, -16.0F, 16.0F, 3.0F, 32.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(8F, 12.5F, 0F));

        PartDefinition coffin = group.addOrReplaceChild("coffin", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-8.0F, -5.5F, -16.0F, 16.0F, 11.0F, 32.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(8F, 5.5F, 0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    @Override
    public void render(CoffinBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(DEACTIVATED));
        BlockState state = blockEntity.getBlockState();


        //BASE RENDERING

        poseStack.pushPose();
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

        if (state.getValue(CoffinBlock.STATE) == 1) {
            if (blockEntity.renderCounter < 10) {
                float f = (float) blockEntity.ticks + partialTick;
                float f3 = Mth.sin(f * 3) / 20;
                poseStack.mulPose(Axis.YP.rotation(f3));
            } else {
                poseStack.mulPose(Axis.YP.rotation(0));
            }
        }
        coffin.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();


        //LID RENDERING

        poseStack.pushPose();
        if (state.getValue(CoffinBlock.ACTIVATED)) {
            vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(ACTIVATED));
        }

        if (state.getValue(CoffinBlock.STATE) == 2) {

            switch (state.getValue(CoffinBlock.FACING)) {
                case Direction.SOUTH:
                    poseStack.mulPose(Axis.YN.rotationDegrees(180));
                    poseStack.translate(-1, 0, -2  );
                    break;
                case Direction.EAST:
                    poseStack.translate(2, 0, 0);
                    poseStack.mulPose(Axis.YN.rotationDegrees(90));
                    break;
                case Direction.WEST:
                    poseStack.translate(-1, 0, 1);
                    poseStack.mulPose(Axis.YN.rotationDegrees(270));
                    break;
                case Direction.NORTH:
                    poseStack.translate(0, 0, -1);
                    poseStack.mulPose(Axis.YN.rotationDegrees(0));
                    break;
            }
        }else {
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
                case Direction.NORTH:
                    poseStack.translate(0, 0, 0);
                    poseStack.mulPose(Axis.YN.rotationDegrees(0));
                    break;
            }
        }


        if (state.getValue(CoffinBlock.STATE) == 1) {
            if (blockEntity.renderCounter < 10) {
                float f = (float) blockEntity.ticks + partialTick;
                float f3 = Mth.sin(f * 3) / 20;
                poseStack.mulPose(Axis.YP.rotation(f3));
            } else {
                poseStack.mulPose(Axis.YP.rotation(0));
            }
        }
        lid.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();


    }


    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(T blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return net.minecraft.world.phys.AABB.encapsulatingFullBlocks(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
    }
}
