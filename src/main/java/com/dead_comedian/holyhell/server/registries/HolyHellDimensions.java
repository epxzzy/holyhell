package com.dead_comedian.holyhell.server.registries;

import com.dead_comedian.holyhell.HolyHell;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HolyHellDimensions {
    public static final DeferredRegister<Level> LEVEL =
            DeferredRegister.create(Registries.DIMENSION, HolyHell.MOD_ID);


    public static final ResourceKey<Level> ANGEL = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(HolyHell.MOD_ID,"angel"));


    public static void register(IEventBus eventBus) {
        LEVEL.register(eventBus);
    }

}
