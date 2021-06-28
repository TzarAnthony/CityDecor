package com.tzaranthony.citydecor.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import java.util.Map;

public class CDWall extends WallBlock {
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final EnumProperty<WallHeight> EAST_WALL = BlockStateProperties.EAST_WALL;
    public static final EnumProperty<WallHeight> NORTH_WALL = BlockStateProperties.NORTH_WALL;
    public static final EnumProperty<WallHeight> SOUTH_WALL = BlockStateProperties.SOUTH_WALL;
    public static final EnumProperty<WallHeight> WEST_WALL = BlockStateProperties.WEST_WALL;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Map<BlockState, VoxelShape> shapeByIndex;
    private final Map<BlockState, VoxelShape> collisionShapeByIndex;
    private static final VoxelShape POST_TEST = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape NORTH_TEST = Block.box(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape SOUTH_TEST = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_TEST = Block.box(0.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape EAST_TEST = Block.box(7.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);

    public CDWall(String name, AbstractBlock block) {
        super(AbstractBlock.Properties.copy(block));
        this.setRegistryName(name);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UP, Boolean.valueOf(true))
                .setValue(NORTH_WALL, WallHeight.NONE)
                .setValue(EAST_WALL, WallHeight.NONE)
                .setValue(SOUTH_WALL, WallHeight.NONE)
                .setValue(WEST_WALL, WallHeight.NONE)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
        this.shapeByIndex = this.makeShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
        this.collisionShapeByIndex = this.makeShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
    }

    private static VoxelShape applyWallShape(VoxelShape p_235631_0_, WallHeight p_235631_1_, VoxelShape p_235631_2_, VoxelShape p_235631_3_) {
        if (p_235631_1_ == WallHeight.TALL) {
            return VoxelShapes.or(p_235631_0_, p_235631_3_);
        } else {
            return p_235631_1_ == WallHeight.LOW ? VoxelShapes.or(p_235631_0_, p_235631_2_) : p_235631_0_;
        }
    }

    private Map<BlockState, VoxelShape> makeShapes(float p_235624_1_, float p_235624_2_, float p_235624_3_, float p_235624_4_, float p_235624_5_, float p_235624_6_) {
        float f = 8.0F - p_235624_1_;
        float f1 = 8.0F + p_235624_1_;
        float f2 = 8.0F - p_235624_2_;
        float f3 = 8.0F + p_235624_2_;
        VoxelShape voxelshape = Block.box((double) f, 0.0D, (double) f, (double) f1, (double) p_235624_3_, (double) f1);
        VoxelShape voxelshape1 = Block.box((double) f2, (double) p_235624_4_, 0.0D, (double) f3, (double) p_235624_5_, (double) f3);
        VoxelShape voxelshape2 = Block.box((double) f2, (double) p_235624_4_, (double) f2, (double) f3, (double) p_235624_5_, 16.0D);
        VoxelShape voxelshape3 = Block.box(0.0D, (double) p_235624_4_, (double) f2, (double) f3, (double) p_235624_5_, (double) f3);
        VoxelShape voxelshape4 = Block.box((double) f2, (double) p_235624_4_, (double) f2, 16.0D, (double) p_235624_5_, (double) f3);
        VoxelShape voxelshape5 = Block.box((double) f2, (double) p_235624_4_, 0.0D, (double) f3, (double) p_235624_6_, (double) f3);
        VoxelShape voxelshape6 = Block.box((double) f2, (double) p_235624_4_, (double) f2, (double) f3, (double) p_235624_6_, 16.0D);
        VoxelShape voxelshape7 = Block.box(0.0D, (double) p_235624_4_, (double) f2, (double) f3, (double) p_235624_6_, (double) f3);
        VoxelShape voxelshape8 = Block.box((double) f2, (double) p_235624_4_, (double) f2, 16.0D, (double) p_235624_6_, (double) f3);
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (Boolean obool : UP.getPossibleValues()) {
            for (WallHeight wallheight : EAST_WALL.getPossibleValues()) {
                for (WallHeight wallheight1 : NORTH_WALL.getPossibleValues()) {
                    for (WallHeight wallheight2 : WEST_WALL.getPossibleValues()) {
                        for (WallHeight wallheight3 : SOUTH_WALL.getPossibleValues()) {
                            VoxelShape voxelshape9 = VoxelShapes.empty();
                            voxelshape9 = applyWallShape(voxelshape9, wallheight, voxelshape4, voxelshape8);
                            voxelshape9 = applyWallShape(voxelshape9, wallheight2, voxelshape3, voxelshape7);
                            voxelshape9 = applyWallShape(voxelshape9, wallheight1, voxelshape1, voxelshape5);
                            voxelshape9 = applyWallShape(voxelshape9, wallheight3, voxelshape2, voxelshape6);
                            if (obool) {
                                voxelshape9 = VoxelShapes.or(voxelshape9, voxelshape);
                            }

                            BlockState blockstate = this.defaultBlockState()
                                    .setValue(UP, obool)
                                    .setValue(EAST_WALL, wallheight)
                                    .setValue(WEST_WALL, wallheight2)
                                    .setValue(NORTH_WALL, wallheight1)
                                    .setValue(SOUTH_WALL, wallheight3);
                            builder.put(blockstate.setValue(WATERLOGGED, Boolean.valueOf(false)), voxelshape9);
                            builder.put(blockstate.setValue(WATERLOGGED, Boolean.valueOf(true)), voxelshape9);
                        }
                    }
                }
            }
        }

        return builder.build();
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.shapeByIndex.get(state);
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.collisionShapeByIndex.get(state);
    }

    public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    private boolean connectsTo(BlockState p_220113_1_, boolean p_220113_2_, Direction p_220113_3_) {
        Block block = p_220113_1_.getBlock();
        boolean flag = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(p_220113_1_, p_220113_3_);
        return p_220113_1_.is(BlockTags.WALLS) || !isExceptionForConnection(block) && p_220113_2_ || block instanceof PaneBlock || flag;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IWorldReader iworldreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.east();
        BlockPos blockpos3 = blockpos.south();
        BlockPos blockpos4 = blockpos.west();
        BlockPos blockpos5 = blockpos.above();
        BlockState blockstate = iworldreader.getBlockState(blockpos1);
        BlockState blockstate1 = iworldreader.getBlockState(blockpos2);
        BlockState blockstate2 = iworldreader.getBlockState(blockpos3);
        BlockState blockstate3 = iworldreader.getBlockState(blockpos4);
        BlockState blockstate4 = iworldreader.getBlockState(blockpos5);
        boolean flag = this.connectsTo(blockstate, blockstate.isFaceSturdy(iworldreader, blockpos1, Direction.SOUTH), Direction.SOUTH);
        boolean flag1 = this.connectsTo(blockstate1, blockstate1.isFaceSturdy(iworldreader, blockpos2, Direction.WEST), Direction.WEST);
        boolean flag2 = this.connectsTo(blockstate2, blockstate2.isFaceSturdy(iworldreader, blockpos3, Direction.NORTH), Direction.NORTH);
        boolean flag3 = this.connectsTo(blockstate3, blockstate3.isFaceSturdy(iworldreader, blockpos4, Direction.EAST), Direction.EAST);
        BlockState blockstate5 = this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
        return this.updateShape(iworldreader, blockstate5, blockpos5, blockstate4, flag, flag1, flag2, flag3);
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        if (facing == Direction.DOWN) {
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        } else {
            return facing == Direction.UP ? this.topUpdate(worldIn, stateIn, facingPos, facingState)
                    : this.sideUpdate(worldIn, currentPos, stateIn, facingPos, facingState, facing);
        }
    }

    private static boolean isConnected(BlockState p_235629_0_, Property<WallHeight> p_235629_1_) {
        return p_235629_0_.getValue(p_235629_1_) != WallHeight.NONE;
    }

    private static boolean isCovered(VoxelShape p_235632_0_, VoxelShape p_235632_1_) {
        return !VoxelShapes.joinIsNotEmpty(p_235632_1_, p_235632_0_, IBooleanFunction.ONLY_FIRST);
    }

    private BlockState topUpdate(IWorldReader p_235625_1_, BlockState p_235625_2_, BlockPos p_235625_3_, BlockState p_235625_4_) {
        boolean flag = isConnected(p_235625_2_, NORTH_WALL);
        boolean flag1 = isConnected(p_235625_2_, EAST_WALL);
        boolean flag2 = isConnected(p_235625_2_, SOUTH_WALL);
        boolean flag3 = isConnected(p_235625_2_, WEST_WALL);
        return this.updateShape(p_235625_1_, p_235625_2_, p_235625_3_, p_235625_4_, flag, flag1, flag2, flag3);
    }

    private BlockState sideUpdate(IWorldReader p_235627_1_, BlockPos p_235627_2_, BlockState p_235627_3_, BlockPos p_235627_4_, BlockState p_235627_5_, Direction p_235627_6_) {
        Direction direction = p_235627_6_.getOpposite();
        boolean flag = p_235627_6_ == Direction.NORTH ? this.connectsTo(p_235627_5_, p_235627_5_.isFaceSturdy(p_235627_1_, p_235627_4_, direction), direction)
                : isConnected(p_235627_3_, NORTH_WALL);
        boolean flag1 = p_235627_6_ == Direction.EAST ? this.connectsTo(p_235627_5_, p_235627_5_.isFaceSturdy(p_235627_1_, p_235627_4_, direction), direction)
                : isConnected(p_235627_3_, EAST_WALL);
        boolean flag2 = p_235627_6_ == Direction.SOUTH ? this.connectsTo(p_235627_5_, p_235627_5_.isFaceSturdy(p_235627_1_, p_235627_4_, direction), direction)
                : isConnected(p_235627_3_, SOUTH_WALL);
        boolean flag3 = p_235627_6_ == Direction.WEST ? this.connectsTo(p_235627_5_, p_235627_5_.isFaceSturdy(p_235627_1_, p_235627_4_, direction), direction)
                : isConnected(p_235627_3_, WEST_WALL);
        BlockPos blockpos = p_235627_2_.above();
        BlockState blockstate = p_235627_1_.getBlockState(blockpos);
        return this.updateShape(p_235627_1_, p_235627_3_, blockpos, blockstate, flag, flag1, flag2, flag3);
    }

    private BlockState updateShape(IWorldReader p_235626_1_, BlockState p_235626_2_, BlockPos p_235626_3_, BlockState p_235626_4_, boolean p_235626_5_, boolean p_235626_6_, boolean p_235626_7_, boolean p_235626_8_) {
        VoxelShape voxelshape = p_235626_4_.getCollisionShape(p_235626_1_, p_235626_3_).getFaceShape(Direction.DOWN);
        BlockState blockstate = this.updateSides(p_235626_2_, p_235626_5_, p_235626_6_, p_235626_7_, p_235626_8_, voxelshape);
        return blockstate.setValue(UP, Boolean.valueOf(this.shouldRaisePost(blockstate, p_235626_4_, voxelshape)));
    }

    private boolean shouldRaisePost(BlockState p_235628_1_, BlockState p_235628_2_, VoxelShape p_235628_3_) {
        boolean flag = p_235628_2_.getBlock() instanceof WallBlock && p_235628_2_.getValue(UP);
        if (flag) {
            return true;
        } else {
            WallHeight wallheight = p_235628_1_.getValue(NORTH_WALL);
            WallHeight wallheight1 = p_235628_1_.getValue(SOUTH_WALL);
            WallHeight wallheight2 = p_235628_1_.getValue(EAST_WALL);
            WallHeight wallheight3 = p_235628_1_.getValue(WEST_WALL);
            boolean flag1 = wallheight1 == WallHeight.NONE;
            boolean flag2 = wallheight3 == WallHeight.NONE;
            boolean flag3 = wallheight2 == WallHeight.NONE;
            boolean flag4 = wallheight == WallHeight.NONE;
            boolean flag5 = flag4 && flag1 && flag2 && flag3 || flag4 != flag1 || flag2 != flag3;
            if (flag5) {
                return true;
            } else {
                boolean flag6 = wallheight == WallHeight.TALL && wallheight1 == WallHeight.TALL || wallheight2 == WallHeight.TALL && wallheight3 == WallHeight.TALL;
                if (flag6) {
                    return false;
                } else {
                    return p_235628_2_.getBlock().is(BlockTags.WALL_POST_OVERRIDE) || isCovered(p_235628_3_, POST_TEST);
                }
            }
        }
    }

    private BlockState updateSides(BlockState p_235630_1_, boolean p_235630_2_, boolean p_235630_3_, boolean p_235630_4_, boolean p_235630_5_, VoxelShape p_235630_6_) {
        return p_235630_1_
                .setValue(NORTH_WALL, this.makeWallState(p_235630_2_, p_235630_6_, NORTH_TEST))
                .setValue(EAST_WALL, this.makeWallState(p_235630_3_, p_235630_6_, EAST_TEST))
                .setValue(SOUTH_WALL, this.makeWallState(p_235630_4_, p_235630_6_, SOUTH_TEST))
                .setValue(WEST_WALL, this.makeWallState(p_235630_5_, p_235630_6_, WEST_TEST));
    }

    private WallHeight makeWallState(boolean p_235633_1_, VoxelShape p_235633_2_, VoxelShape p_235633_3_) {
        if (p_235633_1_) {
            return isCovered(p_235633_2_, p_235633_3_) ? WallHeight.TALL : WallHeight.LOW;
        } else {
            return WallHeight.NONE;
        }
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH_WALL, EAST_WALL, WEST_WALL, SOUTH_WALL, WATERLOGGED);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180:
                return state.setValue(NORTH_WALL, state.getValue(SOUTH_WALL)).setValue(EAST_WALL,
                        state.getValue(WEST_WALL)).setValue(SOUTH_WALL, state.getValue(NORTH_WALL)).setValue(WEST_WALL, state.getValue(EAST_WALL));
            case COUNTERCLOCKWISE_90:
                return state.setValue(NORTH_WALL, state.getValue(EAST_WALL)).setValue(EAST_WALL,
                        state.getValue(SOUTH_WALL)).setValue(SOUTH_WALL, state.getValue(WEST_WALL)).setValue(WEST_WALL, state.getValue(NORTH_WALL));
            case CLOCKWISE_90:
                return state.setValue(NORTH_WALL, state.getValue(WEST_WALL)).setValue(EAST_WALL,
                        state.getValue(NORTH_WALL)).setValue(SOUTH_WALL, state.getValue(EAST_WALL)).setValue(WEST_WALL, state.getValue(SOUTH_WALL));
            default:
                return state;
        }
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT:
                return state.setValue(NORTH_WALL, state.getValue(SOUTH_WALL)).setValue(SOUTH_WALL, state.getValue(NORTH_WALL));
            case FRONT_BACK:
                return state.setValue(EAST_WALL, state.getValue(WEST_WALL)).setValue(WEST_WALL, state.getValue(EAST_WALL));
            default:
                return super.mirror(state, mirrorIn);
        }
    }
}
