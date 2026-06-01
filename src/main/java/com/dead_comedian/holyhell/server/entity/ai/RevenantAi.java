package com.dead_comedian.holyhell.server.entity.ai;

import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import com.dead_comedian.holyhell.server.entity.ai.task.revenant.*;
import com.dead_comedian.holyhell.server.registries.HolyHellActivities;
import com.dead_comedian.holyhell.server.registries.HolyHellMemoryModules;
import com.dead_comedian.holyhell.server.registries.HolyHellSensorTypes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
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
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,

            HolyHellMemoryModules.WEAPON_POS.get(),
            HolyHellMemoryModules.IS_AWAKE.get(),
            HolyHellMemoryModules.SHOULD_DAMAGE.get()


    );

    public static final ImmutableList<SensorType<? extends Sensor<? super RevenantEntity>>> SENSORS = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY,
            HolyHellSensorTypes.REVENANT_ATTACKABLE_ENTITY.get(),
            HolyHellSensorTypes.NEARBY_WEAPON.get(),
            HolyHellSensorTypes.IS_AWAKE_SENSOR.get()

    );

    public static Brain<?> makeBrain(Brain<RevenantEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initAwakeActivities(brain);

        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    public static void updateActivity(RevenantEntity revenant) {
        revenant.getBrain().setActiveActivityToFirstValid(ImmutableList.of(
                HolyHellActivities.AWAKE.get(),
                Activity.IDLE
        ));
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
                                if (mob instanceof RevenantEntity revenant && revenant.getState().getId() == 0) {
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
                        Pair.of(0, new SitDown())
                )
        );

    }


    private static void initAwakeActivities(Brain<RevenantEntity> brain) {
        brain.addActivityWithConditions(
                HolyHellActivities.AWAKE.get(),
                ImmutableList.of(
                        Pair.of(0, new Swim(0.8F)),
                        Pair.of(0, new SitUp()),
                        Pair.of(1, new LookAtTargetSink(45, 90)),
                        Pair.of(1, new MoveToTargetSink()),
                        Pair.of(1, new RevenantPrepareTarget()),
                        Pair.of(2, new Ritual()),
                        Pair.of(2, new PickUpWeapon()),
                        Pair.of(2, new PlaceWeapon()),
                        Pair.of(3, new UnarmedAttack()),
                        Pair.of(3, new ArmedAttack()),
                        Pair.of(4, new DealDashDamage())

                ),
                ImmutableSet.of(
                        Pair.of(HolyHellMemoryModules.IS_AWAKE.get(), MemoryStatus.VALUE_PRESENT)
                )
        );
    }


}
