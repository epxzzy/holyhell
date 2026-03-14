package com.dead_comedian.holyhell.server.helper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WaveTracker {

    private final List<UUID> mobs = new ArrayList<>();

    public void add(Entity entity) {
        mobs.add(entity.getUUID());
    }

    public boolean isWaveFinished(ServerLevel level) {

        mobs.removeIf(uuid -> {
            Entity entity = level.getEntity(uuid);
            return entity == null || !entity.isAlive();
        });

        return mobs.isEmpty();
    }

    public void clear() {
        mobs.clear();
    }

    public boolean isEmpty() {
        return mobs.isEmpty();
    }
}
