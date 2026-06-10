package com.dead_comedian.holyhell.server.item.custom;

import com.dead_comedian.holyhell.server.registries.HolyHellParticles;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;

import java.util.List;

public class HolyShieldItem extends ShieldItem {
    public HolyShieldItem(Properties properties) {
        super(properties);
    }


    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            level.addParticle(HolyHellParticles.KAMIKAZE_EXPLOSION.get(), entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), 0, 0, 0);

            float index = getPowerForTime(this.getUseDuration(stack, entityLiving) - timeLeft);

            double d = (player.getBoundingBox().minX + player.getBoundingBox().maxX) / 2.0;
            double e = (player.getBoundingBox().minZ + player.getBoundingBox().maxZ) / 2.0;
            List<Entity> entities = level.getEntities(entityLiving, entityLiving.getBoundingBox().inflate(4));

            player.getCooldowns().addCooldown(this, (int) Math.clamp(60 * index, 15, 50));

            for (Entity entity : entities) {
                if (entity != player) {
                    double f = entity.getX() - d;
                    double g = entity.getZ() - e;
                    double h = Math.max(f * f + g * g, 0.1);
                    entity.push(f / h * 3 * index, 0.6 * index, g / h * 3 * index);
                    level.playSound(entityLiving, entityLiving.blockPosition(), HolyHellSounds.METAL_HURT.get(), SoundSource.PLAYERS, 0.5f, 1.4f);
                }
            }
        }
    }

    public static float getPowerForTime(int charge) {
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 96;
    }
}
