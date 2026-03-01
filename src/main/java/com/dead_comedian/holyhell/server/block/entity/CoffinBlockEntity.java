package com.dead_comedian.holyhell.server.block.entity;

import com.dead_comedian.holyhell.server.block.CoffinBlock;
import com.dead_comedian.holyhell.server.data.PlayerCoffinStatus;
import com.dead_comedian.holyhell.server.data.StoredInventory;
import com.dead_comedian.holyhell.server.menu.CoffinMenu;
import com.dead_comedian.holyhell.server.registries.HolyHellAttachments;
import com.dead_comedian.holyhell.server.registries.HolyHellBlockEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.items.ItemStackHandler;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CoffinBlockEntity extends BlockEntity implements MenuProvider {

    @Nullable
    private UUID storedPlayer;

    private final ItemStackHandler inventory = new ItemStackHandler(59) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private boolean topRender;
    private boolean leftRender;
    private boolean midRender;
    private boolean rightRender;
    private boolean bottomRender;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> topRender ? 1 : 0;
                case 1 -> leftRender ? 1 : 0;
                case 2 -> midRender ? 1 : 0;
                case 3 -> rightRender ? 1 : 0;
                case 4 -> bottomRender ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            boolean b = value != 0;
            switch (index) {
                case 0 -> topRender = b;
                case 1 -> leftRender = b;
                case 2 -> midRender = b;
                case 3 -> rightRender = b;
                case 4 -> bottomRender = b;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public CoffinBlockEntity(BlockPos pos, BlockState state) {
        super(HolyHellBlockEntities.COFFIN_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ContainerData getData() {
        return data;
    }

    public void setStoredPlayer(@Nullable UUID uuid) {
        storedPlayer = uuid;
        setChanged();
    }

    @Nullable
    public UUID getStoredPlayer() {
        return storedPlayer;
    }


    @Override
    public Component getDisplayName() {
        return Component.literal("Coffin");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {

        if (storedPlayer != null && level instanceof ServerLevel) {
            loadStoredPlayerInventory(storedPlayer);
        }

        if (level != null) {
            level.playSound(null, worldPosition,
                    HolyHellSounds.COFFIN_LID.get(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return new CoffinMenu(id, playerInv, this);
    }



    public void tick(Level level, BlockPos pos, BlockState state) {

        topRender = inventory.getStackInSlot(54).is(Items.GOLD_INGOT);
        leftRender = inventory.getStackInSlot(55).is(Items.GOLD_INGOT);
        midRender = inventory.getStackInSlot(56).is(Items.GOLD_INGOT);
        rightRender = inventory.getStackInSlot(57).is(Items.GOLD_INGOT);
        bottomRender = inventory.getStackInSlot(58).is(Items.GOLD_INGOT);

        boolean active = topRender && leftRender && midRender && rightRender && bottomRender;

        if (level instanceof ServerLevel server && storedPlayer != null) {
            level.setBlock(worldPosition,
                    state.setValue(CoffinBlock.ACTIVATED, active), 3);

            if (server.getPlayerByUUID(storedPlayer) != null) {
                PlayerCoffinStatus status =
                        server.getPlayerByUUID(storedPlayer)
                                .getData(HolyHellAttachments.COFFIN_STATUS);

                status.update(active, pos);
            }
        }
    }


    public void loadStoredPlayerInventory(UUID playerId) {
        if (!(level instanceof ServerLevel server)) return;

        var player = server.getPlayerByUUID(playerId);
        if (player == null) return;

        StoredInventory data =
                player.getData(HolyHellAttachments.STORED_INVENTORY);

        if (data == null) return;

        for (int i = 0; i < 54; i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < 36; i++) {
            inventory.setStackInSlot(i, data.items[i]);
            data.items[i] = ItemStack.EMPTY;
        }

        for (int i = 0; i < 4; i++) {
            inventory.setStackInSlot(36 + i, data.armor[i]);
            data.armor[i] = ItemStack.EMPTY;
        }
        inventory.setStackInSlot(40, data.offhand[0]);
        data.offhand[0] = ItemStack.EMPTY;

        setChanged();
    }

    public void postDeathHook() {
        for (int i = 54; i <= 58; i++) {
            ItemStack gold = inventory.getStackInSlot(i);
            if (gold.is(Items.GOLD_INGOT)) {
                gold.shrink(1);
            }
        }
    }



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("Inventory", inventory.serializeNBT(registries));

        if (storedPlayer != null) {
            tag.putUUID("StoredPlayer", storedPlayer);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));

        if (tag.hasUUID("StoredPlayer")) {
            storedPlayer = tag.getUUID("StoredPlayer");
        }
    }
}