package com.dead_comedian.holyhell.server.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BloodlustEffect extends MobEffect {


    public BloodlustEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        super.onEffectAdded(livingEntity, amplifier);
        if (livingEntity.getAttribute(Attributes.MAX_HEALTH).getValue() <= 20) {
            livingEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
        }
    }
}