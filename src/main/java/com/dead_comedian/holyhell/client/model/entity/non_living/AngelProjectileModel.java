package com.dead_comedian.holyhell.client.model.entity.non_living;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.dead_comedian.holyhell.server.entity.non_living.AngelProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class AngelProjectileModel<T extends AngelProjectileEntity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    private final ModelPart body;
    private final ModelPart ring_2;
    private final ModelPart ring_1;
    private final ModelPart ring_3;
    private final ModelPart eye;

	public AngelProjectileModel(ModelPart root) {
        this.body = root.getChild("body");
        this.ring_2 = root.getChild("body").getChild("ring_2");
        this.ring_1 = root.getChild("body").getChild("ring_1");
        this.ring_3 = root.getChild("body").getChild("ring_3");
        this.eye = root.getChild("body").getChild("eye");
	}

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 5.75F, -0.25F));

        PartDefinition ring_2 = body.addOrReplaceChild("ring_2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.25F, 0.25F));

        PartDefinition cube_r2 = ring_2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, -0.7854F, 0.7854F));

        PartDefinition ring_1 = body.addOrReplaceChild("ring_1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.25F, 0.25F));

        PartDefinition cube_r3 = ring_1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(1, 18).addBox(-6.0F, -6.0F, -2.0F, 12.0F, 12.0F, 4.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, -0.7854F, 0.7854F));

        PartDefinition ring_3 = body.addOrReplaceChild("ring_3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.25F, 0.25F, -0.7854F, -0.7854F, 0.7854F));

        PartDefinition cube_r4 = ring_3.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(1, 35).addBox(-6.0F, -6.0F, -2.0F, 12.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition eye = body.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(26, 45).addBox(-9.5F, -9.5F, 0.0F, 19.0F, 19.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.25F, -0.25F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }



    @Override
    public ModelPart root() {
        return body;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        ring_1.xRot += ageInTicks * 0.1F;
        ring_2.yRot += ageInTicks * 0.1F;
        ring_3.zRot += ageInTicks * 0.1F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}