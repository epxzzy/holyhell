package com.dead_comedian.holyhell.server.registries;

import com.dead_comedian.holyhell.HolyHell;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class HolyHellBiomes {

    public static final ResourceKey<Biome> ANGEL = register("angel");


    private static ResourceKey<Biome> register(String key) {
        return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(HolyHell.MOD_ID, key));
    }

}
