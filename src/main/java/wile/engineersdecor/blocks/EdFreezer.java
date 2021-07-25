/*
 * @file EdFreezer.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Small highly insulated stone liquification furnace
 * (magmatic phase).
 */
package wile.engineersdecor.blocks;

import net.minecraft.world.IWorldReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.*;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import wile.engineersdecor.ModConfig;
import wile.engineersdecor.libmc.detail.Fluidics;
import wile.engineersdecor.ModContent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class EdFreezer
{
  public static void on_config(int consumption, int cooldown_per_second)
  { FreezerTileEntity.on_config(consumption, cooldown_per_second); }

  //--------------------------------------------------------------------------------------------------------------------
  // Block
  //--------------------------------------------------------------------------------------------------------------------

  public static class FreezerBlock extends DecorBlock.Horizontal implements IDecorBlock
  {
    public static final int PHASE_MAX = 4;
    public static final IntegerProperty PHASE = IntegerProperty.create("phase", 0, PHASE_MAX);

    public FreezerBlock(long config, AbstractBlock.Properties builder, final AxisAlignedBB unrotatedAABB)
    { super(config, builder, unrotatedAABB); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    { super.createBlockStateDefinition(builder); builder.add(PHASE); }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    { return super.getStateForPlacement(context).setValue(PHASE, 0); }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState state)
    { return true; }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos)
    { return MathHelper.clamp((state.getValue(PHASE)*4), 0, 15); }

    @Override
    public boolean hasTileEntity(BlockState state)
    { return true; }

    @Override
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    { return new EdFreezer.FreezerTileEntity(); }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {}

    @Override
    public boolean hasDynamicDropList()
    { return true; }

    @Override
    public List<ItemStack> dropList(BlockState state, World world, TileEntity te, boolean explosion)
    {
      final List<ItemStack> stacks = new ArrayList<ItemStack>();
      if(world.isClientSide) return stacks;
      if(!(te instanceof FreezerTileEntity)) return stacks;
      ((FreezerTileEntity)te).reset_process();
      stacks.add(new ItemStack(this, 1));
      return stacks;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
    {
      if(player.isShiftKeyDown()) return ActionResultType.PASS;
      if(world.isClientSide()) return ActionResultType.SUCCESS;
      FreezerTileEntity te = getTe(world, pos);
      if(te==null) return ActionResultType.FAIL;
      final ItemStack stack = player.getItemInHand(hand);
      boolean dirty = false;
      if(Fluidics.manualFluidHandlerInteraction(world, pos, null, player, hand)) {
        world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundCategory.BLOCKS, 0.5f, 1.4f);
        return ActionResultType.CONSUME;
      }
      if(stack.getItem()==Items.WATER_BUCKET) {
        return ActionResultType.CONSUME; // would be already handled
      } else if(stack.isEmpty()) {
        ItemStack ice = te.getIceItem(true);
        if(!ice.isEmpty()) {
          player.addItem(ice);
          world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundCategory.BLOCKS, 0.3f, 1.1f);
        } else {
          world.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 0.2f, 0.02f);
        }
        return ActionResultType.CONSUME;
      } else {
        return ActionResultType.PASS;
      }
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side)
    { return false; }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rnd)
    {}

    @Nullable
    private FreezerTileEntity getTe(World world, BlockPos pos)
    { final TileEntity te=world.getBlockEntity(pos); return (!(te instanceof FreezerTileEntity)) ? (null) : ((FreezerTileEntity)te); }
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Tile entity
  //--------------------------------------------------------------------------------------------------------------------

  public static class FreezerTileEntity extends TileEntity implements ITickableTileEntity, IEnergyStorage, ICapabilityProvider
  {
    public static final int TICK_INTERVAL = 20;
    public static final int MAX_FLUID_LEVEL = 2000;
    public static final int MAX_ENERGY_BUFFER = 32000;
    public static final int MAX_ENERGY_TRANSFER = 8192;
    public static final int TANK_CAPACITY = 2000;
    public static final int DEFAULT_ENERGY_CONSUMPTION = 92;
    public static final int DEFAULT_COOLDOWN_RATE = 2;
    public static final int PHASE_EMPTY = 0;
    public static final int PHASE_WATER = 1;
    public static final int PHASE_ICE = 2;
    public static final int PHASE_PACKEDICE = 3;
    public static final int PHASE_BLUEICE = 4;

    private static int energy_consumption = DEFAULT_ENERGY_CONSUMPTION;
    private static int cooldown_rate = DEFAULT_COOLDOWN_RATE;
    private static int reheat_rate = 1;
    private final Fluidics.Tank tank_ = new Fluidics.Tank(TANK_CAPACITY, TANK_CAPACITY, TANK_CAPACITY, fs->fs.getFluid()==Fluids.WATER);
    private int tick_timer_;
    private int energy_stored_;
    private int progress_;
    private boolean force_block_update_;

    public static void on_config(int consumption, int cooldown_per_second)
    {
      energy_consumption = MathHelper.clamp(consumption, 8, 4096);
      cooldown_rate = MathHelper.clamp(cooldown_per_second, 1, 5);
      reheat_rate = MathHelper.clamp(cooldown_per_second/2, 1, 5);
      ModConfig.log("Config freezer energy consumption:" + energy_consumption + "rf/t, cooldown-rate: " + cooldown_rate + "%/s.");
    }

    public FreezerTileEntity()
    { this(ModContent.TET_FREEZER); }

    public FreezerTileEntity(TileEntityType<?> te_type)
    { super(te_type); }

    public int progress()
    { return progress_; }

    public int phase()
    {
      if(tank_.getFluidAmount() < 1000) return PHASE_EMPTY;
      if(progress_ >= 100) return PHASE_BLUEICE;
      if(progress_ >=  70) return PHASE_PACKEDICE;
      if(progress_ >=  30) return PHASE_ICE;
      return PHASE_WATER;
    }

    public ItemStack getIceItem(boolean extract)
    {
      ItemStack stack;
      switch(phase()) {
        case PHASE_ICE: stack = new ItemStack(Items.ICE); break;
        case PHASE_PACKEDICE: stack = new ItemStack(Items.PACKED_ICE); break;
        case PHASE_BLUEICE: stack = new ItemStack(Items.BLUE_ICE); break;
        default: return ItemStack.EMPTY;
      }
      if(extract) reset_process();
      return stack;
    }

    public int comparator_signal()
    { return phase() * 4; }

    protected void reset_process()
    {
      force_block_update_ = true;
      tank_.drain(1000);
      tick_timer_ = 0;
      progress_ = 0;
    }

    public void readnbt(CompoundNBT nbt)
    {
      energy_stored_ = nbt.getInt("energy");
      progress_ = nbt.getInt("progress");
      tank_.load(nbt);
    }

    protected void writenbt(CompoundNBT nbt)
    {
      nbt.putInt("energy", MathHelper.clamp(energy_stored_,0 , MAX_ENERGY_BUFFER));
      nbt.putInt("progress", MathHelper.clamp(progress_,0 , 100));
      tank_.save(nbt);
    }

    // TileEntity ------------------------------------------------------------------------------

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    { super.load(state, nbt); readnbt(nbt); }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    { super.save(nbt); writenbt(nbt); return nbt; }

    @Override
    public void setRemoved()
    {
      super.setRemoved();
      energy_handler_.invalidate();
      fluid_handler_.invalidate();
      item_handler_.invalidate();
    }

    // IItemHandler  --------------------------------------------------------------------------------

    private final LazyOptional<IItemHandler> item_handler_ = LazyOptional.of(() -> (IItemHandler)new FreezerItemHandler(this));

    protected static class FreezerItemHandler implements IItemHandler
    {
      private final FreezerTileEntity te;

      FreezerItemHandler(FreezerTileEntity te)
      { this.te = te; }

      @Override
      public int getSlots()
      { return 1; }

      @Override
      public int getSlotLimit(int index)
      { return 1; }

      @Override
      public boolean isItemValid(int slot, @Nonnull ItemStack stack)
      { return false; }

      @Override
      @Nonnull
      public ItemStack getStackInSlot(int index)
      { return (index!=0) ? ItemStack.EMPTY : te.getIceItem(false); }

      @Override
      @Nonnull
      public ItemStack insertItem(int index, @Nonnull ItemStack stack, boolean simulate)
      { return ItemStack.EMPTY; }

      @Override
      @Nonnull
      public ItemStack extractItem(int index, int amount, boolean simulate)
      { return te.getIceItem(!simulate); }
    }

    // IFluidHandler  --------------------------------------------------------------------------------

    private final LazyOptional<IFluidHandler> fluid_handler_ = LazyOptional.of(() -> new Fluidics.SingleTankFluidHandler(tank_));

    // IEnergyStorage ----------------------------------------------------------------------------

    protected LazyOptional<IEnergyStorage> energy_handler_ = LazyOptional.of(() -> (IEnergyStorage)this);

    @Override
    public boolean canExtract()
    { return false; }

    @Override
    public boolean canReceive()
    { return true; }

    @Override
    public int getMaxEnergyStored()
    { return MAX_ENERGY_BUFFER; }

    @Override
    public int getEnergyStored()
    { return energy_stored_; }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    { return 0; }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
      if(energy_stored_ >= MAX_ENERGY_BUFFER) return 0;
      int n = Math.min(maxReceive, (MAX_ENERGY_BUFFER - energy_stored_));
      if(n > MAX_ENERGY_TRANSFER) n = MAX_ENERGY_TRANSFER;
      if(!simulate) {energy_stored_ += n; setChanged(); }
      return n;
    }

    // Capability export ----------------------------------------------------------------------------

    @Override
    public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing)
    {
      if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return item_handler_.cast();
      if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return fluid_handler_.cast();
      if(capability == CapabilityEnergy.ENERGY) return energy_handler_.cast();
      return super.getCapability(capability, facing);
    }

    // ITickable ------------------------------------------------------------------------------------

    @Override
    public void tick()
    {
      if(level.isClientSide) return;
      if(--tick_timer_ > 0) return;
      tick_timer_ = TICK_INTERVAL;
      BlockState state = level.getBlockState(worldPosition);
      if(!(state.getBlock() instanceof FreezerBlock)) return;
      boolean dirty = false;
      final int last_phase = phase();
      if(tank_.getFluidAmount() < 1000) {
        progress_ = 0;
      } else if((energy_stored_ <= 0) || (level.hasNeighborSignal(worldPosition))) {
        progress_ = MathHelper.clamp(progress_-reheat_rate, 0,100);
      } else if(progress_ >= 100) {
        progress_ = 100;
        energy_stored_ = MathHelper.clamp(energy_stored_-((energy_consumption*TICK_INTERVAL)/20), 0, MAX_ENERGY_BUFFER);
      } else {
        energy_stored_ = MathHelper.clamp(energy_stored_-(energy_consumption*TICK_INTERVAL), 0, MAX_ENERGY_BUFFER);
        progress_ = MathHelper.clamp(progress_+cooldown_rate, 0, 100);
      }
      int new_phase = phase();
      if(new_phase > last_phase) {
        level.playSound(null, worldPosition, SoundEvents.SAND_FALL, SoundCategory.BLOCKS, 0.2f, 0.7f);
      } else if(new_phase < last_phase) {
        level.playSound(null, worldPosition, SoundEvents.SAND_FALL, SoundCategory.BLOCKS, 0.2f, 0.7f);
      }
      // Block state
      if((force_block_update_ || (state.getValue(FreezerBlock.PHASE) != new_phase))) {
        state = state.setValue(FreezerBlock.PHASE, new_phase);
        level.setBlock(worldPosition, state,3|16);
        level.updateNeighborsAt(getBlockPos(), state.getBlock());
        force_block_update_ = false;
      }
      if(dirty) setChanged();
    }
  }
}
