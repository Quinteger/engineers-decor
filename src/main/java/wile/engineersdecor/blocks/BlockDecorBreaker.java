/*
 * @file BlockDecorBreaker.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2019 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Small Block Breaker
 */
package wile.engineersdecor.blocks;

import wile.engineersdecor.ModEngineersDecor;
import wile.engineersdecor.detail.ModAuxiliaries;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;


public class BlockDecorBreaker extends BlockDecorDirectedHorizontal
{
  //--------------------------------------------------------------------------------------------------------------------
  // Config
  //--------------------------------------------------------------------------------------------------------------------

  public static final int IDLE_TICK_INTERVAL = 40;
  public static final int TICK_INTERVAL = 5;
  public static final int BOOST_FACTOR = 8;
  public static final int DEFAULT_BOOST_ENERGY = 64;
  public static final int DEFAULT_BREAKING_RELUCTANCE = 17;
  public static final int DEFAULT_MIN_BREAKING_TIME = 15;
  public static final int MAX_BREAKING_TIME = 800;
  private static int boost_energy_consumption = DEFAULT_BOOST_ENERGY;
  private static int energy_max = 10000;
  private static int breaking_reluctance = DEFAULT_BREAKING_RELUCTANCE;
  private static int min_breaking_time = DEFAULT_MIN_BREAKING_TIME;
  private static boolean requires_power = false;

