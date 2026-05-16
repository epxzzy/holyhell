package com.dead_comedian.holyhell.server.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.ArrayList;
import java.util.List;

public class StoredInventory {

    List<InventoryCodec> slot;


    public StoredInventory(List<InventoryCodec> list) {
        slot = new ArrayList<>(list);
    }

    public StoredInventory(IAttachmentHolder iAttachmentHolder) {
    }

    public static StoredInventory saveInventory(Inventory inventory) {
        List<InventoryCodec> entries = new ArrayList<>();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                entries.add(new InventoryCodec(i, stack));
            }
        }

        return new StoredInventory(entries);
    }

    public List<InventoryCodec> getSlot() {
        return slot;
    }

    public static final MapCodec<StoredInventory> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    InventoryCodec.INVENTORY_CODEC.listOf().fieldOf("slots").forGetter(data -> data.slot)
            ).apply(instance, StoredInventory::new));


    public record InventoryCodec(int slot, ItemStack stack) {

        public static final Codec<InventoryCodec> INVENTORY_CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        Codec.INT.fieldOf("slot").forGetter(InventoryCodec::slot),
                        ItemStack.CODEC.fieldOf("stack").forGetter(InventoryCodec::stack)
                ).apply(inst, InventoryCodec::new));

    }

}
