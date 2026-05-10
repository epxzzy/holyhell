package com.dead_comedian.holyhell.server.entity.ai.memory_module;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

import java.util.List;

public class NearestTranscendingEntityMemoryModule extends NearestVisibleLivingEntities {
    public NearestTranscendingEntityMemoryModule(LivingEntity livingEntity, List<LivingEntity> nearbyLivingEntities) {
        super(livingEntity, nearbyLivingEntities);
    }
}
