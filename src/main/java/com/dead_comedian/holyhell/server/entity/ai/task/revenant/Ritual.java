package com.dead_comedian.holyhell.server.entity.ai.task.revenant;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class Ritual extends Behavior<RevenantEntity> {
    public Ritual() {
        super(ImmutableMap.of(
                HolyHellMemoryModules.TRANSCENDING_MOBS_DETECTED.get(), MemoryStatus.VALUE_PRESENT)
        );
    }

    @Override
    protected void start(ServerLevel level, RevenantEntity entity, long gameTime) {


        super.start(level, entity, gameTime);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, RevenantEntity entity, long gameTime) {
        return true;
    }

    @Override
    protected void tick(ServerLevel level, RevenantEntity owner, long gameTime) {


    }
}
