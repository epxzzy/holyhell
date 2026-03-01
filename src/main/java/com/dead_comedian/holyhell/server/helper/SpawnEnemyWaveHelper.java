package com.dead_comedian.holyhell.server.helper;

import com.dead_comedian.holyhell.server.block.entity.DiviningTableBlockEntity;
import com.dead_comedian.holyhell.server.entity.AngelEntity;
import com.dead_comedian.holyhell.server.entity.HereticEntity;
import com.dead_comedian.holyhell.server.entity.KamikazeEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpawnEnemyWaveHelper {

    int[] mobSpawnIndex = new int[]{5, 3, 4};
    double capacity = 5;
    int current = 0;


    public SpawnEnemyWaveHelper() {

    }

    public void spawnMobs(int difficulty, BlockPos blockPos, Level level, BlockPos tablePos) {
        List<Entity> trackedEntities = new ArrayList<>();
        if (blockPos != null && level != null && tablePos != null) {
            do {
                double angle = (2 * Math.PI / capacity * difficulty) * current;
                int radius = 8;
                boolean bool = true;

                double x = blockPos.getX() + (Math.cos(angle) * radius);
                double z = blockPos.getZ() + (Math.sin(angle) * radius);
                double y = blockPos.getY() + 0.4;
                BlockPos blockPos2 = new BlockPos((int) x, (int) (y), (int) z);


                do {
                    if (!level.getBlockState(blockPos2).is(Blocks.AIR)) {
                        radius--;
                    } else {
                        bool = false;
                    }

                }
                while (bool);
                Mob mob = null;
                int mobIndex = level.getRandom().nextInt(0, 3);

                mob = switch (mobIndex) {
                    case 0 -> new AngelEntity(HolyHellEntities.ANGEL.get(), level);
                    case 1 -> new KamikazeEntity(HolyHellEntities.KAMIKAZE.get(), level);
                    case 2 -> new HereticEntity(HolyHellEntities.HERETIC.get(), level);
                    default -> mob;
                };

                current = current + mobSpawnIndex[mobIndex];


                if (mob != null) {
                    level.addFreshEntity(mob);
                    mob.moveTo(blockPos2, mob.getYRot(), mob.getXRot());
                trackedEntities.add(mob);
                }


            }
            while (
                    current < capacity * difficulty
            );


            if (current > capacity) {

                level.playSound((Player) null, blockPos, HolyHellSounds.BELL_RING.get(), SoundSource.PLAYERS, 1F, 1F);
                if (level.getBlockEntity(tablePos) instanceof DiviningTableBlockEntity) {
                ((DiviningTableBlockEntity) (Object) Objects.requireNonNull(level.getBlockEntity(tablePos))).setTrackedEntities(trackedEntities);
                    ((DiviningTableBlockEntity) (Object) Objects.requireNonNull(level.getBlockEntity(tablePos))).setDifficulty(difficulty);
                }
                current = 0;
            }
        }
    }
}
