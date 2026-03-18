package com.dead_comedian.holyhell.server.block.entity;

import com.dead_comedian.holyhell.HolyHell;
import com.dead_comedian.holyhell.server.block.DiviningTableBlock;
import com.dead_comedian.holyhell.server.entity.BabOneEntity;
import com.dead_comedian.holyhell.server.helper.WaveSpawner;
import com.dead_comedian.holyhell.server.registries.HolyHellBlockEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellParticles;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
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

    public int timer;

    private int difficulty;
    private int signalPower = 0;

    private boolean cooldown = false;
    private boolean canDropLoot = false;
    private boolean readyToSpawn = false;

    private List<Entity> trackedEntities = new ArrayList<>();

    public DiviningTableBlockEntity(BlockPos pos, BlockState state) {
        super(HolyHellBlockEntities.DIVINING_TABLE_BLOCK_ENTITY.get(), pos, state);
        timer = 1500;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public List<Entity> getTrackedEntities() {
        return trackedEntities;
    }

    public void setTrackedEntities(List<Entity> trackedEntities) {
        this.trackedEntities = trackedEntities;
    }

    public boolean getReadyToSpawn() {
        return readyToSpawn;
    }

    public void enableCooldown() {
        canDropLoot = false;
        readyToSpawn = false;
        cooldown = true;
        timer = 0;
    }

    public void enableReadyToSpawn() {
        canDropLoot = false;
        cooldown = false;
        readyToSpawn = true;
    }

    public void enableCanDropLoot() {
        cooldown = false;
        readyToSpawn = false;
        canDropLoot = true;
    }

    protected boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) {
        return this.getInputSignal(level, pos, state) > 0 && this.getReadyToSpawn();
    }

    protected int getInputSignal(Level level, BlockPos pos, BlockState state) {

        Direction direction = state.getValue(DiviningTableBlock.FACING);

        BlockPos blockpos = pos.relative(direction);

        signalPower = level.getSignal(blockpos, direction);

        if (signalPower >= 15) {
            return signalPower;
        }

        BlockState blockstate = level.getBlockState(blockpos);

        return Math.max(signalPower,
                blockstate.is(Blocks.REDSTONE_WIRE)
                        ? blockstate.getValue(RedStoneWireBlock.POWER)
                        : 0);
    }

    public void tick(Level world, BlockPos pos, BlockState state) {

        if (world.isClientSide) return;

        if (this.shouldTurnOn(world, pos, state)) {

            if (world instanceof ServerLevel server) {

                difficulty = signalPower * 2;

                List<Entity> wave = WaveSpawner.spawnWave(server, pos, difficulty);

                setTrackedEntities(wave);

                enableCooldown();
            }
        }

        Block block = world.getBlockState(pos).getBlock();

        if (block instanceof DiviningTableBlock) {

            if (this.getTrackedEntities().stream().allMatch(Entity::isRemoved)
                    && !this.getTrackedEntities().isEmpty()) {

                enableCanDropLoot();

                if (world instanceof ServerLevel serverLevel && canDropLoot && !world.getDifficulty().equals(Difficulty.PEACEFUL)) {

                    int rolls = (int) (3 + 4 * Math.log1p(difficulty));

                    for (int i = 0; i < rolls; i++) {

                        ejectReward(serverLevel, pos,
                                ResourceKey.create(
                                        Registries.LOOT_TABLE,
                                        ResourceLocation.fromNamespaceAndPath(
                                                HolyHell.MOD_ID,
                                                "spawners/divining_table/divining_table"
                                        )
                                ));

                        if (i == rolls - 1) {

                            enableCooldown();

                            BabOneEntity boss =
                                    new BabOneEntity(HolyHellEntities.BAB_ONE.get(), world);

                            world.addFreshEntity(boss);

                            boss.moveTo(pos.above(), 0, 0);

                            this.getTrackedEntities().clear();
                        }
                    }
                }
            }

            this.getTrackedEntities().removeIf(Objects::isNull);
        }

        if (cooldown) timer++;

        if (world instanceof ServerLevel server) {

            if (timer == 0)
                server.sendParticles(HolyHellParticles.EYE3.get(),
                        pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        1, 0, 0, 0, 0);

            if (timer == 2)
                server.sendParticles(HolyHellParticles.EYE0.get(),
                        pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        1, 0, 0, 0, 0);

            if (timer == 501)
                server.sendParticles(HolyHellParticles.EYE1.get(),
                        pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        1, 0, 0, 0, 0);

            if (timer == 1001)
                server.sendParticles(HolyHellParticles.EYE2.get(),
                        pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        1, 0, 0, 0, 0);

            if (timer >= 1500) {

                server.sendParticles(HolyHellParticles.EYE3.get(),
                        pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        1, 0, 0, 0, 0);

                enableReadyToSpawn();
            }
        }
    }

    public void ejectReward(ServerLevel level, BlockPos pos, ResourceKey<LootTable> lootTable) {

        LootTable loottable = level.getServer()
                .reloadableRegistries()
                .getLootTable(lootTable);

        LootParams lootparams =
                new LootParams.Builder(level)
                        .create(LootContextParamSets.EMPTY);

        ObjectArrayList<ItemStack> items = loottable.getRandomItems(lootparams);

        for (ItemStack stack : items) {

            DefaultDispenseItemBehavior.spawnItem(
                    level,
                    stack,
                    1,
                    Direction.UP,
                    Vec3.atBottomCenterOf(pos).add(0, 2, 0)
            );
        }
    }
}
