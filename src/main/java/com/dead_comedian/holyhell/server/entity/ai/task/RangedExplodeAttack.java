package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.entity.AngelEntity;
import com.dead_comedian.holyhell.server.entity.KamikazeEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellParticles;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RangedExplodeAttack extends Behavior<KamikazeEntity> {

    public int cooldown;
    public int attackCooldownStored;
    public int distanceToTarget;

    public RangedExplodeAttack(int attackCooldown, int distanceToTarget) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));

        this.cooldown = attackCooldown;
        this.attackCooldownStored = attackCooldown;
        this.distanceToTarget = distanceToTarget;
    }

    @Override
    protected void tick(ServerLevel level, KamikazeEntity owner, long gameTime) {
        super.tick(level, owner, gameTime);
        cooldown--;
        if (owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
            LivingEntity target = owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();

            if (distanceToNoHieght(owner, target) <= distanceToTarget && cooldown <= 0) {
                BehaviorUtils.setWalkAndLookTargetMemories(owner, target, 2f, 0);
                if (owner.distanceTo(target) < 0.5) {
                    owner.explode(0.35d);
                    level.sendParticles(HolyHellParticles.KAMIKAZE_EXPLOSION.get(), owner.getX(), owner.getY(), owner.getZ(), 1, 0, 0, 0, 1);
                    AngelEntity angelEntity = new AngelEntity(HolyHellEntities.ANGEL.get(), level);
                    level.addFreshEntity(angelEntity);
                    angelEntity.moveTo(owner.getBlockX() + 0.5, owner.getBlockY(), owner.getBlockZ() + 0.5);
                    owner.discard();

                }


            } else {
                BehaviorUtils.setWalkAndLookTargetMemories(owner, target, 1.5f, distanceToTarget - 1);
            }
        }

    }

    public float distanceToNoHieght(LivingEntity a, Entity entity) {
        float f = (float) (a.getX() - entity.getX());
        float f2 = (float) (a.getZ() - entity.getZ());
        return Mth.sqrt(f * f + f2 * f2);
    }


    @Override
    protected boolean canStillUse(ServerLevel level, KamikazeEntity entity, long gameTime) {
        return true;
    }


    @Override
    protected void stop(ServerLevel level, KamikazeEntity entity, long gameTime) {
        super.stop(level, entity, gameTime);

    }
}
