/*
 * @file EdWasteIncinerator.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Trash/void/nullifier device with internal fifos.
 */
package wile.engineersdecor.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import wile.engineersdecor.ModConfig;
import wile.engineersdecor.ModContent;
import wile.engineersdecor.ModEngineersDecor;
import wile.engineersdecor.blocks.EdFurnace.FurnaceBlock;
import wile.engineersdecor.libmc.client.ContainerGui;
import wile.engineersdecor.libmc.detail.Inventories;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class EdWasteIncinerator
{
  public static void on_config(int boost_energy_per_tick)
  { WasteIncineratorTileEntity.on_config(boost_energy_per_tick); }

  //--------------------------------------------------------------------------------------------------------------------
  // Block
  //--------------------------------------------------------------------------------------------------------------------

  public static class WasteIncineratorBlock extends DecorBlock.Cutout implements IDecorBlock
  {
    public static final BooleanProperty LIT = FurnaceBlock.LIT;

    public WasteIncineratorBlock(long config, AbstractBlock.Properties builder, final AxisAlignedBB unrotatedAABB)
    { super(config, builder, unrotatedAABB); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    { super.createBlockStateDefinition(builder); builder.add(LIT); }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    { return super.getStateForPlacement(context).setValue(LIT, false); }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState state)
    { return true; }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(BlockState blockState, World world, BlockPos pos)
    { return Container.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos)); }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side)
    { return false; }

    @Override
    public boolean hasTileEntity(BlockState state)
    { return true; }

    @Override
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    { return new EdWasteIncinerator.WasteIncineratorTileEntity(); }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
      if(world.isClientSide) return;
      if((!stack.hasTag()) || (!stack.getTag().contains("tedata"))) return;
      CompoundNBT te_nbt = stack.getTag().getCompound("tedata");
      if(te_nbt.isEmpty()) return;
      final TileEntity te = world.getBlockEntity(pos);
      if(!(te instanceof EdWasteIncinerator.WasteIncineratorTileEntity)) return;
      ((EdWasteIncinerator.WasteIncineratorTileEntity)te).readnbt(te_nbt);
      ((EdWasteIncinerator.WasteIncineratorTileEntity)te).setChanged();
    }

    @Override
    public boolean hasDynamicDropList()
    { return true; }

    @Override
    public List<ItemStack> dropList(BlockState state, World world, final TileEntity te, boolean explosion)
    {
      final List<ItemStack> stacks = new ArrayList<ItemStack>();
      if(world.isClientSide) return stacks;
      if(!(te instanceof WasteIncineratorTileEntity)) return stacks;
      if(!explosion) {
        ItemStack stack = new ItemStack(this, 1);
        CompoundNBT te_nbt = ((WasteIncineratorTileEntity) te).getnbt();
        if(!te_nbt.isEmpty()) {
          CompoundNBT nbt = new CompoundNBT();
          nbt.put("tedata", te_nbt);
          stack.setTag(nbt);
        }
        stacks.add(stack);
      } else {
        for(ItemStack stack: ((WasteIncineratorTileEntity)te).stacks_) stacks.add(stack);
        ((WasteIncineratorTileEntity)te).getnbt();
      }
      return stacks;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
    {
      if(world.isClientSide()) return ActionResultType.SUCCESS;
      final TileEntity te = world.getBlockEntity(pos);
      if(!(te instanceof WasteIncineratorTileEntity)) return ActionResultType.FAIL;
      if((!(player instanceof ServerPlayerEntity) && (!(player instanceof FakePlayer)))) return ActionResultType.FAIL;
      NetworkHooks.openGui((ServerPlayerEntity)player,(INamedContainerProvider)te);
      return ActionResultType.CONSUME;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rnd)
    {
      if((state.getBlock()!=this) || (!state.getValue(LIT))) return;
      final double rv = rnd.nextDouble();
      if(rv > 0.5) return;
      final double x=0.5+pos.getX(), y=0.5+pos.getY(), z=0.5+pos.getZ();
      final double xr=rnd.nextDouble()*0.4-0.2, yr=rnd.nextDouble()*0.5, zr=rnd.nextDouble()*0.4-0.2;
      world.addParticle(ParticleTypes.SMOKE, x+xr, y+yr, z+zr, 0.0, 0.0, 0.0);
    }
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Tile entity
  //--------------------------------------------------------------------------------------------------------------------

  public static class WasteIncineratorTileEntity extends TileEntity implements ITickableTileEntity, INameable, IInventory, INamedContainerProvider, ISidedInventory, IEnergyStorage
  {
    public static final int NUM_OF_FIELDS = 1;
    public static final int TICK_INTERVAL = 20;
    public static final int ENERGIZED_TICK_INTERVAL = 5;
    public static final int INCINERATION_STACK_DECREMENT = 4;
    public static final int MAX_ENERGY_BUFFER = 16000;
    public static final int MAX_ENERGY_TRANSFER = 256;
    public static final int DEFAULT_ENERGY_CONSUMPTION = 16;
    public static final int NUM_OF_SLOTS = 16;
    public static final int INPUT_SLOT_NO = 0;
    public static final int BURN_SLOT_NO = NUM_OF_SLOTS-1;

    // Config ----------------------------------------------------------------------------------

    private static int energy_consumption = DEFAULT_ENERGY_CONSUMPTION;

    public static void on_config(int boost_energy_per_tick)
    {
      energy_consumption = MathHelper.clamp(boost_energy_per_tick, 4, 4096);
      ModConfig.log("Config waste incinerator: boost energy consumption:" + energy_consumption + ".");
    }

    // WasteIncineratorTileEntity -----------------------------------------------------------------------------

    private int tick_timer_;
    private int check_timer_;
    private int energy_stored_;
    protected NonNullList<ItemStack> stacks_ = NonNullList.<ItemStack>withSize(NUM_OF_SLOTS, ItemStack.EMPTY);

    public WasteIncineratorTileEntity()
    { this(ModContent.TET_WASTE_INCINERATOR); }

    public WasteIncineratorTileEntity(TileEntityType<?> te_type)
    { super(te_type); reset(); }

    public CompoundNBT getnbt()
    { return writenbt(new CompoundNBT()); }

    protected void reset()
    {
      stacks_ = NonNullList.<ItemStack>withSize(NUM_OF_SLOTS, ItemStack.EMPTY);
      check_timer_ = 0;
      tick_timer_ = 0;
    }

    public void readnbt(CompoundNBT compound)
    {
      NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(NUM_OF_SLOTS, ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound, stacks);
      while(stacks.size() < NUM_OF_SLOTS) stacks.add(ItemStack.EMPTY);
      stacks_ = stacks;
      energy_stored_ = compound.getInt("Energy");
    }

    protected CompoundNBT writenbt(CompoundNBT nbt)
    {
      nbt.putInt("Energy", MathHelper.clamp(energy_stored_,0 , MAX_ENERGY_BUFFER));
      ItemStackHelper.saveAllItems(nbt, stacks_);
      return nbt;
    }

    // TileEntity ------------------------------------------------------------------------------

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    { super.load(state, nbt); readnbt(nbt); }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    { super.save(nbt); return writenbt(nbt); }

    @Override
    public void setRemoved()
    {
      super.setRemoved();
      energy_handler_.invalidate();
      item_handler_.invalidate();
    }

    // INameable  ---------------------------------------------------------------------------

    @Override
    public ITextComponent getName()
    { final Block block=getBlockState().getBlock(); return new StringTextComponent((block!=null) ? block.getDescriptionId() : "Small Waste Incinerator"); }

    @Override
    public boolean hasCustomName()
    { return false; }

    @Override
    public ITextComponent getCustomName()
    { return getName(); }

    // IContainerProvider ----------------------------------------------------------------------

    @Override
    public ITextComponent getDisplayName()
    { return INameable.super.getDisplayName(); }

    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player )
    { return new EdWasteIncinerator.WasteIncineratorContainer(id, inventory, this, IWorldPosCallable.create(level, worldPosition), fields); }

    // IInventory ------------------------------------------------------------------------------

    @Override
    public int getContainerSize()
    { return stacks_.size(); }

    @Override
    public boolean isEmpty()
    { for(ItemStack stack: stacks_) { if(!stack.isEmpty()) return false; } return true; }

    @Override
    public ItemStack getItem(int index)
    { return ((index >= 0) && (index < getContainerSize())) ? stacks_.get(index) : ItemStack.EMPTY; }

    @Override
    public ItemStack removeItem(int index, int count)
    { return ItemStackHelper.removeItem(stacks_, index, count); }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    { return ItemStackHelper.takeItem(stacks_, index); }

    @Override
    public void setItem(int index, ItemStack stack)
    {
      if(stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
      stacks_.set(index, stack);
      setChanged();
    }

    @Override
    public int getMaxStackSize()
    { return 64; }

    @Override
    public void setChanged()
    { super.setChanged(); }

    @Override
    public boolean stillValid(PlayerEntity player)
    { return ((getLevel().getBlockEntity(getBlockPos()) == this)) && (getBlockPos().distSqr(player.blockPosition()) < 64); }

    @Override
    public void startOpen(PlayerEntity player)
    {}

    @Override
    public void stopOpen(PlayerEntity player)
    { setChanged(); }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    { return (index==0); }

    @Override
    public void clearContent()
    { stacks_.clear(); }

    // Fields -----------------------------------------------------------------------------------------------

    protected final IIntArray fields = new IntArray(WasteIncineratorTileEntity.NUM_OF_FIELDS)
    {
      @Override
      public int get(int id)
      {
        switch(id) {
          default: return 0;
        }
      }
      @Override
      public void set(int id, int value)
      {
        switch(id) {
          default: break;
        }
      }
    };

    // ISidedInventory ----------------------------------------------------------------------------

    private static final int[] SIDED_INV_SLOTS = new int[] { INPUT_SLOT_NO };

    @Override
    public int[] getSlotsForFace(Direction side)
    { return SIDED_INV_SLOTS; }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction)
    { return canPlaceItem(index, itemStackIn); }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction)
    { return false; }

    // IEnergyStorage ----------------------------------------------------------------------------

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

    // IItemHandler  --------------------------------------------------------------------------------

    protected static class BItemHandler implements IItemHandler
    {
      private final WasteIncineratorTileEntity te;

      BItemHandler(WasteIncineratorTileEntity te)
      { this.te = te; }

      @Override
      public int getSlots()
      { return 1; }

      @Override
      public int getSlotLimit(int index)
      { return te.getMaxStackSize(); }

      @Override
      public boolean isItemValid(int slot, @Nonnull ItemStack stack)
      { return true; }

      @Override
      @Nonnull
      public ItemStack insertItem(int index, @Nonnull ItemStack stack, boolean simulate)
      {
        if(stack.isEmpty()) return ItemStack.EMPTY;
        if(index != 0) return ItemStack.EMPTY;
        int slotno = 0;
        ItemStack slotstack = getStackInSlot(slotno);
        if(!slotstack.isEmpty())
        {
          if(slotstack.getCount() >= Math.min(slotstack.getMaxStackSize(), getSlotLimit(index))) return stack;
          if(!ItemHandlerHelper.canItemStacksStack(stack, slotstack)) return stack;
          if(!te.canPlaceItemThroughFace(slotno, stack, Direction.UP) || (!te.canPlaceItem(slotno, stack))) return stack;
          int n = Math.min(stack.getMaxStackSize(), getSlotLimit(index)) - slotstack.getCount();
          if(stack.getCount() <= n) {
            if(!simulate) {
              ItemStack copy = stack.copy();
              copy.grow(slotstack.getCount());
              te.setItem(slotno, copy);
            }
            return ItemStack.EMPTY;
          } else {
            stack = stack.copy();
            if(!simulate) {
              ItemStack copy = stack.split(n);
              copy.grow(slotstack.getCount());
              te.setItem(slotno, copy);
              return stack;
            } else {
              stack.shrink(n);
              return stack;
            }
          }
        } else {
          if(!te.canPlaceItemThroughFace(slotno, stack, Direction.UP) || (!te.canPlaceItem(slotno, stack))) return stack;
          int n = Math.min(stack.getMaxStackSize(), getSlotLimit(index));
          if(n < stack.getCount()) {
            stack = stack.copy();
            if(!simulate) {
              te.setItem(slotno, stack.split(n));
              return stack;
            } else {
              stack.shrink(n);
              return stack;
            }
          } else {
            if(!simulate) te.setItem(slotno, stack);
            return ItemStack.EMPTY;
          }
        }
      }

      @Override
      @Nonnull
      public ItemStack extractItem(int index, int amount, boolean simulate)
      { return ItemStack.EMPTY; }

      @Override
      @Nonnull
      public ItemStack getStackInSlot(int index)
      { return te.getItem(index); }
    }

    // Capability export ----------------------------------------------------------------------------

    protected LazyOptional<IItemHandler> item_handler_ = LazyOptional.of(() -> new WasteIncineratorTileEntity.BItemHandler(this));
    protected LazyOptional<IEnergyStorage> energy_handler_ = LazyOptional.of(() -> (IEnergyStorage)this);

    @Override
    public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing)
    {
      if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return item_handler_.cast();
      if(capability == CapabilityEnergy.ENERGY) return energy_handler_.cast();
      return super.getCapability(capability, facing);
    }

    // ITickableTileEntity ---------------------------------------------------------------------------

    @Override
    public void tick()
    {
      if(--tick_timer_ > 0) return;
      tick_timer_ = TICK_INTERVAL;
      if(level.isClientSide) return;
      boolean dirty = false;
      ItemStack processing_stack = stacks_.get(BURN_SLOT_NO);
      final boolean was_processing = !processing_stack.isEmpty();
      boolean is_processing = was_processing;
      boolean new_stack_processing = false;
      if((!stacks_.get(0).isEmpty()) && transferItems(0, 1, getMaxStackSize())) dirty = true;
      ItemStack first_stack = stacks_.get(0);
      boolean shift = !first_stack.isEmpty();
      if(is_processing) {
        processing_stack.shrink(INCINERATION_STACK_DECREMENT);
        if(processing_stack.getCount() <= 0) {
          processing_stack = ItemStack.EMPTY;
          is_processing = false;
        }
        stacks_.set(BURN_SLOT_NO, processing_stack);
        if(energy_stored_ >= (energy_consumption * TICK_INTERVAL)) {
          energy_stored_ -= (energy_consumption * TICK_INTERVAL);
          tick_timer_ = ENERGIZED_TICK_INTERVAL;
        }
        dirty = true;
      }
      if(shift) {
        boolean transferred = false;
        for(int i=BURN_SLOT_NO-1; i>0; --i) {
          transferred |= transferItems(i-1, i, getMaxStackSize());
        }
        if((!is_processing) && (!transferred)) {
          shiftStacks(0, BURN_SLOT_NO);
          dirty = true;
        }
      }
      if((was_processing != is_processing) || (new_stack_processing)) {
        if(new_stack_processing) level.playSound(null, worldPosition, SoundEvents.LAVA_AMBIENT, SoundCategory.BLOCKS, 0.05f, 2.4f);
        final BlockState state = level.getBlockState(worldPosition);
        if(state.getBlock() instanceof WasteIncineratorBlock) {
          level.setBlock(worldPosition, state.setValue(WasteIncineratorBlock.LIT, is_processing), 2|16);
        }
      }
      if(dirty) setChanged();
    }

    // Aux methods ----------------------------------------------------------------------------------

    private ItemStack shiftStacks(final int index_from, final int index_to)
    {
      if(index_from >= index_to) return ItemStack.EMPTY;
      ItemStack out_stack = ItemStack.EMPTY;
      ItemStack stack = stacks_.get(index_from);
      for(int i=index_from+1; i<=index_to; ++i) {
        out_stack = stacks_.get(i);
        stacks_.set(i, stack);
        stack = out_stack;
      }
      stacks_.set(index_from, ItemStack.EMPTY);
      return out_stack;
    }

    private boolean transferItems(final int index_from, final int index_to, int count)
    {
      ItemStack from = stacks_.get(index_from);
      if(from.isEmpty()) return false;
      ItemStack to = stacks_.get(index_to);
      if(from.getCount() < count) count = from.getCount();
      if(count <= 0) return false;
      boolean changed = true;
      if(to.isEmpty()) {
        stacks_.set(index_to, from.split(count));
      } else if(to.getCount() >= to.getMaxStackSize()) {
        changed = false;
      } else if(Inventories.areItemStacksDifferent(from, to)) {
        changed = false;
      } else {
        if((to.getCount()+count) >= to.getMaxStackSize()) {
          from.shrink(to.getMaxStackSize()-to.getCount());
          to.setCount(to.getMaxStackSize());
        } else {
          from.shrink(count);
          to.grow(count);
        }
      }
      if(from.isEmpty() && from!=ItemStack.EMPTY) {
        stacks_.set(index_from, ItemStack.EMPTY);
        changed = true;
      }
      return changed;
    }
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Container
  //--------------------------------------------------------------------------------------------------------------------

  public static class WasteIncineratorContainer extends Container
  {
    private static final int PLAYER_INV_START_SLOTNO = WasteIncineratorTileEntity.NUM_OF_SLOTS;
    protected final PlayerEntity player_;
    protected final IInventory inventory_;
    protected final IWorldPosCallable wpc_;
    private final IIntArray fields_;
    private int proc_time_needed_;

    public int field(int index) { return fields_.get(index); }
    public PlayerEntity player() { return player_ ; }
    public IInventory inventory() { return inventory_ ; }
    public World world() { return player_.level; }

    public WasteIncineratorContainer(int cid, PlayerInventory player_inventory)
    { this(cid, player_inventory, new Inventory(WasteIncineratorTileEntity.NUM_OF_SLOTS), IWorldPosCallable.NULL, new IntArray(WasteIncineratorTileEntity.NUM_OF_FIELDS)); }

    private WasteIncineratorContainer(int cid, PlayerInventory player_inventory, IInventory block_inventory, IWorldPosCallable wpc, IIntArray fields)
    {
      super(ModContent.CT_WASTE_INCINERATOR, cid);
      player_ = player_inventory.player;
      inventory_ = block_inventory;
      wpc_ = wpc;
      fields_ = fields;
      int i=-1;
      addSlot(new Slot(inventory_, ++i, 13, 9));
      addSlot(new Slot(inventory_, ++i, 37, 12));
      addSlot(new Slot(inventory_, ++i, 54, 13));
      addSlot(new Slot(inventory_, ++i, 71, 14));
      addSlot(new Slot(inventory_, ++i, 88, 15));
      addSlot(new Slot(inventory_, ++i, 105, 16));
      addSlot(new Slot(inventory_, ++i, 122, 17));
      addSlot(new Slot(inventory_, ++i, 139, 18));
      addSlot(new Slot(inventory_, ++i, 144, 38));
      addSlot(new Slot(inventory_, ++i, 127, 39));
      addSlot(new Slot(inventory_, ++i, 110, 40));
      addSlot(new Slot(inventory_, ++i, 93, 41));
      addSlot(new Slot(inventory_, ++i, 76, 42));
      addSlot(new Slot(inventory_, ++i, 59, 43));
      addSlot(new Slot(inventory_, ++i, 42, 44));
      addSlot(new Slot(inventory_, ++i, 17, 58));
      for(int x=0; x<9; ++x) {
        addSlot(new Slot(player_inventory, x, 8+x*18, 144)); // player slots: 0..8
      }
      for(int y=0; y<3; ++y) {
        for(int x=0; x<9; ++x) {
          addSlot(new Slot(player_inventory, x+y*9+9, 8+x*18, 86+y*18)); // player slots: 9..35
        }
      }
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    { return inventory_.stillValid(player); }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index)
    {
      Slot slot = getSlot(index);
      if((slot==null) || (!slot.hasItem())) return ItemStack.EMPTY;
      ItemStack slot_stack = slot.getItem();
      ItemStack transferred = slot_stack.copy();
      if((index>=0) && (index<PLAYER_INV_START_SLOTNO)) {
        // Device slots
        if(!moveItemStackTo(slot_stack, PLAYER_INV_START_SLOTNO, PLAYER_INV_START_SLOTNO+36, true)) return ItemStack.EMPTY;
      } else if((index >= PLAYER_INV_START_SLOTNO) && (index <= PLAYER_INV_START_SLOTNO+36)) {
        // Player slot
        if(!moveItemStackTo(slot_stack, 0, PLAYER_INV_START_SLOTNO-1, true)) return ItemStack.EMPTY;
      } else {
        // invalid slot
        return ItemStack.EMPTY;
      }
      if(slot_stack.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
      if(slot_stack.getCount() == transferred.getCount()) return ItemStack.EMPTY;
      slot.onTake(player, slot_stack);
      return transferred;
    }
  }

  //--------------------------------------------------------------------------------------------------------------------
  // GUI
  //--------------------------------------------------------------------------------------------------------------------

  @OnlyIn(Dist.CLIENT)
  public static class WasteIncineratorGui extends ContainerGui<WasteIncineratorContainer>
  {
    protected final PlayerEntity player_;

    public WasteIncineratorGui(WasteIncineratorContainer container, PlayerInventory player_inventory, ITextComponent title)
    { super(container, player_inventory, title); this.player_ = player_inventory.player; }

    @Override
    public void init()
    { super.init(); }

    @Override
    public void render(MatrixStack mx, int mouseX, int mouseY, float partialTicks)
    {
      renderBackground/*renderBackground*/(mx);
      super.render(mx, mouseX, mouseY, partialTicks);
      renderTooltip(mx, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(MatrixStack mx, int x, int y)
    {}

    @Override
    @SuppressWarnings("deprecation")
    protected void renderBg(MatrixStack mx, float partialTicks, int mouseX, int mouseY)
    {
      RenderSystem.enableBlend();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      getMinecraft().getTextureManager().bind(new ResourceLocation(ModEngineersDecor.MODID, "textures/gui/small_waste_incinerator_gui.png"));
      final int x0=leftPos, y0=this.topPos, w=imageWidth, h=imageHeight;
      blit(mx, x0, y0, 0, 0, w, h);
      RenderSystem.disableBlend();
    }
  }

}
