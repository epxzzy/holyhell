package com.dead_comedian.holyhell.server.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class TableLootData {

    public record TableLoot(HolderSet<Item> item, int chance) {
        public static final Codec<TableLoot> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("item_id").forGetter(TableLoot::item),
                        Codec.INT.fieldOf("chance").forGetter(TableLoot::chance)
                ).apply(instance, TableLoot::new)
        );
    }
}
