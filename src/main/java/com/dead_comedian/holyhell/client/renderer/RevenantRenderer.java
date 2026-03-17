package com.dead_comedian.holyhell.client.renderer;


import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.client.model.entity.RevenantModel;
import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;


public class RevenantRenderer extends MobRenderer<RevenantEntity, RevenantModel<RevenantEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HolyHell.MOD_ID, "textures/entity/revenant.png");

    public RevenantRenderer(EntityRendererProvider.Context context) {
        super(context, new RevenantModel<>(context.bakeLayer(HolyHellModelLayers.REVENANT)), 0.6f);
    }

    @Override
    public ResourceLocation getTextureLocation(RevenantEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(RevenantEntity mobEntity, float f, float g, PoseStack matrixStack,
                       MultiBufferSource vertexConsumerProvider, int i) {


        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

}