package com.dead_comedian.holyhell.server.event;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.server.registries.HolyHellEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = HolyHell.MOD_ID)
public class SacrificialKatarEvents {


    @SubscribeEvent
    public static void criticalHitEvent(LivingHealEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasEffect(HolyHellEffects.BLOODLUST)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void giveBuffsFromKatar(LivingDeathEvent event) {

        LivingEntity victimEntity = event.getEntity();
        LivingEntity entity1 = (LivingEntity) event.getSource().getDirectEntity();
        if (victimEntity instanceof LivingEntity && entity1 != null && victimEntity != entity1) {
            if (entity1.hasEffect(HolyHellEffects.BLOODLUST)) {
                entity1.setHealth((float) (entity1.getHealth() + victimEntity.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 0.3F));


                if (victimEntity instanceof RangedAttackMob) {
                    entity1.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));

                }
                if (victimEntity instanceof FlyingAnimal) {
                    entity1.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 1));

                }
                if (victimEntity instanceof Monster) {
                    entity1.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));

                }
                if (victimEntity instanceof Animal) {
                    entity1.addEffect(new MobEffectInstance(MobEffects.SATURATION, 200, 1));

                }
            }
        }

    }

    @SubscribeEvent
    public static void removeRegen(PlayerTickEvent.Post event) {
        Player entity = event.getEntity();
        if (entity.hasEffect(HolyHellEffects.BLOODLUST) && entity.hasEffect(MobEffects.REGENERATION)) {
            entity.removeEffect(MobEffects.REGENERATION);
        }
        if (entity.hasEffect(HolyHellEffects.BLOODLUST) && entity.hasEffect(MobEffects.HEALTH_BOOST)) {
            entity.removeEffect(MobEffects.HEALTH_BOOST);
        }
        if (entity.hasEffect(HolyHellEffects.BLOODLUST) && entity.hasEffect(MobEffects.HEAL)) {
            entity.removeEffect(MobEffects.HEAL);

        }
    }
}
