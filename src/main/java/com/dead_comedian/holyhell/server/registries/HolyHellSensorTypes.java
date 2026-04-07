package com.dead_comedian.holyhell.server.registries;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.server.entity.RevenantEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.MobSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HolyHellSensorTypes {

    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES =
            DeferredRegister.create(Registries.SENSOR_TYPE, HolyHell.MOD_ID);

    public static final Supplier<SensorType<MobSensor<RevenantEntity>>> TRANSCENDING_MOBS_SENSOR =
            SENSOR_TYPES.register("transcending_mobs_sensor",
                    () -> new SensorType<>(() -> new MobSensor<>(10, RevenantEntity::shouldEvaporate, RevenantEntity::isAlive, HolyHellMemoryModules.TRANSCENDING_MOBS_DETECTED.get(), 80)));

    public static void register(IEventBus eventBus) {
        SENSOR_TYPES.register(eventBus);
    }


}





