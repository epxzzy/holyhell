package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.entity.ai.RevenantStates;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;

public class PickUpWeapon extends Behavior<RevenantEntity> {
    public PickUpWeapon() {
        super(ImmutableMap.of(HolyHellMemoryModules.WEAPON_POS.get(), MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, RevenantEntity owner) {
        return owner.distanceToSqr(
                owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get().getX(),
                owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get().getY(),
                owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get().getZ()
        ) <= 6 &&
                owner.getState().getId() == 3 &&
                owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get() instanceof Player;
    }

    @Override
    protected void start(ServerLevel level, RevenantEntity entity, long gameTime) {
        super.start(level, entity, gameTime);
        level.destroyBlock(entity.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get(), false);
        entity.setState(RevenantStates.ARMED);
    }
}
