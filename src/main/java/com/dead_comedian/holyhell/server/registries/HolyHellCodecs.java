package com.dead_comedian.holyhell.server.registries;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.server.data.StatueData;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class HolyHellCodecs {

    public static final ResourceKey<Registry<StatueData.FullStatueCodec>> STATUES = key("statues");

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HolyHell.MOD_ID, name));
    }

}
