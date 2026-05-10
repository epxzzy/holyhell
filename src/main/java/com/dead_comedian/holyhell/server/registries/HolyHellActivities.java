package com.dead_comedian.holyhell.server.registries;

import com.dead_comedian.holyhell.HolyHell;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HolyHellActivities {
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(Registries.ACTIVITY, HolyHell.MOD_ID);

    public static final Supplier<Activity> AWAKE = ACTIVITIES.register("awake", () -> new Activity("awake"));

    public static void register(IEventBus modBusEvent){
        ACTIVITIES.register(modBusEvent);
    }
}

