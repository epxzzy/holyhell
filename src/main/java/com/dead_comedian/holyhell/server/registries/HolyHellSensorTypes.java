package com.dead_comedian.holyhell.server.registries;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.server.entity.ai.sensor.AttackableEntitySensor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HolyHellSensorTypes {

    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES =
            DeferredRegister.create(Registries.SENSOR_TYPE, HolyHell.MOD_ID);

    public static final Supplier<SensorType<AttackableEntitySensor>> ATTACKABLE_ENTITY =
            SENSOR_TYPES.register("attackable_entity",
                    () -> new SensorType<>(AttackableEntitySensor::new));


    public static void register(IEventBus eventBus) {
        SENSOR_TYPES.register(eventBus);
    }


}





