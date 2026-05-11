package com.dead_comedian.holyhell.server.entity.ai.sensor;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Set;

public class AttackableEntitySensor extends Sensor<RevenantEntity> {
    @Override
    protected void doTick(ServerLevel level, RevenantEntity entity) {
        AABB searchBox = entity.getBoundingBox().inflate(5, 1, 5);
        List<Entity> nearbyEntities = level.getEntities(entity, searchBox, entity1 -> entity1.getType().is(HolyHellTags.Entities.REVENANT_TRANSCENDS));
        nearbyEntities.sort((entityA, entityB) -> Float.compare(entity.distanceTo(entityA), entity.distanceTo(entityB)));

        if (entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
            if (entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get() instanceof Player player) {
                if (player.isCreative() || player.isSpectator()) {
                    entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                }
            }
        }

        if (!nearbyEntities.isEmpty()) {
            Entity entity1 = nearbyEntities.getFirst();
            if (entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()) {
                entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, ((LivingEntity) (Object) entity1));
            }


            if (entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get().isRemoved()) {
                entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }
        }


    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.ATTACK_TARGET);
    }
}
