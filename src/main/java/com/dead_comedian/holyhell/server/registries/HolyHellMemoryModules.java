package com.dead_comedian.holyhell.server.registries;

import com.dead_comedian.holyhell.HolyHell;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

public class HolyHellMemoryModules {


    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES =
            DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, HolyHell.MOD_ID);

    public static final Supplier<MemoryModuleType<Boolean>> TRANSCENDING_MOBS_DETECTED = MEMORY_MODULE_TYPES.register("transcending_mobs_detected", () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    public static void register(IEventBus eventBus) {
        MEMORY_MODULE_TYPES.register(eventBus);

    }
}
