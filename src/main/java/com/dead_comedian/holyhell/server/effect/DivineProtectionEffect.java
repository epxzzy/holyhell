package com.dead_comedian.holyhell.server.effect;


import com.dead_comedian.holyhell.server.registries.HolyHellAttachments;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;


public class DivineProtectionEffect extends MobEffect {
    int repeat = 75;

    public DivineProtectionEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        livingEntity.setData(HolyHellAttachments.RENDER_RINGS, true);
        livingEntity.level().playSound(livingEntity, livingEntity.blockPosition(),
                HolyHellSounds.RINGS_INTRO.get(), SoundSource.PLAYERS, 0.2f, 1);
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