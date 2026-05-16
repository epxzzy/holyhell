package com.dead_comedian.holyhell.server.block;

import com.dead_comedian.holyhell.server.block.entity.CoffinBlockEntity;
import com.dead_comedian.holyhell.server.registries.HolyHellAttachments;
import com.dead_comedian.holyhell.server.registries.HolyHellBlockEntities;
import com.dead_comedian.holyhell.server.registries.HolyHellSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CoffinBlock extends BaseEntityBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF = EnumProperty.create("half", DoubleBlockHalf.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public CoffinBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(ACTIVATED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HALF, FACING, ACTIVATED, OPEN);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        switch (state.getValue(FACING)) {
            case NORTH -> level.setBlock(pos.north(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
            case SOUTH -> level.setBlock(pos.south(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
            case EAST -> level.setBlock(pos.east(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
            case WEST -> level.setBlock(pos.west(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
        }
    }


    @Override
    public BlockState rotate(BlockState state, Rotation pRot) {
        return state.setValue(FACING, pRot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror pMirror) {
        return state.rotate(pMirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            switch (state.getValue(FACING)) {
                case NORTH -> level.destroyBlock(pos.north(), true);
                case SOUTH -> level.destroyBlock(pos.south(), true);
                case EAST -> level.destroyBlock(pos.east(), true);
                case WEST -> level.destroyBlock(pos.west(), true);
            }
        }
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            switch (state.getValue(FACING)) {
                case NORTH -> level.destroyBlock(pos.south(), true);
                case SOUTH -> level.destroyBlock(pos.north(), true);
                case EAST -> level.destroyBlock(pos.west(), true);
                case WEST -> level.destroyBlock(pos.east(), true);
            }
        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
    }

    @Nullable
    public BlockEntity getLowerBlockEntity(BlockPos pos, BlockState state, Level level) {
        switch (state.getValue(FACING)) {
            case NORTH -> {
                return level.getBlockEntity(pos.south());
            }
            case SOUTH -> {
                return level.getBlockEntity(pos.north());
            }
            case EAST -> {
                return level.getBlockEntity(pos.west());
            }
            case WEST -> {
                return level.getBlockEntity(pos.east());
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {


            if (stack.is(Items.GOLD_BLOCK) && !state.getValue(ACTIVATED)) {
                if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    level.setBlock(pos, state.setValue(CoffinBlock.ACTIVATED, true), 3);
                    player.setData(HolyHellAttachments.HAS_COFFIN, true);
                    if (level.getBlockEntity(pos) instanceof CoffinBlockEntity coffinBlockEntity) {
                        coffinBlockEntity.setStoredUUID(player.getUUID());
                    }
                } else if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                    level.setBlock(this.getLowerBlockEntity(pos, state, level).getBlockPos()
                            , this.getLowerBlockEntity(pos, state, level).getBlockState()
                                    .setValue(CoffinBlock.ACTIVATED, true), 3);

                    ((CoffinBlockEntity) (Object) this.getLowerBlockEntity(pos, state, level)).setStoredUUID(player.getUUID());
                }


                stack.consume(1, player);
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }


            if (stack.isEmpty()) {
                if (state.getValue(HALF) == DoubleBlockHalf.LOWER
                        && state.getValue(ACTIVATED)
                        && player.getData(HolyHellAttachments.HAS_COFFIN)
                        && level.getBlockEntity(pos) instanceof CoffinBlockEntity coffinBlockEntity
                        && coffinBlockEntity.getStoredUUID() != null) {


                    if (coffinBlockEntity.getStoredUUID().equals(player.getUUID())) {
                        level.playSound((Player) null, pos, HolyHellSounds.COFFIN_LID.get(), SoundSource.BLOCKS, 1, 1);
                        level.setBlock(pos, state.setValue(OPEN, true), 3);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide());


                } else if (state.getValue(HALF) == DoubleBlockHalf.LOWER
                        && player.getData(HolyHellAttachments.HAS_COFFIN)
                        && getLowerBlockEntity(pos, state, level) instanceof CoffinBlockEntity coffinBlockEntity) {


                    if (coffinBlockEntity.getStoredUUID().equals(player.getUUID())) {
                        System.out.println(player.getData(HolyHellAttachments.SAVED_INVENTORY));
                    }

                }
            }
        }
        return ItemInteractionResult.FAIL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) return new CoffinBlockEntity(pos, state);
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> pBlockEntityType) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, HolyHellBlockEntities.COFFIN_BLOCK_ENTITY.get(),
                (level1, pos, state1, pBlockEntity) -> pBlockEntity.tick(level1, pos, state1));
    }


    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER
                && level.getBlockEntity(pos) instanceof CoffinBlockEntity coffinBlockEntity
                && coffinBlockEntity.getStoredUUID() != null) {

            level.getPlayerByUUID((coffinBlockEntity.getStoredUUID()))
                    .setData(HolyHellAttachments.HAS_COFFIN, false);

        }
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER
                && getLowerBlockEntity(pos, state, (Level) level) instanceof CoffinBlockEntity coffinBlockEntity
                && coffinBlockEntity.getStoredUUID() != null) {

            level.getPlayerByUUID((coffinBlockEntity.getStoredUUID()))
                    .setData(HolyHellAttachments.HAS_COFFIN, false);

        }

    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
        super.playerDestroy(level, player, pos, state, pBlockEntity, pTool);
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state))) {
            preventDropFromBottomPart(level, pos, state, player);
        }
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER
                && level.getBlockEntity(pos) instanceof CoffinBlockEntity coffinBlockEntity
                && coffinBlockEntity.getStoredUUID() != null) {

            level.getPlayerByUUID((coffinBlockEntity.getStoredUUID()))
                    .setData(HolyHellAttachments.HAS_COFFIN, false);

        }
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER
                && getLowerBlockEntity(pos, state, (Level) level) instanceof CoffinBlockEntity coffinBlockEntity
                && coffinBlockEntity.getStoredUUID() != null) {

            level.getPlayerByUUID((coffinBlockEntity.getStoredUUID()))
                    .setData(HolyHellAttachments.HAS_COFFIN, false);

        }


    }

    protected static void preventDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.is(state.getBlock()) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState postDestroyState = belowState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(belowPos, postDestroyState, 35);
                level.levelEvent(player, 2001, belowPos, Block.getId(belowState));
            }
        }
    }

}