package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.entity.ai.RevenantStates;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class SitDown extends Behavior<RevenantEntity> {

    public static final int DURATION = 20;

    public SitDown() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT), DURATION);
    }


    @Override
    protected void start(ServerLevel level, RevenantEntity entity, long gameTime) {
        super.start(level, entity, gameTime);
        entity.setState(RevenantStates.SIT_DOWN);
        entity.playSound(HolyHellSounds.REVENANT_RISE.get());
    }

    @Override
    protected void stop(ServerLevel level, RevenantEntity entity, long gameTime) {
        super.stop(level, entity, gameTime);
        entity.setState(RevenantStates.CATATONIC);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, RevenantEntity owner) {
        return owner.getState().getId() == 3;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, RevenantEntity entity, long gameTime) {
        return true;
    }
}
