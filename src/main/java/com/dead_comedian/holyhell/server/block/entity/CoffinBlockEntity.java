package com.dead_comedian.holyhell.server.block.entity;

import com.dead_comedian.holyhell.CommonConfig;
import com.dead_comedian.holyhell.server.block.CoffinBlock;
import com.dead_comedian.holyhell.server.data.StoredInventory;
import com.dead_comedian.holyhell.server.registries.HolyHellAttachments;
import com.dead_comedian.holyhell.server.registries.HolyHellBlockEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
        this.ticks = 0;
    }

    public int renderCounter = 0;

    public boolean renderCounterToggle = true;

    public int ticks;

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


    public void tick(Level level, BlockPos pos, BlockState state) {

        if (ticks < 100) {
            ticks++;
        } else {
            ticks = 0;
        }

        if (state.getValue(CoffinBlock.STATE) == 1) {
            if (renderCounter < 10 && renderCounterToggle) {
                renderCounter++;
                if (renderCounter >= 9) {
                    renderCounterToggle = false;
                }
            } else if (renderCounter > 0 && !renderCounterToggle) {
                renderCounter--;
                if (renderCounter <= 1) {
                    renderCounterToggle = true;
                    renderCounter = 0;
                    level.setBlock(pos, state.setValue(CoffinBlock.STATE, 0), 3);
                }


            }
        }

        if (CommonConfig.ENABLE_COFFINS.get()) {
            if (!state.getValue(CoffinBlock.OPEN)) return;
            if (getStoredUUID() == null) return;
            if (level.getPlayerByUUID((getStoredUUID())) == null) return;

            Player player = level.getPlayerByUUID(getStoredUUID());
            List<StoredInventory.InventoryCodec> storedInventory = player.getData(HolyHellAttachments.SAVED_INVENTORY).getSlot();

            if (storedInventory == null) {

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE.getType(), pos.getX(), pos.getY(), pos.getZ(), 10, 0.5, 0.5, 0.5, 0.05);
                }

                level.playSound((Player) null, pos, HolyHellSounds.COFFIN_LID.get(), SoundSource.BLOCKS, 1, 1);
                level.setBlock(pos, state.setValue(CoffinBlock.OPEN, false)
                        .setValue(CoffinBlock.ACTIVATED, false)
                        .setValue(CoffinBlock.STATE, 1), 3);
                player.setData(HolyHellAttachments.HAS_COFFIN, false);
                player.setData(HolyHellAttachments.DIED, false);

            } else {

                if (storedInventory.isEmpty()) {


                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE.getType(), pos.getX(), pos.getY(), pos.getZ(), 10, 0.5, 0.5, 0.5, 0.05);
                    }

                    level.playSound((Player) null, pos, HolyHellSounds.COFFIN_LID.get(), SoundSource.BLOCKS, 1, 1);
                    level.setBlock(pos, state
                            .setValue(CoffinBlock.OPEN, false)
                            .setValue(CoffinBlock.ACTIVATED, false)
                            .setValue(CoffinBlock.STATE, 3), 3);

                    player.setData(HolyHellAttachments.HAS_COFFIN, false);
                    player.setData(HolyHellAttachments.DIED, false);

                    this.setStoredUUID(null);
                    storedInventory.clear();
                } else if (level.getGameTime() % 5 == 0 && !storedInventory.isEmpty()) {
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
            }
        }
    }
}
