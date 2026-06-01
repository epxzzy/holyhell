package com.dead_comedian.holyhell.datagen;


import com.dead_comedian.holyhell.server.registries.HolyHellBlocks;
import com.dead_comedian.holyhell.server.registries.HolyHellItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class HolyhellRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public HolyhellRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        stairRecipe(HolyHellBlocks.MARBLE_STAIRS.get(), HolyHellBlocks.MARBLE.get(), recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_SLAB.get(), HolyHellBlocks.MARBLE.get());
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_WALL.get(), HolyHellBlocks.MARBLE.get());

        twoByTwoPacker(recipeOutput, RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_BRICKS.get(), HolyHellBlocks.MARBLE.get());
        stairRecipe(HolyHellBlocks.MARBLE_BRICK_STAIRS.get(), HolyHellBlocks.MARBLE_BRICKS.get(), recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_BRICK_SLAB.get(), HolyHellBlocks.MARBLE_BRICKS.get());
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_BRICK_WALL.get(), HolyHellBlocks.MARBLE_BRICKS.get());

        smeltingResultFromBase(recipeOutput, HolyHellBlocks.CRACKED_MARBLE_BRICKS.get(), HolyHellBlocks.MARBLE_BRICKS.get());
        stairRecipe(HolyHellBlocks.CRACKED_MARBLE_BRICK_STAIRS.get(), HolyHellBlocks.CRACKED_MARBLE_BRICKS.get(), recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_SLAB.get(), HolyHellBlocks.CRACKED_MARBLE_BRICKS.get());
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_WALL.get(), HolyHellBlocks.CRACKED_MARBLE_BRICKS.get());

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HolyHellBlocks.MARBLE_column.get())
                .pattern("B")
                .pattern("B")
                .define('B', HolyHellBlocks.MARBLE_SLAB.get())
                .unlockedBy("has_ingredient", has(HolyHellBlocks.MARBLE_SLAB.get())).save(recipeOutput);

        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_SLAB.get(),HolyHellBlocks.MARBLE.get() , 2);
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_STAIRS.get(),HolyHellBlocks.MARBLE.get());
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_WALL.get(),HolyHellBlocks.MARBLE   .get());


        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_BRICKS.get(),HolyHellBlocks.MARBLE.get());
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_BRICK_SLAB.get(),HolyHellBlocks.MARBLE.get(),2);
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_BRICK_STAIRS.get(),HolyHellBlocks.MARBLE.get());
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_BRICK_WALL.get(),HolyHellBlocks.MARBLE.get());

        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICKS.get(),HolyHellBlocks.MARBLE.get());
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_SLAB.get(),HolyHellBlocks.MARBLE.get(),2);
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_STAIRS.get(),HolyHellBlocks.MARBLE.get());
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_WALL.get(),HolyHellBlocks.MARBLE.get());

        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_column.get(),HolyHellBlocks.MARBLE.get());


        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_BRICK_SLAB.get(),HolyHellBlocks.MARBLE_BRICKS.get(),2);
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_BRICK_STAIRS.get(),HolyHellBlocks.MARBLE_BRICKS.get());
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.MARBLE_BRICK_WALL.get(),HolyHellBlocks.MARBLE_BRICKS.get());
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICKS.get(),HolyHellBlocks.MARBLE_BRICKS.get());
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_SLAB.get(),HolyHellBlocks.MARBLE_BRICKS.get(),2);
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_STAIRS.get(),HolyHellBlocks.MARBLE_BRICKS.get());
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_WALL.get(),HolyHellBlocks.MARBLE_BRICKS.get());

        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_SLAB.get(),HolyHellBlocks.CRACKED_MARBLE_BRICKS.get(),2);
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_STAIRS.get(),HolyHellBlocks.CRACKED_MARBLE_BRICKS.get());
        stonecutterResultFromBase(recipeOutput,RecipeCategory.BUILDING_BLOCKS, HolyHellBlocks.CRACKED_MARBLE_BRICK_WALL.get(),HolyHellBlocks.CRACKED_MARBLE_BRICKS.get());



        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HolyHellBlocks.TALL_CANDLEHOLDER.get())
                .pattern("BBB")
                .pattern("XBX")
                .pattern(" B ")
                .define('B', HolyHellItems.BAPTIZED_PLATE.get())
                .define('X', Blocks.CANDLE)
                .unlockedBy("has_ingredient", has(Blocks.CANDLE)).save(recipeOutput);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HolyHellBlocks.CHANDELIER.get())
                .pattern("XBX")
                .pattern("BBB")
                .define('B', HolyHellItems.BAPTIZED_PLATE.get())
                .define('X', Blocks.CANDLE)
                .unlockedBy("has_ingredient", has(Blocks.CANDLE)).save(recipeOutput);

    }

    //HELPERS
    private void stairRecipe(ItemLike result, ItemLike ingredient, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .pattern("B  ")
                .pattern("BB ")
                .pattern("BBB")
                .define('B', ingredient)
                .unlockedBy("has_ingredient", has(ingredient)).save(recipeOutput);
    }

}