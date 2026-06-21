package com.dead_comedian.holyhell.server.block.entity;

import com.dead_comedian.holyhell.CommonConfig;
import com.dead_comedian.holyhell.server.block.CoffinBlock;
import com.dead_comedian.holyhell.server.block.property.CoffinState;
import com.dead_comedian.holyhell.server.data.StoredInventory;
import com.dead_comedian.holyhell.server.registries.HolyHellAttachments;
import com.dead_comedian.holyhell.server.registries.HolyHellBlockEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class CoffinBlockEntity extends BlockEntity {
    public static final int LID_SLIDE_DURATION_TICKS = 20;
    public static final int ITEM_DISPENSE_INTERVAL_TICKS = 5;

    public CoffinBlockEntity(BlockPos pos, BlockState state) {
        super(HolyHellBlockEntities.COFFIN_BLOCK_ENTITY.get(), pos, state);
        this.ticks = 0;
    }

    public int renderCounter = 0;
    public boolean renderCounterToggle = true;
    public int ticks = 0;
    private float lidSlideTicksOld = 0;
    private float lidSlideTicks = 0;
    private UUID storedUUID;

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        UUID storedUuid = this.getStoredUUID();

        if (storedUuid != null) {
            tag.putUUID("stored_uuid", storedUuid);
        }

        tag.putFloat("lid_slide_ticks", this.lidSlideTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.hasUUID("stored_uuid")) {
            this.setStoredUUID(tag.getUUID("stored_uuid"));
        }
        this.lidSlideTicks = tag.getFloat("lid_slide_ticks");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putFloat("lid_slide_ticks", this.lidSlideTicks);

        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (ticks < 100) {
            ticks++;
        } else {
            ticks = 0;
        }

        /*
        if (state.getValue(CoffinBlock.STATE) == CoffinState.ACTIVATED) {
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
                    level.setBlock(pos, state.setValue(CoffinBlock.STATE, CoffinState.CLOSED), 3);
                }
            }
        }
        */

        if (state.getValue(CoffinBlock.STATE) == CoffinState.OPEN) {
            this.handleLidSliding(level, state, pos);
        }

        if (CommonConfig.ENABLE_COFFINS.get() && state.getValue(CoffinBlock.STATE) == CoffinState.OPEN) {
            this.handleItemDispensing(level, state, pos);
        }
    }

    private void handleLidSliding(Level level, BlockState state, BlockPos pos) {
        this.lidSlideTicksOld = this.lidSlideTicks;

        if (state.getValue(CoffinBlock.STATE) == CoffinState.OPEN) {
            this.lidSlideTicks = Mth.clamp(this.lidSlideTicks + 1.0f, 0.0f, LID_SLIDE_DURATION_TICKS);
        }
        else if (state.getValue(CoffinBlock.STATE) == CoffinState.CLOSING) {
            this.lidSlideTicks = Mth.clamp(this.lidSlideTicks - 1.0f, 0.0f, LID_SLIDE_DURATION_TICKS);
        }

        if (this.lidSlideTicks <= 0) {
            level.setBlock(pos, state.setValue(CoffinBlock.STATE, CoffinState.CLOSED), 3);
        }
    }

    private void handleItemDispensing(Level level, BlockState state, BlockPos pos) {
        UUID storedUuid = this.getStoredUUID();
        if (storedUuid == null) return;

        Player player = level.getPlayerByUUID(storedUuid);
        if (player == null) return;

        List<StoredInventory.InventoryCodec> savedInventory = player.getData(HolyHellAttachments.SAVED_INVENTORY).getSlot();
        if (savedInventory == null) return;

        if (level.getGameTime() % ITEM_DISPENSE_INTERVAL_TICKS != 0) return;

        if (savedInventory.isEmpty()) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE.getType(), pos.getX(), pos.getY(), pos.getZ(), 10, 0.5, 0.5, 0.5, 0.05);
            }

            level.playSound(null, pos, HolyHellSounds.COFFIN_LID.get(), SoundSource.BLOCKS, 1, 1);
            level.setBlock(pos, state.setValue(CoffinBlock.STATE, CoffinState.CLOSING), 3);

            player.setData(HolyHellAttachments.HAS_COFFIN, false);
            player.setData(HolyHellAttachments.DIED, false);

            this.setStoredUUID(null);
            savedInventory.clear();
        }
        else if (this.canDispenseItems()) {
            level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.BLOCKS, 1, 1);

            ItemStack itemStack = savedInventory.removeFirst().stack();
            DefaultDispenseItemBehavior.spawnItem(
                    level,
                    itemStack,
                    2,
                    Direction.UP,
                    Vec3.atBottomCenterOf(pos).add(0, 2, 0)
            );
        }
    }

    public UUID getStoredUUID() {
        return storedUUID;
    }

    public void setStoredUUID(UUID storedUUID) {
        this.storedUUID = storedUUID;
    }

    public float getLidSlidingAnimationProgress(float partialTicks) {
        return Mth.lerp(partialTicks, this.lidSlideTicksOld, this.lidSlideTicks) / LID_SLIDE_DURATION_TICKS;
    }

    public boolean canDispenseItems() {
        return this.lidSlideTicks >= LID_SLIDE_DURATION_TICKS;
    }
}
