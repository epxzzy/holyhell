package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.HolyHell;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class RangedAttack extends Behavior<LivingEntity> {

    public int cooldown;
    public int attackCooldownStored;
    public int distanceToTarget;

    public RangedAttack(int attackCooldown, int distanceToTarget) {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT
        ));

        this.cooldown = attackCooldown;
        this.attackCooldownStored = attackCooldown;
        this.distanceToTarget = distanceToTarget;
    }

    @Override
    protected void tick(ServerLevel level, LivingEntity owner, long gameTime) {
        super.tick(level, owner, gameTime);
        cooldown--;
        if (owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
            LivingEntity target = owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();

            if (owner.distanceTo(target) <= distanceToTarget) {
                if (owner instanceof RangedAttackMob && cooldown <= 0) {
                    ((RangedAttackMob) owner).performRangedAttack(target, 2);
                    cooldown = attackCooldownStored;
                }

            } else {
                BehaviorUtils.setWalkAndLookTargetMemories(owner, target, 1.5f, distanceToTarget-1);
            }
        }

    }

    @Override
    protected boolean canStillUse(ServerLevel level, LivingEntity entity, long gameTime) {
        return true;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity owner) {
        if (owner instanceof RangedAttackMob) {
            return owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get().isAlive();
        } else {
            HolyHell.LOGGER.warn("Living entity must implement RangedAttackMob interface");
            return false;
        }
    }

    @Override
    protected void stop(ServerLevel level, LivingEntity entity, long gameTime) {
        super.stop(level, entity, gameTime);

    }
}
