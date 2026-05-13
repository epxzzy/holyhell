package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.block.CandleholderBlock;
import com.dead_comedian.holyhell.server.block.TallCandleholderBlock;
import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.entity.ai.RevenantStates;
import com.dead_comedian.holyhell.server.registries.HolyHellBlocks;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class PlaceWeapon extends Behavior<RevenantEntity> {
    public PlaceWeapon() {
        super(ImmutableMap.of(HolyHellMemoryModules.WEAPON_POS.get(), MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, RevenantEntity owner) {
        return owner.distanceToSqr(
                owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get().getX(),
                owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get().getY(),
                owner.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get().getZ()
        ) <= 6 &&
                owner.getState().getId() == 4;
    }

    @Override
    protected void start(ServerLevel level, RevenantEntity entity, long gameTime) {
        super.start(level, entity, gameTime);
        level.setBlock(entity.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get(),
                HolyHellBlocks.TALL_CANDLEHOLDER.get().defaultBlockState().setValue(TallCandleholderBlock.PIECE, 0),
                3);
        level.setBlock(entity.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get().above(),
                HolyHellBlocks.TALL_CANDLEHOLDER.get().defaultBlockState().setValue(TallCandleholderBlock.PIECE, 1),
                3);
        level.setBlock(entity.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).get().above(2),
                HolyHellBlocks.TALL_CANDLEHOLDER.get().defaultBlockState().setValue(TallCandleholderBlock.PIECE, 2),
                3);
        entity.setState(RevenantStates.UNARMED);
        entity.getBrain().eraseMemory(HolyHellMemoryModules.WEAPON_POS.get());
    }
}
