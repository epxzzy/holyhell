package com.dead_comedian.holyhell.server.entity.ai.sensor;

import com.dead_comedian.holyhell.server.entity.AngelEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Set;

public class AttackablePlayerSensor extends Sensor<LivingEntity> {
    @Override
    protected void doTick(ServerLevel level, LivingEntity entity) {
        double follow = entity.getAttribute(Attributes.FOLLOW_RANGE).getValue();
        AABB searchBox = entity.getBoundingBox().inflate(follow, 1, follow);
        List<Entity> nearbyEntities = level.getEntities(entity, searchBox, entity1 -> entity1 instanceof Player player && !player.isCreative() && !player.isSpectator());
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
                entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, ((Player) (Object) entity1));
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
