package com.dead_comedian.holyhell.server.block.entity;

import com.dead_comedian.holyhell.Holyhell;
import com.dead_comedian.holyhell.server.block.DiviningTableBlock;
import com.dead_comedian.holyhell.server.registries.HolyHellBlockEntities;
import com.dead_comedian.holyhell.server.registries.HolyhellParticles;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DiviningTableBlockEntity extends BlockEntity {

    public int timer = 0;

    private int difficulty;

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    private boolean cooldown = false;
    private boolean canDropLoot = false;
    private boolean readyToSpawn = false;

    public void enableCooldown(boolean cooldown) {
        canDropLoot = false;
        readyToSpawn = false;
        this.cooldown = cooldown;
    }

    public void enableReadyToSpawn(boolean readyToSpawn) {
        canDropLoot = false;
        cooldown = false;
        this.readyToSpawn = readyToSpawn;
    }

    public void enableCanDropLoot(boolean canDropLoot) {
        cooldown = false;
        readyToSpawn = false;
        this.canDropLoot = canDropLoot;
    }

    public void setTimer(int timer1) {
        timer = timer1;
    }


    private List<Entity> trackedEntities = new ArrayList<>();

    public List<Entity> getTrackedEntities() {
        return trackedEntities;
    }

    public void setTrackedEntities(List<Entity> trackedEntities) {
        this.trackedEntities = trackedEntities;
    }

    public void ejectReward(ServerLevel level, BlockPos pos, ResourceKey<LootTable> lootTable) {
        LootTable loottable = level.getServer().reloadableRegistries().getLootTable(lootTable);
        LootParams lootparams = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        ObjectArrayList<ItemStack> objectarraylist = loottable.getRandomItems(lootparams);
        if (!objectarraylist.isEmpty()) {
            for (ItemStack itemstack : objectarraylist) {
                int randomInt = level.getRandom().nextInt(1, 5);
                switch (randomInt) {
                    case 1:
                        DefaultDispenseItemBehavior.spawnItem(level, itemstack, 1, Direction.UP, Vec3.atBottomCenterOf(pos).add(1, 2, 1));
                        break;
                    case 2:
                        DefaultDispenseItemBehavior.spawnItem(level, itemstack, 1, Direction.UP, Vec3.atBottomCenterOf(pos).add(-1, 2, 1));
                        break;
                    case 3:
                        DefaultDispenseItemBehavior.spawnItem(level, itemstack, 1, Direction.UP, Vec3.atBottomCenterOf(pos).add(-1, 2, -1));
                        break;
                    case 4:
                        DefaultDispenseItemBehavior.spawnItem(level, itemstack, 1, Direction.UP, Vec3.atBottomCenterOf(pos).add(1, 2, -1));
                        break;
                }
            }
        }
    }

    public void tick(Level world, BlockPos pos, BlockState state) {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof DiviningTableBlock) {

            if (this.getTrackedEntities().stream().allMatch(Entity::isRemoved) && !this.getTrackedEntities().isEmpty()) {
                enableCanDropLoot(true);
                if (level instanceof ServerLevel serverLevel && canDropLoot) {
                    for (int i = 0; i < Math.pow(1.2,difficulty)+3; i++) {
                        ejectReward(serverLevel, pos, ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Holyhell.MOD_ID, "spawners/divining_table/divining_table")));
                        if(i == (int)Math.pow(1.2,difficulty)+2){
                            enableCooldown(true);
                            this.getTrackedEntities().clear();
                        }
                    } 
                }
            }
            this.getTrackedEntities().removeIf(Objects::isNull);


        }

        if (timer >= 1 && timer <= 1501) {
            timer++;
        }
        if (world instanceof ServerLevel) {
            if (timer == 0) {
                ((ServerLevel) world).sendParticles(HolyhellParticles.EYE3.get(), pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
            }
            if (timer == 2) {
                ((ServerLevel) world).sendParticles(HolyhellParticles.EYE0.get(), pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
            }
            if (timer == 501) {
                ((ServerLevel) world).sendParticles(HolyhellParticles.EYE1.get(), pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
            }
            if (timer == 1001) {
                ((ServerLevel) world).sendParticles(HolyhellParticles.EYE2.get(), pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
            }
            if (timer >= 1500) {
                ((ServerLevel) world).sendParticles(HolyhellParticles.EYE3.get(), pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
            }
        }
    }

    public int getTimer() {
        return timer;
    }

    public DiviningTableBlockEntity(BlockPos pos, BlockState state) {
        super(HolyHellBlockEntities.DIVINING_TABLE_BLOCK_ENTITY.get(), pos, state);
    }
}
