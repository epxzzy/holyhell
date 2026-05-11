package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.AABB;

public class DealDashDamage extends Behavior<RevenantEntity> {
    public static final int DURATION = 20;

    public DealDashDamage() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                HolyHellMemoryModules.SHOULD_DAMAGE.get(), MemoryStatus.VALUE_PRESENT), DURATION);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, RevenantEntity entity, long gameTime) {
        return true;
    }

    @Override
    protected void tick(ServerLevel level, RevenantEntity owner, long gameTime) {
        super.tick(level, owner, gameTime);
        if (owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
            LivingEntity targetEntity = owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
            AABB revenantThrustAABB = owner.getBoundingBox().expandTowards(2.5, 1.5, 2.5);

            if (revenantThrustAABB.intersects(targetEntity.getBoundingBox())) {
                if (!targetEntity.isBlocking()) {
                    owner.doHurtTarget(targetEntity);
                } else {
                    double d0 = targetEntity.getX() - owner.getX();
                    double d1 = targetEntity.getZ() - owner.getZ();
                    double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
                    targetEntity.push(d0 / d2 * 4.0, 0.2, d1 / d2 * 4.0);
                }
                owner.getBrain().eraseMemory(HolyHellMemoryModules.SHOULD_DAMAGE.get());
            }
        }
    }

    @Override
    protected void stop(ServerLevel level, RevenantEntity entity, long gameTime) {
        entity.getBrain().eraseMemory(HolyHellMemoryModules.SHOULD_DAMAGE.get());
    }
}

