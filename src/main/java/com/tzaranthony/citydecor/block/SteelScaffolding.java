package com.tzaranthony.citydecor.block;

import com.tzaranthony.citydecor.util.CDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class SteelScaffolding extends CDScaffolding {
    public static final IntegerProperty DISTANCE = CDBlockStateProperties.DISTANCE_0_15;

    public SteelScaffolding(String name) {
        super();
        this.setRegistryName(name);
        this.stateDefinition.any()
                .setValue(DISTANCE, Integer.valueOf(15))
                .setValue(WATERLOGGED, Boolean.valueOf(false))
                .setValue(BOTTOM, Boolean.valueOf(false));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, WATERLOGGED, BOTTOM);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level world = context.getLevel();
        int i = SteelScaffolding.getDistance(world, blockpos);
        return this.defaultBlockState()
                .setValue(WATERLOGGED, Boolean.valueOf(world.getFluidState(blockpos).getType() == Fluids.WATER))
                .setValue(DISTANCE, Integer.valueOf(i))
                .setValue(BOTTOM, Boolean.valueOf(this.isBottom(world, blockpos, i)));
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        int i = SteelScaffolding.getDistance(worldIn, pos);
        BlockState blockstate = state.setValue(DISTANCE, Integer.valueOf(i)).setValue(BOTTOM, Boolean.valueOf(this.isBottom(worldIn, pos, i)));
        if (blockstate.getValue(DISTANCE) == 11) {
            if (state.getValue(DISTANCE) == 11) {
                FallingBlockEntity.fall(worldIn, pos, blockstate);
            } else {
                worldIn.destroyBlock(pos, true);
            }
        } else if (state != blockstate) {
            worldIn.setBlock(pos, blockstate, 3);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return SteelScaffolding.getDistance(worldIn, pos) < 15;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_56068_, BlockGetter p_56069_, BlockPos p_56070_, CollisionContext p_56071_) {
        if (p_56071_.isAbove(Shapes.block(), p_56070_, true) && !p_56071_.isDescending()) {
            return this.STABLE_SHAPE;
        } else {
            return p_56068_.getValue(DISTANCE) != 0 && p_56068_.getValue(BOTTOM) && p_56071_.isAbove(BELOW_BLOCK, p_56070_, true) ? UNSTABLE_SHAPE_BOTTOM : Shapes.empty();
        }
    }

    public static int getDistance(BlockGetter blockReader, BlockPos pos) {
        BlockPos.MutableBlockPos blockpos$mutable = pos.mutable().move(Direction.DOWN);
        BlockState blockstate = blockReader.getBlockState(blockpos$mutable);
        int i = 11;
        if (blockstate.is(CDBlocks.STEEL_SCAFFOLDING)) {
            i = blockstate.getValue(DISTANCE);
        } else if (blockstate.isFaceSturdy(blockReader, blockpos$mutable, Direction.UP)) {
            return 0;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockstate1 = blockReader.getBlockState(blockpos$mutable.setWithOffset(pos, direction));
            if (blockstate1.is(CDBlocks.STEEL_SCAFFOLDING)) {
                i = Math.min(i, blockstate1.getValue(DISTANCE) + 1);
                if (i == 1) {
                    break;
                }
            }
        }
        return i;
    }

    static {
        VoxelShape voxelshape = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VoxelShape voxelshape1 = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
        VoxelShape voxelshape2 = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
        VoxelShape voxelshape3 = Block.box(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
        VoxelShape voxelshape4 = Block.box(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
        STABLE_SHAPE = Shapes.or(voxelshape, voxelshape1, voxelshape2, voxelshape3, voxelshape4);
        VoxelShape voxelshape5 = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 16.0D);
        VoxelShape voxelshape6 = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
        VoxelShape voxelshape7 = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D);
        VoxelShape voxelshape8 = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D);
        UNSTABLE_SHAPE = Shapes.or(UNSTABLE_SHAPE_BOTTOM, STABLE_SHAPE, voxelshape6, voxelshape5, voxelshape8, voxelshape7);
    }
}