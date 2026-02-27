package com.dead_comedian.holyhell.server.effect;


import com.dead_comedian.holyhell.server.registries.HolyHellAttachments;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;


public class JesistenceEffect extends MobEffect {

    int repeat = 75;

    public JesistenceEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public void onMobRemoved(LivingEntity livingEntity, int amplifier, Entity.RemovalReason reason) {
        livingEntity.setData(HolyHellAttachments.DAMAGE_ABSORBED.get(), 0F);
        livingEntity.setData(HolyHellAttachments.SHOULD_EXPLODE.get(), false);
        livingEntity.setData(HolyHellAttachments.RENDER_RINGS, false);
        super.onMobRemoved(livingEntity, amplifier, reason);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        livingEntity.setData(HolyHellAttachments.RENDER_RINGS, true);
        super.onEffectAdded(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {


        repeat--;
        if (repeat <= 0) {
            repeat = 75;
        }
        return super.shouldApplyEffectTickThisTick(duration, amplifier);
    }

}