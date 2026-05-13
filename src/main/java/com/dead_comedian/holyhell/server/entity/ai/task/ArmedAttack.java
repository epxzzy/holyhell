package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.entity.ai.RevenantStates;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ArmedAttack extends Behavior<RevenantEntity> {

    public static final int DURATION = 20;

    public ArmedAttack() {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT,
                HolyHellMemoryModules.SHOULD_DAMAGE.get(), MemoryStatus.VALUE_ABSENT
        ), DURATION);
    }

    @Override
    protected void tick(ServerLevel level, RevenantEntity owner, long gameTime) {
        super.tick(level, owner, gameTime);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, RevenantEntity owner) {
        return owner.distanceTo(owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get()) <= 5
                && owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get() instanceof Player
                && owner.getState().getId() == 4;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, RevenantEntity entity, long gameTime) {
        return true;
    }

    @Override
    protected void start(ServerLevel level, RevenantEntity entity, long gameTime) {
        super.start(level, entity, gameTime);
        entity.setState(RevenantStates.ATTACK_ARMED);
    }


    @Override
    protected void stop(ServerLevel level, RevenantEntity entity, long gameTime) {
        super.stop(level, entity, gameTime);
        if (entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
            entity.getBrain().setMemory(HolyHellMemoryModules.SHOULD_DAMAGE.get(), true);
            LivingEntity targetEntity = entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
            Vec3 direction = targetEntity.position().subtract(entity.position()).multiply(1, 0, 1).normalize();
            entity.addDeltaMovement(direction.scale(6));


        }
        entity.setState(RevenantStates.ARMED);
    }

}