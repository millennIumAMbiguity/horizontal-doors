package millenniumambiguity.horizontaldoors.blocks;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

//import javax.annotation.Nullable;

public class HorizontalDoorBlock extends DoorBlock {

    protected static final VoxelShape VS_NORTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape VS_EAST = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape VS_SOUTH = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape VS_WEST = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    protected static final VoxelShape VS_DOWN = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape VS_UP = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

    public HorizontalDoorBlock(BlockSetType $$0, BlockBehaviour.Properties $$1) {
        super($$1, $$0);
    }

    public HorizontalDoorBlock(DoorBlock baseDoor) {
        super(Block.Properties.copy(baseDoor), baseDoor.type());
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        Direction $$4 = $$0.getValue(FACING);
        boolean $$5 = !$$0.getValue(OPEN);
        boolean $$6 = $$0.getValue(HINGE) == DoorHingeSide.RIGHT;

        return switch ($$4) {
            case SOUTH -> $$5 ? VS_UP : ($$6 ? VS_WEST : VS_EAST);
            case WEST -> $$5 ? VS_UP : ($$6 ? VS_NORTH : VS_SOUTH);
            case NORTH -> $$5 ? VS_UP : ($$6 ? VS_EAST : VS_WEST);
            default -> $$5 ? VS_UP : ($$6 ? VS_SOUTH : VS_NORTH);
        };
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        Direction dir = $$0.getValue(FACING);
        Direction dirInv = dir.getOpposite();
        DoubleBlockHalf $$6 = $$0.getValue(HALF);
        if ($$1.getAxis() != dir.getAxis() || $$6 == DoubleBlockHalf.LOWER != ($$1 == dir)) {
            return $$6 == DoubleBlockHalf.LOWER && $$1 == dirInv && !$$0.canSurvive($$3, $$4)
                    ? Blocks.AIR.defaultBlockState()
                    : $$0;
        } else {
            return $$2.getBlock() instanceof HorizontalDoorBlock && $$2.getValue(HALF) != $$6 ? $$2.setValue(HALF, $$6) : Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return switch ($$3) {
            case LAND, AIR -> !$$0.getValue(OPEN);
            case WATER -> false;
        };
    }

    //@Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockPos $$1 = $$0.getClickedPos();
        Level $$2 = $$0.getLevel();
        Direction dir = $$0.getClickedFace();

        if (dir == Direction.UP || dir == Direction.DOWN){
            dir = $$0.getHorizontalDirection();
        }

        // try all possible rotations clock wise.
        for (int i = 0; i < 4; i++){
            BlockPos relPos = relativeP($$1, dir);
            if ($$2.getBlockState(relPos).canBeReplaced($$0)) {
                boolean $$3 = $$2.hasNeighborSignal($$1) || $$2.hasNeighborSignal(relPos);
                return this.defaultBlockState()
                        .setValue(FACING, dir)
                        .setValue(HINGE, this.getHinge($$0, dir))
                        .setValue(POWERED, $$3)
                        .setValue(OPEN, $$3)
                        .setValue(HALF, DoubleBlockHalf.LOWER);
            }

            dir = dir.getClockWise();
        }

        return null;
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        BlockPos pos = relativeP($$1, $$2.getValue(FACING));
        $$0.setBlock(pos, $$2.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }
    private DoorHingeSide getHinge(BlockPlaceContext $$0, Direction dir) {
        BlockGetter $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        Direction $$3 = $$0.getHorizontalDirection();
        BlockPos $$4 = relativeP($$2, dir);
        Direction $$5 = $$3.getCounterClockWise();
        BlockPos $$6 = $$2.relative($$5);
        BlockState $$7 = $$1.getBlockState($$6);
        BlockPos $$8 = $$4.relative($$5);
        BlockState $$9 = $$1.getBlockState($$8);
        Direction $$10 = $$3.getClockWise();
        BlockPos $$11 = $$2.relative($$10);
        BlockState $$12 = $$1.getBlockState($$11);
        BlockPos $$13 = $$4.relative($$10);
        BlockState $$14 = $$1.getBlockState($$13);
        int $$15 = ($$7.isCollisionShapeFullBlock($$1, $$6) ? -1 : 0)
                + ($$9.isCollisionShapeFullBlock($$1, $$8) ? -1 : 0)
                + ($$12.isCollisionShapeFullBlock($$1, $$11) ? 1 : 0)
                + ($$14.isCollisionShapeFullBlock($$1, $$13) ? 1 : 0);
        boolean $$16 = $$7.is(this) && $$7.getValue(HALF) == DoubleBlockHalf.LOWER;
        if ($$15 > 0 || ($$16 && $$15 == 0)) {
            return DoorHingeSide.RIGHT;
        } else {
            return DoorHingeSide.LEFT;
        }
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        Direction dir = $$0.getValue(FACING);
        boolean $$6 = $$1.hasNeighborSignal($$2)
                || $$1.hasNeighborSignal($$2.relative($$0.getValue(HALF) == DoubleBlockHalf.LOWER ? dir : dir.getOpposite()));
        if (!this.defaultBlockState().is($$3) && $$6 != $$0.getValue(POWERED)) {
            if ($$6 != $$0.getValue(OPEN)) {
                this.playSound(null, $$1, $$2, $$6);
                $$1.gameEvent(null, $$6 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, $$2);
            }

            $$1.setBlock($$2, $$0.setValue(POWERED, $$6).setValue(OPEN, $$6), 2);
        }
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Direction dir = $$0.getValue(FACING);
        BlockPos $$3 = relativeN($$2, dir);
        BlockState $$4 = $$1.getBlockState($$3);
        return $$0.getValue(HALF) == DoubleBlockHalf.LOWER ? $$4.isFaceSturdy($$1, $$3, dir) : $$4.is(this);
    }

    private void playSound(/*@Nullable*/ Entity $$0, Level $$1, BlockPos $$2, boolean $$3) {
        $$1.playSound($$0, $$2, $$3 ? this.type().doorOpen() : this.type().doorClose(), SoundSource.BLOCKS, 1.0F, $$1.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    public BlockPos relativeP(BlockPos pos, Direction $$0) {
        return new BlockPos(pos.getX() + $$0.getStepX(), pos.getY(), pos.getZ() + $$0.getStepZ());
    }

    public BlockPos relativeN(BlockPos pos, Direction $$0) {
        return new BlockPos(pos.getX() - $$0.getStepX(), pos.getY(), pos.getZ() - $$0.getStepZ());
    }
}