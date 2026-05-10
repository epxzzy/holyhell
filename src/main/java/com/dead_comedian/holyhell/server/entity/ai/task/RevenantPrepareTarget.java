package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import java.util.Map;

public class RevenantPrepareTarget extends Behavior<RevenantEntity> {
    public RevenantPrepareTarget() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected void tick(ServerLevel level, RevenantEntity owner, long gameTime) {
        super.tick(level, owner, gameTime);

        owner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(
                owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get().blockPosition(),
                1F,
                1));
    }

    @Override
    protected boolean canStillUse(ServerLevel level, RevenantEntity entity, long gameTime) {
        return !entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty();
    }
}
