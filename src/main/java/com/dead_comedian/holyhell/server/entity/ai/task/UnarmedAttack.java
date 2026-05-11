package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.entity.ai.RevenantStates;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;


public class UnarmedAttack extends Behavior<RevenantEntity> {
    public static final int DURATION = 20;

    public UnarmedAttack() {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                HolyHellMemoryModules.WEAPON_POS.get(),MemoryStatus.VALUE_ABSENT
        ), DURATION);
    }

    @Override
    protected void tick(ServerLevel level, RevenantEntity owner, long gameTime) {
        super.tick(level, owner, gameTime);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, RevenantEntity owner) {
        return owner.distanceTo(owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get()) <= 3 && owner.getState().getId() == 3 && owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).isEmpty();
    }

    @Override
    protected boolean canStillUse(ServerLevel level, RevenantEntity entity, long gameTime) {
        return true;
    }

    @Override
    protected void start(ServerLevel level, RevenantEntity entity, long gameTime) {
        super.start(level, entity, gameTime);
        entity.setState(RevenantStates.ATTACK_UNARMED);

    }

    @Override
    protected void stop(ServerLevel level, RevenantEntity entity, long gameTime) {
        super.stop(level, entity, gameTime);


        if (entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
            entity.doHurtTarget(entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get());
        }
        entity.setState(RevenantStates.UNARMED);
    }
}
