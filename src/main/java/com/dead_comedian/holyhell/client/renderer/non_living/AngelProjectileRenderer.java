package com.dead_comedian.holyhell.client.renderer.non_living;


import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.client.model.entity.non_living.AngelProjectileModel;
import com.dead_comedian.holyhell.server.entity.non_living.AngelProjectileEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;


public class AngelProjectileRenderer extends EntityRenderer<AngelProjectileEntity> {

    private static final ResourceLocation TEXTURE1 = ResourceLocation.fromNamespaceAndPath(HolyHell.MOD_ID, "textures/entity/angel_projectile.png");
    private final AngelProjectileModel<AngelProjectileEntity> model;

    public AngelProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new AngelProjectileModel<>(context.bakeLayer(HolyHellModelLayers.ANGEL_PROJECTILE));
    }




    @Override
    public void render(AngelProjectileEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        pPoseStack.scale(0.8F,0.8F,0.8F);
        pPoseStack.pushPose();
        VertexConsumer vertexConsumer = pBuffer.getBuffer(this.model.renderType(TEXTURE1));
        this.model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
        model.setupAnim(pEntity, 0, 0, pEntity.tickCount + pPartialTicks, 0, 0);

        pPoseStack.popPose();

        // Call the parent render method
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, 15728640);
    }

    @Override
    protected int getBlockLightLevel(AngelProjectileEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(AngelProjectileEntity entity) {
        return TEXTURE1;
    }

}