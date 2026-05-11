package com.dead_comedian.holyhell.server.entity.ai.sensor;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class IsAwakeSensor extends Sensor<RevenantEntity> {
    @Override
    protected void doTick(ServerLevel level, RevenantEntity entity) {
        if (entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent() ||
                (entity.getBrain().getMemory(HolyHellMemoryModules.WEAPON_POS.get()).isPresent() )
        ) {

            entity.getBrain().setMemory(HolyHellMemoryModules.IS_AWAKE.get(), true);
        } else {
            entity.getBrain().eraseMemory(HolyHellMemoryModules.IS_AWAKE.get());
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(HolyHellMemoryModules.IS_AWAKE.get());
    }
}
