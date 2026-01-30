package com.dead_comedian.holyhell.datagen;


import com.dead_comedian.holyhell.Holyhell;
import com.dead_comedian.holyhell.server.registries.HolyHellBlocks;
import com.dead_comedian.holyhell.server.registries.HolyhellTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class HolyhellBlockTagProvider extends BlockTagsProvider {
    public HolyhellBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Holyhell.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(HolyHellBlocks.SKULL_PILE.get())
                .add(HolyHellBlocks.BONE_PILE.get())
                .add(HolyHellBlocks.BONE_CHANDELIER.get())
                .add(HolyHellBlocks.COFFIN.get())

                .add(HolyHellBlocks.CANDELABRA.get())
                .add(HolyHellBlocks.TALL_CANDELABRA.get())

                .add(HolyHellBlocks.CANDLEHOLDER.get())
                .add(HolyHellBlocks.TALL_CANDLEHOLDER.get())

                .add(HolyHellBlocks.CHANDELIER.get())

                .add(HolyHellBlocks.DIVINING_TABLE.get())
                .add(HolyHellBlocks.STONE_CROSS.get())
                .add(HolyHellBlocks.FALLING_CROSS.get())

                .add(HolyHellBlocks.COBBLED_MARBLE.get())
                .add(HolyHellBlocks.COBBLED_MARBLE_SLAB.get())
                .add(HolyHellBlocks.COBBLED_MARBLE_STAIRS.get())
                .add(HolyHellBlocks.COBBLED_MARBLE_WALL.get())

                .add(HolyHellBlocks.MARBLE.get())
                .add(HolyHellBlocks.MARBLE_SLAB.get())
                .add(HolyHellBlocks.MARBLE_STAIRS.get())
                .add(HolyHellBlocks.MARBLE_WALL.get())

                .add(HolyHellBlocks.MARBLE_BRICKS.get())
                .add(HolyHellBlocks.MARBLE_BRICK_SLAB.get())
                .add(HolyHellBlocks.MARBLE_BRICK_STAIRS.get())
                .add(HolyHellBlocks.MARBLE_BRICK_WALL.get())

                .add(HolyHellBlocks.CRACKED_MARBLE_BRICKS.get())
                .add(HolyHellBlocks.CRACKED_MARBLE_BRICK_SLAB.get())
                .add(HolyHellBlocks.CRACKED_MARBLE_BRICK_STAIRS.get())
                .add(HolyHellBlocks.CRACKED_MARBLE_BRICK_WALL.get())

                .add(HolyHellBlocks.MARBLE_COLLUMN.get())

                .add(HolyHellBlocks.ATLAS_STATUE.get())
                .add(HolyHellBlocks.BAPHOMET_STATUE.get())
                .add(HolyHellBlocks.DOOMSLAYER_STATUE.get())
                .add(HolyHellBlocks.ICARUS_STATUE.get())
                .add(HolyHellBlocks.KRATOS_STATUE.get())
                .add(HolyHellBlocks.V1_STATUE.get());


        tag(HolyhellTags.Blocks.MARBLE)
                .add(HolyHellBlocks.COBBLED_MARBLE.get())
                .add(HolyHellBlocks.COBBLED_MARBLE_SLAB.get())
                .add(HolyHellBlocks.COBBLED_MARBLE_STAIRS.get())
                .add(HolyHellBlocks.COBBLED_MARBLE_WALL.get())

                .add(HolyHellBlocks.MARBLE.get())
                .add(HolyHellBlocks.MARBLE_SLAB.get())
                .add(HolyHellBlocks.MARBLE_STAIRS.get())
                .add(HolyHellBlocks.MARBLE_WALL.get())

                .add(HolyHellBlocks.MARBLE_BRICKS.get())
                .add(HolyHellBlocks.MARBLE_BRICK_SLAB.get())
                .add(HolyHellBlocks.MARBLE_BRICK_STAIRS.get())
                .add(HolyHellBlocks.MARBLE_BRICK_WALL.get())

                .add(HolyHellBlocks.CRACKED_MARBLE_BRICKS.get())
                .add(HolyHellBlocks.CRACKED_MARBLE_BRICK_SLAB.get())
                .add(HolyHellBlocks.CRACKED_MARBLE_BRICK_STAIRS.get())
                .add(HolyHellBlocks.CRACKED_MARBLE_BRICK_WALL.get())

                .add(HolyHellBlocks.MARBLE_COLLUMN.get())

                .add(HolyHellBlocks.ATLAS_STATUE.get())
                .add(HolyHellBlocks.BAPHOMET_STATUE.get())
                .add(HolyHellBlocks.DOOMSLAYER_STATUE.get())
                .add(HolyHellBlocks.ICARUS_STATUE.get())
                .add(HolyHellBlocks.KRATOS_STATUE.get())
                .add(HolyHellBlocks.V1_STATUE.get());


        tag(HolyhellTags.Blocks.STATUE)
                .add(HolyHellBlocks.ATLAS_STATUE.get())
                .add(HolyHellBlocks.BAPHOMET_STATUE.get())
                .add(HolyHellBlocks.DOOMSLAYER_STATUE.get())
                .add(HolyHellBlocks.ICARUS_STATUE.get())
                .add(HolyHellBlocks.KRATOS_STATUE.get())
                .add(HolyHellBlocks.V1_STATUE.get());

        tag(HolyhellTags.Blocks.LIGHTING_BLOCKS)
                .add(HolyHellBlocks.CANDELABRA.get())
                .add(HolyHellBlocks.TALL_CANDELABRA.get())
                .add(HolyHellBlocks.CANDLEHOLDER.get())
                .add(HolyHellBlocks.TALL_CANDLEHOLDER.get())
                .add(HolyHellBlocks.CHANDELIER.get())
                .add(HolyHellBlocks.BONE_CHANDELIER.get());

    }
}