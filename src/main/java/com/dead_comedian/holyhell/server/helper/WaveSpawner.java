package com.dead_comedian.holyhell.server.helper;

import com.dead_comedian.holyhell.server.entity.AngelEntity;
import com.dead_comedian.holyhell.server.entity.HereticEntity;
import com.dead_comedian.holyhell.server.entity.KamikazeEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;

public class WaveSpawner {

    public static List<Entity> spawnWave(ServerLevel level, BlockPos center, int difficulty) {

        List<Entity> spawned = new ArrayList<>();

        int mobCount = Math.min(4 + difficulty, 24);

        for (int i = 0; i < mobCount; i++) {

            double angle = (Math.PI * 2 / mobCount) * i;
            int radius = 8;

            int x = center.getX() + (int)(Math.cos(angle) * radius);
            int z = center.getZ() + (int)(Math.sin(angle) * radius);
            int y = center.getY();

            BlockPos spawnPos = new BlockPos(x, y, z);

            Mob mob = switch(level.random.nextInt(3)) {

                case 0 -> new AngelEntity(HolyHellEntities.ANGEL.get(), level);
                case 1 -> new KamikazeEntity(HolyHellEntities.KAMIKAZE.get(), level);
                default -> new HereticEntity(HolyHellEntities.HERETIC.get(), level);
            };

            mob.moveTo(
                    spawnPos.getX() + 0.5,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5,
                    0,
                    0
            );

            level.addFreshEntity(mob);

            spawned.add(mob);
        }

        return spawned;
    }
}
