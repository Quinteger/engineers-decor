/*
 * @file EdFloorGratingBlock.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Floor gratings.
 */
package wile.engineersdecor.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;



public class EdHatchBlock extends DecorBlock.HorizontalWaterLoggable implements IDecorBlock
{
  public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
  protected final ArrayList<VoxelShape> vshapes_open;

  public EdHatchBlock(long config, AbstractBlock.Properties builder, final AxisAlignedBB unrotatedAABBClosed, final AxisAlignedBB unrotatedAABBOpen)
  {
    super(config, builder, unrotatedAABBClosed); vshapes_open = makeHorizontalShapeLookup(new AxisAlignedBB[]{unrotatedAABBOpen});
    registerDefaultState(super.defaultBlockState().setValue(OPEN, false).setValue(POWERED, false));
  }

  public EdHatchBlock(long config, AbstractBlock.Properties builder, final AxisAlignedBB[] unrotatedAABBsClosed, final AxisAlignedBB[] unrotatedAABBsOpen)
  { super(config, builder, unrotatedAABBsClosed); vshapes_open = makeHorizontalShapeLookup(unrotatedAABBsOpen); }

  @Override
  public RenderTypeHint getRenderTypeHint()
  { return RenderTypeHint.CUTOUT; }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext selectionContext)
  { return state.getValue(OPEN) ? vshapes_open.get((state.getValue(HORIZONTAL_FACING)).get3DDataValue() & 0x7) : super.getShape(state, source, pos, selectionContext); }

  @Override
  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
  { return state.getValue(OPEN); }

  @Override
  @SuppressWarnings("deprecation")
  public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type)
  { return !state.getValue(OPEN); }

  @Override
  public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity)
  {
    if(!state.getValue(OPEN)) return false;
    {
      final BlockState up_state = world.getBlockState(pos.above());
      if(up_state.is(this) && (up_state.getValue(OPEN))) return true;
      if(up_state.isLadder(world, pos.above(), entity)) return true;
    }
    {
      final BlockState down_state = world.getBlockState(pos.below());
      if(down_state.is(this) && (down_state.getValue(OPEN))) return true;
      if(down_state.isLadder(world, pos.below(), entity)) return true;
    }
    return false;
  }

  @Override
  public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType)
  { return false; }

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
  { super.createBlockStateDefinition(builder); builder.add(OPEN, POWERED); }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
  {
    if(world.isClientSide()) return ActionResultType.SUCCESS;
    boolean open = !state.getValue(OPEN);
    world.setBlock(pos, state.setValue(OPEN, open), 1|2);
    world.playSound(null, pos, open?SoundEvents.IRON_DOOR_OPEN:SoundEvents.IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 0.7f, 1.4f);
    return ActionResultType.CONSUME;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
  {
    if((world.isClientSide) || (!(state.getBlock() instanceof EdHatchBlock))) return;
    boolean powered = world.hasNeighborSignal(pos);
    if(powered == state.getValue(POWERED)) return;
    if(powered != state.getValue(OPEN)) world.playSound(null, pos, powered?SoundEvents.IRON_DOOR_OPEN:SoundEvents.IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 0.7f, 1.4f);
    world.setBlock(pos, state.setValue(OPEN, powered).setValue(POWERED, powered), 1|2);
  }

  @Override
  public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side)
  { return false; }

  @Override
  @SuppressWarnings("deprecation")
  public void entityInside(BlockState state, World world, BlockPos pos, Entity entity)
  {
    if((!state.getValue(OPEN)) || (!(entity instanceof PlayerEntity))) return;
    final PlayerEntity player = (PlayerEntity)entity;
    if(entity.getLookAngle().y() > -0.75) return;
    if(player.getDirection() != state.getValue(HORIZONTAL_FACING)) return;
    Vector3d ppos = player.position();
    Vector3d centre = Vector3d.atBottomCenterOf(pos);
    Vector3d v = centre.subtract(ppos);
    if(ppos.y() < (centre.y()-0.1) || (v.lengthSqr() > 0.3)) return;
    v = v.scale(0.3);
    player.push(v.x, 0, v.z);
  }
}
