package com.dead_comedian.holyhell.server.block.entity;

import com.dead_comedian.holyhell.server.block.CoffinBlock;
import com.dead_comedian.holyhell.server.data.StoredInventory;
import com.dead_comedian.holyhell.server.registries.HolyHellAttachments;
import com.dead_comedian.holyhell.server.registries.HolyHellBlockEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class CoffinBlockEntity extends BlockEntity implements Container {

    public CoffinBlockEntity(BlockPos pos, BlockState state) {
        super(HolyHellBlockEntities.COFFIN_BLOCK_ENTITY.get(), pos, state);

    }

    private UUID storedUUID;

    public UUID getStoredUUID() {
        return storedUUID;
    }

    public void setStoredUUID(UUID storedUUID) {
        this.storedUUID = storedUUID;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int slot) {
        return null;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return null;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {

    }


    public void tick(Level level, BlockPos pos, BlockState state1) {


        if (!state1.getValue(CoffinBlock.OPEN)) return;
        if (getStoredUUID() == null) return;

        Player player = level.getPlayerByUUID(getStoredUUID());
        List<StoredInventory.InventoryCodec> storedInventory = player.getData(HolyHellAttachments.SAVED_INVENTORY).getSlot();


        if (level.getGameTime() % 5 == 0 && !storedInventory.isEmpty()) {
            level.playSound((Player) null, pos, SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.BLOCKS, 1, 1);

            ItemStack itemStack = storedInventory.removeFirst().stack();
            DefaultDispenseItemBehavior.spawnItem(
                    level,
                    itemStack,
                    2,
                    Direction.UP,
                    Vec3.atBottomCenterOf(pos).add(0, 2, 0)
            );

        }

        if (storedInventory.isEmpty()) {
            level.playSound((Player) null, pos, HolyHellSounds.COFFIN_LID.get(), SoundSource.BLOCKS, 1, 1);
            level.setBlock(pos, state1.setValue(CoffinBlock.OPEN, false).setValue(CoffinBlock.ACTIVATED, false), 3);
            player.setData(HolyHellAttachments.HAS_COFFIN, false);
            player.setData(HolyHellAttachments.DIED, false);
            this.setStoredUUID(null);
            storedInventory.clear();
        }
    }
}