  public static void on_config(int boost_energy_per_tick, int breaking_time_per_hardness, int min_breaking_time_ticks, boolean power_required)
  {
    boost_energy_consumption = TICK_INTERVAL * MathHelper.clamp(boost_energy_per_tick, 16, 512);
    energy_max = Math.max(boost_energy_consumption * 10, 10000);
    breaking_reluctance = MathHelper.clamp(breaking_time_per_hardness, 5, 50);
    min_breaking_time = MathHelper.clamp(min_breaking_time_ticks, 10, 100);
    requires_power = power_required;
    ModEngineersDecor.logger.info("Config block breaker: Boost energy consumption:" + (boost_energy_consumption/TICK_INTERVAL) + "rf/t, reluctance=" + breaking_reluctance + "t/hrdn, break time offset=" + min_breaking_time + "t");
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Block
  //--------------------------------------------------------------------------------------------------------------------

  public static final PropertyBool ACTIVE = PropertyBool.create("active");

  public BlockDecorBreaker(@Nonnull String registryName, long config, @Nullable Material material, float hardness, float resistance, @Nullable SoundType sound, @Nonnull AxisAlignedBB unrotatedAABB)
  {
    super(registryName, config, material, hardness, resistance, sound, unrotatedAABB);
    setLightOpacity(0);
  }

  @Override
  protected BlockStateContainer createBlockState()
  { return new BlockStateContainer(this, FACING, ACTIVE); }

  @Override
  public IBlockState getStateFromMeta(int meta)
  { return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 0x7)).withProperty(ACTIVE, (meta & 0x8)!=0); }

  @Override
  public int getMetaFromState(IBlockState state)
  { return (state.getValue(FACING).getHorizontalIndex() & 0x7) | (state.getValue(ACTIVE) ? 8 : 0); }

  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
  { return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(ACTIVE, false); }

  @Override
  public boolean hasTileEntity(IBlockState state)
  { return true; }

  @Override
  @Nullable
  public TileEntity createTileEntity(World world, IBlockState state)
  { return new BTileEntity(); }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rnd)
  {
    if((state.getBlock()!=this) || (!state.getValue(ACTIVE))) return;
    final double rv = rnd.nextDouble();
    if(rv > 0.8) return;
    final double x=0.5+pos.getX(), y=0.5+pos.getY(), z=0.5+pos.getZ();
    final double xc=0.52, xr=rnd.nextDouble()*0.4-0.2, yr=(y-0.3+rnd.nextDouble()*0.2);
    switch(state.getValue(FACING)) {
      case WEST:  world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x-xc, yr, z+xr, 0.0, 0.0, 0.0); break;
      case EAST:  world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+xc, yr, z+xr, 0.0, 0.0, 0.0); break;
      case NORTH: world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+xr, yr, z-xc, 0.0, 0.0, 0.0); break;
      default:    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+xr, yr, z+xc, 0.0, 0.0, 0.0); break;
    }
  }

  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos)
  {
    if(!(world instanceof World) || (((World) world).isRemote)) return;
    TileEntity te = world.getTileEntity(pos);
    if(!(te instanceof BTileEntity)) return;
    ((BTileEntity)te).block_updated();
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
  {
    if(world.isRemote) return true;
    TileEntity te = world.getTileEntity(pos);
    if(!(te instanceof BTileEntity)) return true;
    ((BTileEntity)te).state_message(player);
    return true;
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Tile entity
  //--------------------------------------------------------------------------------------------------------------------

  public static class BTileEntity extends TileEntity implements ITickable, IEnergyStorage
  {
    private int tick_timer_;
    private int active_timer_;
    private int proc_time_elapsed_;
    private int time_needed_;
    private int energy_;

    public BTileEntity()
    {}

    public void block_updated()
    { if(tick_timer_ > 2) tick_timer_ = 2; }

    public void state_message(EntityPlayer player)
    {
      String soc = Integer.toString(MathHelper.clamp((energy_*100/energy_max),0,100));
      String progress = "";
      if((proc_time_elapsed_ > 0) && (time_needed_ > 0) && (active_timer_ > 0)) {
        progress = " | " + Integer.toString((int)MathHelper.clamp((((double)proc_time_elapsed_) / ((double)time_needed_) * 100), 0, 100)) + "%%";
      }
      ModAuxiliaries.playerChatMessage(player, soc + "%%/" + energy_max + "RF" + progress);
    }

    public void readnbt(NBTTagCompound nbt)
    { energy_ = nbt.getInteger("energy"); }

    private void writenbt(NBTTagCompound nbt)
    { nbt.setInteger("energy", energy_); }

    // TileEntity ------------------------------------------------------------------------------

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState os, IBlockState ns)
    { return (os.getBlock() != ns.getBlock()) || (!(ns.getBlock() instanceof BlockDecorBreaker)); }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    { super.readFromNBT(nbt); readnbt(nbt); }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    { super.writeToNBT(nbt); writenbt(nbt); return nbt; }

    // IEnergyStorage ----------------------------------------------------------------------------

    @Override
    public boolean canExtract()
    { return false; }

    @Override
    public boolean canReceive()
    { return true; }

    @Override
    public int getMaxEnergyStored()
    { return boost_energy_consumption*2; }

    @Override
    public int getEnergyStored()
    { return energy_; }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    { return 0; }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
      maxReceive = MathHelper.clamp(maxReceive, 0, Math.max(energy_max-energy_, 0));
      if(!simulate) energy_ += maxReceive;
      return maxReceive;
    }

    // Capability export ----------------------------------------------------------------------------

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing)
    { return ((cap==CapabilityEnergy.ENERGY)) || super.hasCapability(cap, facing); }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
      if(capability == CapabilityEnergy.ENERGY) {
        return (T)this;
      } else {
        return super.getCapability(capability, facing);
      }
    }

    // ITickable ------------------------------------------------------------------------------------

    private static HashSet<Block> blacklist = new HashSet<>();
    static {
      blacklist.add(Blocks.AIR);
      blacklist.add(Blocks.BEDROCK);
      blacklist.add(Blocks.FIRE);
      blacklist.add(Blocks.END_PORTAL);
      blacklist.add(Blocks.END_GATEWAY);
      blacklist.add(Blocks.END_PORTAL_FRAME);
    }

    private static boolean isBreakable(IBlockState state, BlockPos pos, World world)
    {
      final Block block = state.getBlock();
      if(blacklist.contains(block)) return false;
      if(state.getMaterial().isLiquid()) return false;
      if(block.isAir(state, world, pos)) return false;
      float bh = state.getBlockHardness(world, pos);
      if((bh<0) || (bh>55)) return false;
      return true;
    }

    private static boolean breakBlock(IBlockState state, BlockPos pos, World world)
    {
      if(world.isRemote || world.restoringBlockSnapshots) return false; // retry next cycle
      final Block block = state.getBlock();
      block.dropBlockAsItem(world, pos, state, 0);
      world.setBlockToAir(pos);
      SoundType stype = state.getBlock().getSoundType(state, world, pos, null);
      if(stype != null) world.playSound(null, pos, stype.getPlaceSound(), SoundCategory.BLOCKS, stype.getVolume()*0.6f, stype.getPitch());
      return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void update()
    {
      if(--tick_timer_ > 0) return;
      final IBlockState device_state = world.getBlockState(pos);
      if(!(device_state.getBlock() instanceof BlockDecorBreaker)) { tick_timer_ = TICK_INTERVAL; return; }
      if(world.isRemote) {
        if(!device_state.getValue(ACTIVE)) {
          tick_timer_ = TICK_INTERVAL;
        } else {
          tick_timer_ = 1;
          // not sure if is so cool to do this each tick ... may be simplified/removed again.
          SoundEvent sound = SoundEvents.BLOCK_WOOD_HIT;
          SoundType stype = world.getBlockState(pos.offset(device_state.getValue(FACING))).getBlock().getSoundType();
          if((stype == SoundType.CLOTH) || (stype == SoundType.PLANT) || (stype == SoundType.SNOW)) {
            sound = SoundEvents.BLOCK_CLOTH_HIT;
          } else if((stype == SoundType.GROUND) || (stype == SoundType.SAND)) {
            sound = SoundEvents.BLOCK_GRAVEL_HIT;
          }
          world.playSound(pos.getX(), pos.getY(), pos.getZ(), sound, SoundCategory.BLOCKS, 0.1f, 1.2f, false);
        }
      } else {
        tick_timer_ = TICK_INTERVAL;
        final BlockPos target_pos = pos.offset(device_state.getValue(FACING));
        final IBlockState target_state = world.getBlockState(target_pos);
        if((world.isBlockPowered(pos)) || (!isBreakable(target_state, target_pos, world))) {
          if(device_state.getValue(ACTIVE)) world.setBlockState(pos, device_state.withProperty(ACTIVE, false), 1|2);
          proc_time_elapsed_ = 0;
          tick_timer_ = IDLE_TICK_INTERVAL;
          return;
        }
        time_needed_ = MathHelper.clamp((int)(target_state.getBlockHardness(world, target_pos) * breaking_reluctance) + min_breaking_time, min_breaking_time, MAX_BREAKING_TIME);
        if(energy_ >= boost_energy_consumption) {
          energy_ -= boost_energy_consumption;
          proc_time_elapsed_ += TICK_INTERVAL * (1+BOOST_FACTOR);
          time_needed_ += min_breaking_time * (3*BOOST_FACTOR/5);
          active_timer_ = 2;
        } else if(!requires_power) {
          proc_time_elapsed_ += TICK_INTERVAL;
          active_timer_ = 1024;
        } else if(active_timer_ > 0) {
          --active_timer_;
        }
        boolean active = (active_timer_ > 0);
        if(requires_power && !active) {
          proc_time_elapsed_ = Math.max(0, proc_time_elapsed_ - 2*TICK_INTERVAL);
        }
        if(proc_time_elapsed_ >= time_needed_) {
          proc_time_elapsed_ = 0;
          breakBlock(target_state, target_pos, world);
          active = false;
        }
        if(device_state.getValue(ACTIVE) != active) {
          world.setBlockState(pos, device_state.withProperty(ACTIVE, active), 1|2);
        }
      }
    }
  }
}
