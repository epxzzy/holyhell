package com.dead_comedian.holyhell.server.entity.ai.task;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.entity.ai.task.revenant.Ritual;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import com.dead_comedian.holyhell.server.registries.HolyHellSensorTypes;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;
import java.util.Set;

public class RevenantAi {


    public static final List<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            HolyHellMemoryModules.TRANSCENDING_MOBS_DETECTED.get(),
            MemoryModuleType.PATH
    );

    public static final List<SensorType<? extends Sensor<? super RevenantEntity>>> SENSORS = ImmutableList.of(
            HolyHellSensorTypes.TRANSCENDING_MOBS_SENSOR.get(),
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY
    );

    public static Brain<?> makeBrain(Brain<RevenantEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    public static void updateActivity(RevenantEntity revenant) {
        revenant.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    private static void initCoreActivity(Brain<RevenantEntity> brain) {
        brain.addActivity(
                Activity.CORE,
                0,
                ImmutableList.of(
                        new LookAtTargetSink(45, 90),
                        new MoveToTargetSink() {
                            @Override
                            protected boolean checkExtraStartConditions(ServerLevel serverLevel, Mob mob) {
                                if (mob instanceof RevenantEntity revenant && revenant.isCatatonic()) {
                                    return false;
                                }

                                return super.checkExtraStartConditions(serverLevel, mob);
                            }
                        }


                )
        );
    }


    private static void initIdleActivity(Brain<RevenantEntity> brain) {
        brain.addActivity(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(1, new Ritual())
                )
        );

    }

}
