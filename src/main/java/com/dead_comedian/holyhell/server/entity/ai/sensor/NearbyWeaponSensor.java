package com.dead_comedian.holyhell.server.entity.ai.sensor;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellBlocks;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Optional;
import java.util.Set;

public class NearbyWeaponSensor extends Sensor<RevenantEntity> {

    @Override
    protected void doTick(ServerLevel level, RevenantEntity entity) {

        for (BlockPos pos : BlockPos.betweenClosed(entity.blockPosition().offset(-15, -2, -15), entity.blockPosition().offset(15, 2, 15))) {

            Optional<BlockPos> memory = entity.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get());
            if (level.getBlockState(pos).is(HolyHellBlocks.TALL_CANDLEHOLDER.get()) && memory.isEmpty() && (entity.getState().getId() == 3 || entity.getState().getId()==0)) {
                entity.getBrain().setMemory(HolyHellMemoryModules.WEAPON_POS.get(), pos.immutable());
            }
            if (memory.isPresent() &&
                    !level.getBlockState(memory.get()).is(HolyHellBlocks.TALL_CANDLEHOLDER.get()) &&
                    entity.getState().getId() == 3) {
                entity.getBrain().eraseMemory(HolyHellMemoryModules.WEAPON_POS.get());
            }
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(HolyHellMemoryModules.WEAPON_POS.get());
    }
}
