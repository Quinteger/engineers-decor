/*
 * @file ModContent.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Definition and initialisation of blocks of this
 * module, along with their tile entities if applicable.
 *
 * Note: Straight forward definition of different blocks/entities
 *       to make recipes, models and texture definitions easier.
 */
package wile.engineersdecor;

import net.minecraft.block.BlockState;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.world.IBlockReader;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.commons.lang3.ArrayUtils;
import wile.engineersdecor.blocks.*;
import wile.engineersdecor.blocks.EdFurnace.FurnaceBlock;
import wile.engineersdecor.blocks.EdFurnace.FurnaceContainer;
import wile.engineersdecor.blocks.EdFurnace.FurnaceGui;
import wile.engineersdecor.blocks.EdFurnace.FurnaceTileEntity;
import wile.engineersdecor.items.EdItem;
import wile.engineersdecor.libmc.blocks.StandardBlocks;
import wile.engineersdecor.libmc.blocks.StandardBlocks.IStandardBlock;
import wile.engineersdecor.libmc.detail.Auxiliaries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import javax.annotation.Nonnull;


import net.minecraft.block.AbstractBlock;

@SuppressWarnings("unused")
public class ModContent
{
  private static final String MODID = ModEngineersDecor.MODID;

  //--------------------------------------------------------------------------------------------------------------------

  private static Boolean disallowSpawn(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entity) { return false; }

  //--------------------------------------------------------------------------------------------------------------------
  // Blocks
  //--------------------------------------------------------------------------------------------------------------------

  public static final DecorBlock.Normal CLINKER_BRICK_BLOCK = (DecorBlock.Normal)(new DecorBlock.Normal(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_block"));

  public static final EdSlabBlock CLINKER_BRICK_SLAB = (EdSlabBlock)(new EdSlabBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_slab"));

  public static final EdStairsBlock CLINKER_BRICK_STAIRS = (EdStairsBlock)(new EdStairsBlock(
    DecorBlock.CFG_DEFAULT,
    CLINKER_BRICK_BLOCK.defaultBlockState(),
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_stairs"));

  public static final EdWallBlock CLINKER_BRICK_WALL = (EdWallBlock)(new EdWallBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_wall"));

  public static final DecorBlock.Normal CLINKER_BRICK_STAINED_BLOCK = (DecorBlock.Normal)(new DecorBlock.Normal(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_stained_block"));

  public static final EdSlabBlock CLINKER_BRICK_STAINED_SLAB = (EdSlabBlock)(new EdSlabBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_stained_slab"));

  public static final EdStairsBlock CLINKER_BRICK_STAINED_STAIRS = (EdStairsBlock)(new EdStairsBlock(
    DecorBlock.CFG_DEFAULT,
    CLINKER_BRICK_BLOCK.defaultBlockState(),
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_stained_stairs"));

  public static final EdCornerOrnamentedBlock CLINKER_BRICK_SASTOR_CORNER = (EdCornerOrnamentedBlock)(new EdCornerOrnamentedBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE),
    new Block[]{CLINKER_BRICK_BLOCK, CLINKER_BRICK_STAINED_BLOCK, CLINKER_BRICK_SLAB, CLINKER_BRICK_STAIRS, CLINKER_BRICK_STAINED_SLAB, CLINKER_BRICK_STAINED_STAIRS}
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_sastor_corner_block"));

  public static final DecorBlock.HorizontalWaterLoggable CLINKER_BRICK_RECESSED = (DecorBlock.HorizontalWaterLoggable)(new DecorBlock.HorizontalWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE),
    new AxisAlignedBB[] {
      Auxiliaries.getPixeledAABB( 3,0, 0, 13,16, 1),
      Auxiliaries.getPixeledAABB( 0,0, 1, 16,16,11),
      Auxiliaries.getPixeledAABB( 4,0,11, 12,16,13)
    }
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_recessed"));

  public static final DecorBlock.HorizontalWaterLoggable CLINKER_BRICK_SASTOR_VERTICAL_SLOTTED = (DecorBlock.HorizontalWaterLoggable)(new DecorBlock.HorizontalWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE),
    new AxisAlignedBB[] {
      Auxiliaries.getPixeledAABB( 3,0, 0, 13,16, 1),
      Auxiliaries.getPixeledAABB( 3,0,15, 13,16,16),
      Auxiliaries.getPixeledAABB( 0,0, 1, 16,16,15)
    }
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_vertically_slit"));

  public static final DecorBlock.HorizontalWaterLoggable CLINKER_BRICK_VERTICAL_SLAB_STRUCTURED = (DecorBlock.HorizontalWaterLoggable)(new DecorBlock.HorizontalWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE),
    new AxisAlignedBB[] {
      Auxiliaries.getPixeledAABB( 0,0, 0, 16,16, 8),
    }
  )).setRegistryName(new ResourceLocation(MODID, "clinker_brick_vertical_slab_structured"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final DecorBlock.Normal SLAG_BRICK_BLOCK = (DecorBlock.Normal)(new DecorBlock.Normal(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "slag_brick_block"));

  public static final EdSlabBlock SLAG_BRICK_SLAB = (EdSlabBlock)(new EdSlabBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "slag_brick_slab"));

  public static final EdStairsBlock SLAG_BRICK_STAIRS = (EdStairsBlock)(new EdStairsBlock(
    DecorBlock.CFG_DEFAULT,
    SLAG_BRICK_BLOCK.defaultBlockState(),
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "slag_brick_stairs"));

  public static final EdWallBlock SLAG_BRICK_WALL = (EdWallBlock)(new EdWallBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(3f, 10f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "slag_brick_wall"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final DecorBlock.Normal REBAR_CONCRETE_BLOCK = (DecorBlock.Normal)(new DecorBlock.Normal(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(5f, 2000f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "rebar_concrete"));

  public static final EdSlabBlock REBAR_CONCRETE_SLAB = (EdSlabBlock)(new EdSlabBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(5f, 2000f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "rebar_concrete_slab"));

  public static final EdStairsBlock REBAR_CONCRETE_STAIRS = (EdStairsBlock)(new EdStairsBlock(
    DecorBlock.CFG_DEFAULT,
    REBAR_CONCRETE_BLOCK.defaultBlockState(),
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(5f, 2000f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "rebar_concrete_stairs"));

  public static final EdWallBlock REBAR_CONCRETE_WALL = (EdWallBlock)(new EdWallBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(5f, 2000f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "rebar_concrete_wall"));

  public static final EdSlabSliceBlock HALFSLAB_REBARCONCRETE = (EdSlabSliceBlock)(new EdSlabSliceBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(5f, 2000f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "halfslab_rebar_concrete"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final DecorBlock.Normal GAS_CONCRETE_BLOCK = (DecorBlock.Normal)(new DecorBlock.Normal(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(1.5f, 10f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "gas_concrete"));

  public static final EdSlabBlock GAS_CONCRETE_SLAB = (EdSlabBlock)(new EdSlabBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(1.5f, 10f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "gas_concrete_slab"));

  public static final EdStairsBlock GAS_CONCRETE_STAIRS = (EdStairsBlock)(new EdStairsBlock(
    DecorBlock.CFG_DEFAULT,
    REBAR_CONCRETE_BLOCK.defaultBlockState(),
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(1.5f, 10f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "gas_concrete_stairs"));

  public static final EdWallBlock GAS_CONCRETE_WALL = (EdWallBlock)(new EdWallBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(1.5f, 10f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "gas_concrete_wall"));

  public static final EdSlabSliceBlock HALFSLAB_GASCONCRETE = (EdSlabSliceBlock)(new EdSlabSliceBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(1.5f, 10f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "halfslab_gas_concrete"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final DecorBlock.Normal REBAR_CONCRETE_TILE = (DecorBlock.Normal)(new DecorBlock.Normal(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(5f, 2000f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "rebar_concrete_tile"));

  public static final EdSlabBlock REBAR_CONCRETE_TILE_SLAB = (EdSlabBlock)(new EdSlabBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(5f, 2000f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "rebar_concrete_tile_slab"));

  public static final EdStairsBlock REBAR_CONCRETE_TILE_STAIRS = (EdStairsBlock)(new EdStairsBlock(
    DecorBlock.CFG_DEFAULT,
    REBAR_CONCRETE_TILE.defaultBlockState(),
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(5f, 2000f).sound(SoundType.STONE).isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "rebar_concrete_tile_stairs"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final EdGlassBlock PANZERGLASS_BLOCK = (EdGlassBlock)(new EdGlassBlock(
    DecorBlock.CFG_TRANSLUCENT,
    AbstractBlock.Properties.of(Material.GLASS, MaterialColor.NONE).strength(0.7f, 2000f).sound(SoundType.METAL).noOcclusion().isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "panzerglass_block"));

  public static final EdSlabBlock PANZERGLASS_SLAB = (EdSlabBlock)(new EdSlabBlock(
    DecorBlock.CFG_TRANSLUCENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(0.7f, 2000f).sound(SoundType.METAL).noOcclusion().isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "panzerglass_slab"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final EdRoofBlock DARK_CERAMIC_SHINGLE_ROOF = (EdRoofBlock)(new EdRoofBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(2f, 6f).sound(SoundType.STONE).noOcclusion().dynamicShape().isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "dark_shingle_roof"));

  public static final EdRoofBlock DARK_CERAMIC_SHINGLE_ROOF_METALIZED = (EdRoofBlock)(new EdRoofBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(2f, 6f).sound(SoundType.STONE).noOcclusion().dynamicShape().isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "dark_shingle_roof_metallized"));

  public static final EdRoofBlock DARK_CERAMIC_SHINGLE_ROOF_SKYLIGHT = (EdRoofBlock)(new EdRoofBlock(
    DecorBlock.CFG_TRANSLUCENT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(2f, 6f).sound(SoundType.STONE).noOcclusion().dynamicShape().isValidSpawn(ModContent::disallowSpawn)
  )).setRegistryName(new ResourceLocation(MODID, "dark_shingle_roof_skylight"));

  public static final EdChimneyTrunkBlock DARK_CERAMIC_SHINGLE_ROOF_CHIMNEYTRUNK = (EdChimneyTrunkBlock)(new EdChimneyTrunkBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(2f, 6f).sound(SoundType.STONE).noOcclusion().dynamicShape().isValidSpawn(ModContent::disallowSpawn),
    VoxelShapes.create(Auxiliaries.getPixeledAABB(3, 0, 3, 13, 16, 13)),
    VoxelShapes.create(Auxiliaries.getPixeledAABB(5, 0, 5, 11, 16, 11))
  )).setRegistryName(new ResourceLocation(MODID, "dark_shingle_roof_chimneytrunk"));

  public static final EdChimneyTrunkBlock DARK_CERAMIC_SHINGLE_ROOF_WIRECONDUIT = (EdChimneyTrunkBlock)(new EdChimneyTrunkBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(2f, 6f).sound(SoundType.STONE).noOcclusion().dynamicShape().isValidSpawn(ModContent::disallowSpawn),
    VoxelShapes.join(
      VoxelShapes.create(Auxiliaries.getPixeledAABB(3,  0, 3, 13, 13, 13)),
      VoxelShapes.create(Auxiliaries.getPixeledAABB(5, 13, 5, 11, 16, 11)),
      IBooleanFunction.OR
    ),
    VoxelShapes.join(
      VoxelShapes.create(Auxiliaries.getPixeledAABB(5,  0, 5, 11, 15, 11)),
      VoxelShapes.create(Auxiliaries.getPixeledAABB(7, 15, 7,  9, 16,  9)),
      IBooleanFunction.OR
    )
  )).setRegistryName(new ResourceLocation(MODID, "dark_shingle_roof_wireconduit"));

  public static final EdChimneyBlock DARK_CERAMIC_SHINGLE_ROOF_CHIMNEY = (EdChimneyBlock)(new EdChimneyBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(5f, 6f).sound(SoundType.STONE).dynamicShape().isValidSpawn(ModContent::disallowSpawn),
    Auxiliaries.getPixeledAABB(3, 0, 3, 13, 6, 13)
  )).setRegistryName(new ResourceLocation(MODID, "dark_shingle_roof_chimney"));

  public static final DecorBlock.Normal DARK_CERAMIC_SHINGLE_ROOF_BLOCK = (DecorBlock.Normal)(new DecorBlock.Normal(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(2f, 6f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "dark_shingle_roof_block"));

  public static final EdSlabBlock DARK_CERAMIC_SHINGLE_ROOF_SLAB = (EdSlabBlock)(new EdSlabBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(2f, 6f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "dark_shingle_roof_slab"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final EdGroundBlock DENSE_GRIT_SAND = (EdGroundBlock)(new EdGroundBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5f, 3f).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL)
  )).setRegistryName(new ResourceLocation(MODID, "dense_grit_sand_block"));

  public static final EdGroundBlock DENSE_GRIT_DIRT = (EdGroundBlock)(new EdGroundBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5f, 3f).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL)
  )).setRegistryName(new ResourceLocation(MODID, "dense_grit_dirt_block"));

  public static final EdSlabSliceBlock HALFSLAB_DARK_CERAMIC_SHINGLE_ROOF = (EdSlabSliceBlock)(new EdSlabSliceBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).strength(2f, 15f).sound(SoundType.STONE)
  )).setRegistryName(new ResourceLocation(MODID, "dark_shingle_roof_slabslice"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final EdLadderBlock METAL_RUNG_LADDER = (EdLadderBlock)(new EdLadderBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1.0f, 8f).sound(SoundType.METAL).noOcclusion()
  )).setRegistryName(new ResourceLocation(MODID, "metal_rung_ladder"));

  public static final EdLadderBlock METAL_RUNG_STEPS = (EdLadderBlock)(new EdLadderBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1.0f, 8f).sound(SoundType.METAL).noOcclusion()
  )).setRegistryName(new ResourceLocation(MODID, "metal_rung_steps"));

  public static final EdLadderBlock TREATED_WOOD_LADDER = (EdLadderBlock)(new EdLadderBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1.0f, 8f).sound(SoundType.WOOD).noOcclusion()
  )).setRegistryName(new ResourceLocation(MODID, "treated_wood_ladder"));

  public static final EdHatchBlock IRON_HATCH = (EdHatchBlock)(new EdHatchBlock(
    DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 2000f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(0.5,1,0, 15.5,3,14),
    Auxiliaries.getPixeledAABB(0.5,1,0, 15.5,14.,2)
  )).setRegistryName(new ResourceLocation(MODID, "iron_hatch"));

  public static final EdDoorBlock METAL_SLIDING_DOOR = (EdDoorBlock)(new EdDoorBlock(
    DecorBlock.CFG_TRANSLUCENT|DecorBlock.CFG_HORIZIONTAL,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1.5f, 8f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(15, 0.0,6, 16,16.0,10),
      Auxiliaries.getPixeledAABB( 0,15.5,6, 16,16.0,10),
    },
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(15, 0.0,6, 16,16.0,10),
      Auxiliaries.getPixeledAABB( 0, 0.0,6, 16, 0.3,10),
    },
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB( 0,0,7, 16,16,9)
    },
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB( 0,0,7, 16,16,9)
    },
    SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_DOOR_CLOSE
  )).setRegistryName(new ResourceLocation(MODID, "metal_sliding_door"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final DecorBlock.Normal OLD_INDUSTRIAL_PLANKS = (DecorBlock.Normal)(new DecorBlock.Normal(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1.5f, 6f).sound(SoundType.WOOD)
  )).setRegistryName(new ResourceLocation(MODID, "old_industrial_wood_planks"));

  public static final EdSlabBlock OLD_INDUSTRIAL_SLAB = (EdSlabBlock)(new EdSlabBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1.5f, 6f).sound(SoundType.WOOD)
  )).setRegistryName(new ResourceLocation(MODID, "old_industrial_wood_slab"));

  public static final EdStairsBlock OLD_INDUSTRIAL_STAIRS = (EdStairsBlock)(new EdStairsBlock(
    DecorBlock.CFG_DEFAULT,
    OLD_INDUSTRIAL_PLANKS.defaultBlockState(),
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1.5f, 6f).sound(SoundType.WOOD)
  )).setRegistryName(new ResourceLocation(MODID, "old_industrial_wood_stairs"));

  public static final EdSlabSliceBlock OLD_INDUSTRIAL_SLABSLICE = (EdSlabSliceBlock)(new EdSlabSliceBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1.5f, 6f).sound(SoundType.WOOD).noOcclusion()
  )).setRegistryName(new ResourceLocation(MODID, "old_industrial_wood_slabslice"));

  public static final EdDoorBlock OLD_INDUSTRIAL_WOOD_DOOR = (EdDoorBlock)(new EdDoorBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1.5f, 6f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(15,0, 0, 16,16,16),
    Auxiliaries.getPixeledAABB( 0,0,13, 16,16,16),
    SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_DOOR_CLOSE
  )).setRegistryName(new ResourceLocation(MODID, "old_industrial_wood_door"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final DecorBlock.WaterLoggable TREATED_WOOD_TABLE = (DecorBlock.WaterLoggable)(new DecorBlock.WaterLoggable(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2f, 5f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(1,0,1, 15,15.9,15)
  )).setRegistryName(new ResourceLocation(MODID, "treated_wood_table"));

  public static final EdChair.ChairBlock TREATED_WOOD_STOOL = (EdChair.ChairBlock)(new EdChair.ChairBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2f, 5f).sound(SoundType.WOOD).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(4,7,4, 12,8.8,12),
      Auxiliaries.getPixeledAABB(7,0,7, 9,7,9),
      Auxiliaries.getPixeledAABB(4,0,7, 12,1,9),
      Auxiliaries.getPixeledAABB(7,0,4, 9,1,12),
    }
  )).setRegistryName(new ResourceLocation(MODID, "treated_wood_stool"));

  public static final DecorBlock.WaterLoggable TREATED_WOOD_SIDE_TABLE = (DecorBlock.WaterLoggable)(new DecorBlock.WaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2f, 5f).sound(SoundType.WOOD).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB( 2,15, 2, 14,16,14),
      Auxiliaries.getPixeledAABB( 3,15, 1, 13,16, 2),
      Auxiliaries.getPixeledAABB( 3,15,14, 13,16,15),
      Auxiliaries.getPixeledAABB( 1,15, 3,  2,16,13),
      Auxiliaries.getPixeledAABB(14,15, 3, 15,16,13),
      Auxiliaries.getPixeledAABB( 3,14, 3, 13,15,13),
      Auxiliaries.getPixeledAABB( 7, 3, 7,  9,12, 9),
      Auxiliaries.getPixeledAABB( 2, 0, 7, 14, 1, 9),
      Auxiliaries.getPixeledAABB( 7, 0, 2,  9, 1,14)
    }
  )).setRegistryName(new ResourceLocation(MODID, "treated_wood_side_table"));

  public static final DecorBlock.DirectedWaterLoggable TREATED_WOOD_WINDOWSILL = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2f, 5f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(0.5,15,10.5, 15.5,16,16)
  )).setRegistryName(new ResourceLocation(MODID, "treated_wood_windowsill"));

  public static final DecorBlock.DirectedWaterLoggable TREATED_WOOD_BROAD_WINDOWSILL = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_FACING_PLACEMENT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2f, 5f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,14.5,4, 16,16,16)
  )).setRegistryName(new ResourceLocation(MODID, "treated_wood_broad_windowsill"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final DecorBlock.DirectedWaterLoggable INSET_LIGHT_IRON = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 8f).sound(SoundType.METAL).lightLevel((state)->15).noOcclusion(),
    Auxiliaries.getPixeledAABB(5.2,5.2,0, 10.8,10.8,0.3)
  )).setRegistryName(new ResourceLocation(MODID, "iron_inset_light"));

  public static final DecorBlock.DirectedWaterLoggable FLOOR_EDGE_LIGHT_IRON = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 8f).sound(SoundType.METAL).lightLevel((state)->15).noOcclusion(),
    Auxiliaries.getPixeledAABB(5,0,0, 11,2,0.5)
  )).setRegistryName(new ResourceLocation(MODID, "iron_floor_edge_light"));

  public static final DecorBlock.DirectedWaterLoggable CEILING_EDGE_LIGHT_IRON = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 8f).sound(SoundType.METAL).lightLevel((state)->15).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB( 0,15.5,0, 16,16,2.0),
      Auxiliaries.getPixeledAABB( 0,14.0,0, 16,16,0.5),
      Auxiliaries.getPixeledAABB( 0,14.0,0,  1,16,2.0),
      Auxiliaries.getPixeledAABB(15,14.0,0, 16,16,2.0),
    }
  )).setRegistryName(new ResourceLocation(MODID, "iron_ceiling_edge_light"));

  public static final DecorBlock.DirectedWaterLoggable BULB_LIGHT_IRON = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 8f).sound(SoundType.METAL).lightLevel((state)->15).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(6.5,6.5,1, 9.5,9.5,4),
      Auxiliaries.getPixeledAABB(6.0,6.0,0, 10.0,10.0,1.0)
    }
  )).setRegistryName(new ResourceLocation(MODID, "iron_bulb_light"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final DecorBlock.WaterLoggable STEEL_TABLE = (DecorBlock.WaterLoggable)(new DecorBlock.WaterLoggable(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 8f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,0,0, 16,16,16)
  )).setRegistryName(new ResourceLocation(MODID, "steel_table"));

  public static final EdFloorGratingBlock STEEL_FLOOR_GRATING = (EdFloorGratingBlock)(new EdFloorGratingBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 8f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,14,0, 16,15.5,16)
  )).setRegistryName(new ResourceLocation(MODID, "steel_floor_grating"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final EdWindowBlock TREATED_WOOD_WINDOW = (EdWindowBlock)(new EdWindowBlock(
    DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2f, 8f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,0,7, 16,16,9)
  )).setRegistryName(new ResourceLocation(MODID, "treated_wood_window"));

  public static final EdWindowBlock STEEL_FRAMED_WINDOW = (EdWindowBlock)(new EdWindowBlock(
    DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 8f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,0,7.5, 16,16,8.5)
  )).setRegistryName(new ResourceLocation(MODID, "steel_framed_window"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final EdStraightPoleBlock TREATED_WOOD_POLE = (EdStraightPoleBlock)(new EdStraightPoleBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_FLIP_PLACEMENT_IF_SAME,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2f, 5f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(5.8,5.8,0, 10.2,10.2,16),
    null
  )).setRegistryName(new ResourceLocation(MODID, "treated_wood_pole"));

  public static final EdStraightPoleBlock TREATED_WOOD_POLE_HEAD = (EdStraightPoleBlock)(new EdStraightPoleBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_FLIP_PLACEMENT_IF_SAME,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2f, 5f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(5.8,5.8,0, 10.2,10.2,16),
    TREATED_WOOD_POLE
  )).setRegistryName(new ResourceLocation(MODID, "treated_wood_pole_head"));

  public static final EdStraightPoleBlock TREATED_WOOD_POLE_SUPPORT = (EdStraightPoleBlock)(new EdStraightPoleBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_FLIP_PLACEMENT_IF_SAME,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2f, 5f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(5.8,5.8,0, 10.2,10.2,16),
    TREATED_WOOD_POLE
  )).setRegistryName(new ResourceLocation(MODID, "treated_wood_pole_support"));

  public static final EdStraightPoleBlock THIN_STEEL_POLE = (EdStraightPoleBlock)(new EdStraightPoleBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 11f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(6,6,0, 10,10,16),
    null
  )).setRegistryName(new ResourceLocation(MODID, "thin_steel_pole"));

  public static final EdStraightPoleBlock THIN_STEEL_POLE_HEAD = (EdStraightPoleBlock)(new EdStraightPoleBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_FLIP_PLACEMENT_IF_SAME,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 11f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(6,6,0, 10,10,16),
    THIN_STEEL_POLE
  )).setRegistryName(new ResourceLocation(MODID, "thin_steel_pole_head"));

  public static final EdStraightPoleBlock THICK_STEEL_POLE = (EdStraightPoleBlock)(new EdStraightPoleBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 11f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(5,5,0, 11,11,16),
    null
  )).setRegistryName(new ResourceLocation(MODID, "thick_steel_pole"));

  public static final EdStraightPoleBlock THICK_STEEL_POLE_HEAD = (EdStraightPoleBlock)(new EdStraightPoleBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_FLIP_PLACEMENT_IF_SAME,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 11f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(5,5,0, 11,11,16),
    THICK_STEEL_POLE
  )).setRegistryName(new ResourceLocation(MODID, "thick_steel_pole_head"));

  public static final EdHorizontalSupportBlock STEEL_DOUBLE_T_SUPPORT = (EdHorizontalSupportBlock)(new EdHorizontalSupportBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 11f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB( 5,11,0, 11,16,16), // main beam
    Auxiliaries.getPixeledAABB(10,11,5, 16,16,11), // east beam (also for west 180deg)
    Auxiliaries.getPixeledAABB( 6, 0,6, 10,16,10), // down thin
    Auxiliaries.getPixeledAABB( 5, 0,5, 11,16,11)  // down thick
  )).setRegistryName(new ResourceLocation(MODID, "steel_double_t_support"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final DecorBlock.DirectedWaterLoggable SIGN_MODLOGO = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1000f).sound(SoundType.WOOD).lightLevel((state)->1).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,0,15.6, 16,16,16.0)
  )).setRegistryName(new ResourceLocation(MODID, "sign_decor"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_HOTWIRE = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_hotwire"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_DANGER = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_danger"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_DEFENSE = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_defense"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_FACTORY_AREA = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_factoryarea"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_EXIT = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(3,7,15.6, 13,13,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_exit"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_RADIOACTIVE = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_radioactive"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_LASER = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_laser"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_CAUTION = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_caution"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_MAGIC_HAZARD = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_magichazard"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_FIRE_HAZARD = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_firehazard"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_HOT_SURFACE = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_hotsurface"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_MAGNETIC_FIELD = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_magneticfield"));

  public static final DecorBlock.DirectedWaterLoggable SIGN_FROST_WARNING = (DecorBlock.DirectedWaterLoggable)(new DecorBlock.DirectedWaterLoggable(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_AI_PASSABLE,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 1f).sound(SoundType.WOOD).noOcclusion(),
    Auxiliaries.getPixeledAABB(2,2,15.6, 14,14,16)
  )).setRegistryName(new ResourceLocation(MODID, "sign_frost"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final EdCraftingTable.CraftingTableBlock CRAFTING_TABLE = (EdCraftingTable.CraftingTableBlock)(new EdCraftingTable.CraftingTableBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1f, 12f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(0,15,0, 16,16,16),
      Auxiliaries.getPixeledAABB(1, 0,1, 15,16,15)
    }
  )).setRegistryName(new ResourceLocation(MODID, "metal_crafting_table"));

  public static final FurnaceBlock SMALL_LAB_FURNACE = (FurnaceBlock)(new FurnaceBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1f, 12f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(1,0,1, 15, 1,15),
      Auxiliaries.getPixeledAABB(0,1,1, 16,16,16),
    }
  )).setRegistryName(new ResourceLocation(MODID, "small_lab_furnace"));

  public static final EdElectricalFurnace.ElectricalFurnaceBlock SMALL_ELECTRICAL_FURNACE = (EdElectricalFurnace.ElectricalFurnaceBlock)(new EdElectricalFurnace.ElectricalFurnaceBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 12f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(0, 0,0, 16,11,16),
      Auxiliaries.getPixeledAABB(1,11,0, 15,12,16),
      Auxiliaries.getPixeledAABB(2,12,0, 14,13,16),
      Auxiliaries.getPixeledAABB(3,13,0, 13,14,16),
      Auxiliaries.getPixeledAABB(4,14,0, 12,16,16),
    }
  )).setRegistryName(new ResourceLocation(MODID, "small_electrical_furnace"));

  public static final EdDropper.DropperBlock FACTORY_DROPPER = (EdDropper.DropperBlock)(new EdDropper.DropperBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 12f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,0,1, 16,16,16)
  )).setRegistryName(new ResourceLocation(MODID, "factory_dropper"));

  public static final EdPlacer.PlacerBlock FACTORY_PLACER = (EdPlacer.PlacerBlock)(new EdPlacer.PlacerBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_FLIP_PLACEMENT_SHIFTCLICK|DecorBlock.CFG_OPPOSITE_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 12f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(0,0,2, 16,16,16),
      Auxiliaries.getPixeledAABB( 0,0,0, 1,16, 2),
      Auxiliaries.getPixeledAABB(15,0,0,16,16, 2)
    }
  )).setRegistryName(new ResourceLocation(MODID, "factory_placer"));

  public static final EdBreaker.BreakerBlock SMALL_BLOCK_BREAKER = (EdBreaker.BreakerBlock)(new EdBreaker.BreakerBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT|DecorBlock.CFG_FLIP_PLACEMENT_SHIFTCLICK,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 12f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(1,0,0, 15, 4, 7),
      Auxiliaries.getPixeledAABB(1,0,7, 15,12,16),
      Auxiliaries.getPixeledAABB(0,0,0, 1, 5, 4),
      Auxiliaries.getPixeledAABB(0,0,4, 1,12,16),
      Auxiliaries.getPixeledAABB(15,0,0, 16, 5, 4),
      Auxiliaries.getPixeledAABB(15,0,4, 16,12,16)
    }
  )).setRegistryName(new ResourceLocation(MODID, "small_block_breaker"));

  public static final EdHopper.HopperBlock FACTORY_HOPPER = (EdHopper.HopperBlock)(new EdHopper.HopperBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 12f).sound(SoundType.METAL).noOcclusion(),()->{
      final AxisAlignedBB[] down_aabbs = new AxisAlignedBB[]{
        Auxiliaries.getPixeledAABB( 5, 0, 5, 11, 1,11),
        Auxiliaries.getPixeledAABB( 4, 1, 4, 12, 7,12),
        Auxiliaries.getPixeledAABB( 2, 7, 2, 14,10,14),
        Auxiliaries.getPixeledAABB( 0,10, 0, 16,16,16),
        Auxiliaries.getPixeledAABB( 0, 4, 5,  2,10,11),
        Auxiliaries.getPixeledAABB(14, 4, 5, 16,10,11),
        Auxiliaries.getPixeledAABB( 5, 4, 0, 11,10, 2),
        Auxiliaries.getPixeledAABB( 5, 4,14, 11,10,16),
      };
      final AxisAlignedBB[] up_aabbs = new AxisAlignedBB[]{
        Auxiliaries.getPixeledAABB( 5,15, 5, 11,16,11),
        Auxiliaries.getPixeledAABB( 4,14, 4, 12, 9,12),
        Auxiliaries.getPixeledAABB( 2, 9, 2, 14, 6,14),
        Auxiliaries.getPixeledAABB( 0, 6, 0, 16, 0,16),
        Auxiliaries.getPixeledAABB( 0,12, 5,  2, 6,11),
        Auxiliaries.getPixeledAABB(14,12, 5, 16, 6,11),
        Auxiliaries.getPixeledAABB( 5,12, 0, 11, 6, 2),
        Auxiliaries.getPixeledAABB( 5,12,14, 11, 6,16),
      };
      final AxisAlignedBB[] north_aabbs = new AxisAlignedBB[]{
        Auxiliaries.getPixeledAABB( 5, 0, 5, 11, 1,11),
        Auxiliaries.getPixeledAABB( 4, 1, 4, 12, 7,12),
        Auxiliaries.getPixeledAABB( 2, 7, 2, 14,10,14),
        Auxiliaries.getPixeledAABB( 0,10, 0, 16,16,16),
        Auxiliaries.getPixeledAABB( 0, 4, 5,  2,10,11),
        Auxiliaries.getPixeledAABB(14, 4, 5, 16,10,11),
        Auxiliaries.getPixeledAABB( 5, 1, 0, 11, 7, 4),
        Auxiliaries.getPixeledAABB( 5, 4,14, 11,10,16),
      };
      return new ArrayList<VoxelShape>(Arrays.asList(
        Auxiliaries.getUnionShape(down_aabbs),
        Auxiliaries.getUnionShape(up_aabbs),
        Auxiliaries.getUnionShape(Auxiliaries.getRotatedAABB(north_aabbs, Direction.NORTH, false)),
        Auxiliaries.getUnionShape(Auxiliaries.getRotatedAABB(north_aabbs, Direction.SOUTH, false)),
        Auxiliaries.getUnionShape(Auxiliaries.getRotatedAABB(north_aabbs, Direction.WEST, false)),
        Auxiliaries.getUnionShape(Auxiliaries.getRotatedAABB(north_aabbs, Direction.EAST, false)),
        VoxelShapes.block(),
        VoxelShapes.block()
      ));
    }
  )).setRegistryName(new ResourceLocation(MODID, "factory_hopper"));

  public static final EdWasteIncinerator.WasteIncineratorBlock SMALL_WASTE_INCINERATOR = (EdWasteIncinerator.WasteIncineratorBlock)(new EdWasteIncinerator.WasteIncineratorBlock(
    DecorBlock.CFG_DEFAULT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 12f).sound(SoundType.METAL),
    Auxiliaries.getPixeledAABB(0,0,0, 16,16,16)
  )).setRegistryName(new ResourceLocation(MODID, "small_waste_incinerator"));

  public static final EdMineralSmelter.MineralSmelterBlock SMALL_MINERAL_SMELTER = (EdMineralSmelter.MineralSmelterBlock)(new EdMineralSmelter.MineralSmelterBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 12f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(1.1,0,1.1, 14.9,16,14.9)
  )).setRegistryName(new ResourceLocation(MODID, "small_mineral_smelter"));

  public static final EdFreezer.FreezerBlock SMALL_FREEZER = (EdFreezer.FreezerBlock)(new EdFreezer.FreezerBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 12f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(1.1,0,1.1, 14.9,16,14.9)
  )).setRegistryName(new ResourceLocation(MODID, "small_freezer"));

  public static final EdSolarPanel.SolarPanelBlock SMALL_SOLAR_PANEL = (EdSolarPanel.SolarPanelBlock)(new EdSolarPanel.SolarPanelBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 10f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(0,0,0, 16,2,16),
      Auxiliaries.getPixeledAABB(6,1.5,3, 10,10.5,13),
    }
  )).setRegistryName(new ResourceLocation(MODID, "small_solar_panel"));

  public static final EdMilker.MilkerBlock SMALL_MILKING_MACHINE = (EdMilker.MilkerBlock)(new EdMilker.MilkerBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 10f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB( 1, 1,0, 15,14,10),
      Auxiliaries.getPixeledAABB( 0,14,0, 16,16,13),
      Auxiliaries.getPixeledAABB( 0, 0,0, 16, 1,13),
      Auxiliaries.getPixeledAABB( 0, 1,1,  1,14,11),
      Auxiliaries.getPixeledAABB(15, 1,1, 16,14,11)
    }
  )).setRegistryName(new ResourceLocation(MODID, "small_milking_machine"));

  public static final EdTreeCutter.TreeCutterBlock SMALL_TREE_CUTTER = (EdTreeCutter.TreeCutterBlock)(new EdTreeCutter.TreeCutterBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_FLIP_PLACEMENT_SHIFTCLICK,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 10f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB( 0,0, 0, 16,3,16),
      Auxiliaries.getPixeledAABB( 0,3, 0,  3,8,16),
      Auxiliaries.getPixeledAABB( 3,7, 0,  5,8,16),
      Auxiliaries.getPixeledAABB(15,0, 0, 16,6,16),
      Auxiliaries.getPixeledAABB( 0,0,13, 16,8,16),
      Auxiliaries.getPixeledAABB( 5,6,12, 16,8,13),
    }
  )).setRegistryName(new ResourceLocation(MODID, "small_tree_cutter"));

  public static final EdPipeValve.PipeValveBlock STRAIGHT_CHECK_VALVE = (EdPipeValve.PipeValveBlock)(new EdPipeValve.PipeValveBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT|DecorBlock.CFG_FLIP_PLACEMENT_SHIFTCLICK,
    EdPipeValve.CFG_CHECK_VALVE,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 8f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(2,2, 0, 14,14, 2),
      Auxiliaries.getPixeledAABB(2,2,14, 14,14,16),
      Auxiliaries.getPixeledAABB(3,3, 5, 13,13,11),
      Auxiliaries.getPixeledAABB(4,4, 2, 12,12,14),
    }
  )).setRegistryName(new ResourceLocation(MODID, "straight_pipe_valve"));

  public static final EdPipeValve.PipeValveBlock STRAIGHT_REDSTONE_VALVE = (EdPipeValve.PipeValveBlock)(new EdPipeValve.PipeValveBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT,
    EdPipeValve.CFG_REDSTONE_CONTROLLED_VALVE,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 8f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(2,2, 0, 14,14, 2),
      Auxiliaries.getPixeledAABB(2,2,14, 14,14,16),
      Auxiliaries.getPixeledAABB(3,3, 5, 13,13,11),
      Auxiliaries.getPixeledAABB(4,4, 2, 12,12,14),
    }
  )).setRegistryName(new ResourceLocation(MODID, "straight_pipe_valve_redstone"));

  public static final EdPipeValve.PipeValveBlock STRAIGHT_REDSTONE_ANALOG_VALVE = (EdPipeValve.PipeValveBlock)(new EdPipeValve.PipeValveBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_FACING_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT,
    EdPipeValve.CFG_REDSTONE_CONTROLLED_VALVE|EdPipeValve.CFG_ANALOG_VALVE,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 8f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(2,2, 0, 14,14, 2),
      Auxiliaries.getPixeledAABB(2,2,14, 14,14,16),
      Auxiliaries.getPixeledAABB(3,3, 5, 13,13,11),
      Auxiliaries.getPixeledAABB(4,4, 2, 12,12,14),
    }
  )).setRegistryName(new ResourceLocation(MODID, "straight_pipe_valve_redstone_analog"));

  public static final EdFluidBarrel.FluidBarrelBlock FLUID_BARREL = (EdFluidBarrel.FluidBarrelBlock)(new EdFluidBarrel.FluidBarrelBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 10f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(2, 0,0, 14, 1,16),
      Auxiliaries.getPixeledAABB(1, 1,0, 15, 2,16),
      Auxiliaries.getPixeledAABB(0, 2,0, 16,14,16),
      Auxiliaries.getPixeledAABB(1,14,0, 15,15,16),
      Auxiliaries.getPixeledAABB(2,15,0, 14,16,16),
    }
  )).setRegistryName(new ResourceLocation(MODID, "fluid_barrel"));

  public static final EdFluidFunnel.FluidFunnelBlock SMALL_FLUID_FUNNEL = (EdFluidFunnel.FluidFunnelBlock)(new EdFluidFunnel.FluidFunnelBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 10f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[]{
      Auxiliaries.getPixeledAABB(0, 0,0, 16,14,16),
      Auxiliaries.getPixeledAABB(1,14,1, 15,15,15),
      Auxiliaries.getPixeledAABB(0,15,0, 16,16,16)
    }
  )).setRegistryName(new ResourceLocation(MODID, "small_fluid_funnel"));

  public static final EdLabeledCrate.LabeledCrateBlock LABELED_CRATE = (EdLabeledCrate.LabeledCrateBlock)(new EdLabeledCrate.LabeledCrateBlock(
    DecorBlock.CFG_HORIZIONTAL|DecorBlock.CFG_LOOK_PLACEMENT|DecorBlock.CFG_OPPOSITE_PLACEMENT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(0.5f, 32f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,0,0, 16,16,16)
  )).setRegistryName(new ResourceLocation(MODID, "labeled_crate"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final EdSlabSliceBlock HALFSLAB_TREATEDWOOD = (EdSlabSliceBlock)(new EdSlabSliceBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HARD_IE_DEPENDENT,
    AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(1f, 4f).sound(SoundType.WOOD).noOcclusion()
  )).setRegistryName(new ResourceLocation(MODID, "halfslab_treated_wood"));

  public static final EdSlabSliceBlock HALFSLAB_SHEETMETALIRON = (EdSlabSliceBlock)(new EdSlabSliceBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HARD_IE_DEPENDENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1f, 8f).sound(SoundType.METAL).noOcclusion()
  )).setRegistryName(new ResourceLocation(MODID, "halfslab_sheetmetal_iron"));

  public static final EdSlabSliceBlock HALFSLAB_SHEETMETALSTEEL = (EdSlabSliceBlock)(new EdSlabSliceBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HARD_IE_DEPENDENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1f, 8f).sound(SoundType.METAL).noOcclusion()
  )).setRegistryName(new ResourceLocation(MODID, "halfslab_sheetmetal_steel"));

  public static final EdSlabSliceBlock HALFSLAB_SHEETMETALCOPPER = (EdSlabSliceBlock)(new EdSlabSliceBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HARD_IE_DEPENDENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1f, 8f).sound(SoundType.METAL).noOcclusion()
  )).setRegistryName(new ResourceLocation(MODID, "halfslab_sheetmetal_copper"));

  public static final EdSlabSliceBlock HALFSLAB_SHEETMETALGOLD = (EdSlabSliceBlock)(new EdSlabSliceBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HARD_IE_DEPENDENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1f, 8f).sound(SoundType.METAL).noOcclusion()
  )).setRegistryName(new ResourceLocation(MODID, "halfslab_sheetmetal_gold"));

  public static final EdSlabSliceBlock HALFSLAB_SHEETMETALALUMINIUM = (EdSlabSliceBlock)(new EdSlabSliceBlock(
    DecorBlock.CFG_CUTOUT|DecorBlock.CFG_HARD_IE_DEPENDENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1f, 8f).sound(SoundType.METAL).noOcclusion()
  )).setRegistryName(new ResourceLocation(MODID, "halfslab_sheetmetal_aluminum"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final EdFenceBlock STEEL_MESH_FENCE = (EdFenceBlock)(new EdFenceBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 10f).sound(SoundType.METAL).noOcclusion(),
    1.5, 16, 0.25, 0, 16, 16
  )).setRegistryName(new ResourceLocation(MODID, "steel_mesh_fence"));

  public static final EdDoubleGateBlock STEEL_MESH_FENCE_GATE = (EdDoubleGateBlock)(new EdDoubleGateBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 10f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,0,6.5, 16,16,9.5)
  )).setRegistryName(new ResourceLocation(MODID, "steel_mesh_fence_gate"));

  public static final EdRailingBlock STEEL_RAILING = (EdRailingBlock)(new EdRailingBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(1f, 10f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,0,0,  0, 0,0),
    Auxiliaries.getPixeledAABB(0,0,0, 16,15.9,1)
  )).setRegistryName(new ResourceLocation(MODID, "steel_railing"));

  public static final EdCatwalkBlock STEEL_CATWALK = (EdCatwalkBlock)(new EdCatwalkBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 10f).sound(SoundType.METAL).noOcclusion(),
    Auxiliaries.getPixeledAABB(0,0,0, 16, 2,16),
    Auxiliaries.getPixeledAABB(0,0,0, 16,15.9, 1),
    STEEL_RAILING
  )).setRegistryName(new ResourceLocation(MODID, "steel_catwalk"));

  public static final EdCatwalkTopAlignedBlock STEEL_CATWALK_TOP_ALIGNED = (EdCatwalkTopAlignedBlock)(new EdCatwalkTopAlignedBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 10f).sound(SoundType.METAL).noOcclusion(),
    new VoxelShape[]{
      VoxelShapes.create(Auxiliaries.getPixeledAABB(0,14,0, 16, 16,16)), // only base
      Auxiliaries.getUnionShape( // base with thick pole
        Auxiliaries.getPixeledAABB(0,14,0, 16, 16,16),
        Auxiliaries.getPixeledAABB(5, 0,5, 11,15, 11)
      ),
      Auxiliaries.getUnionShape( // base with thin pole
        Auxiliaries.getPixeledAABB(0,14,0, 16, 16,16),
        Auxiliaries.getPixeledAABB(6, 0,6, 10,15, 10)
      ),
      Auxiliaries.getUnionShape( // structure frame-like
        Auxiliaries.getPixeledAABB( 0, 0, 0, 16,  2,16),
        Auxiliaries.getPixeledAABB( 0,14, 0, 16, 16,16),
        Auxiliaries.getPixeledAABB( 0, 0, 0,  1, 16, 1),
        Auxiliaries.getPixeledAABB(15, 0, 0, 16, 16, 1),
        Auxiliaries.getPixeledAABB(15, 0,15, 16, 16,16),
        Auxiliaries.getPixeledAABB( 0, 0,15,  1, 16,16)
      )
    }
  )).setRegistryName(new ResourceLocation(MODID, "steel_catwalk_ta"));

  public static final EdCatwalkStairsBlock STEEL_CATWALK_STAIRS = (EdCatwalkStairsBlock)(new EdCatwalkStairsBlock(
    DecorBlock.CFG_CUTOUT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f, 10f).sound(SoundType.METAL).noOcclusion(),
    new AxisAlignedBB[] { // base
      Auxiliaries.getPixeledAABB( 1, 2, 8, 15,  4,  16),
      Auxiliaries.getPixeledAABB( 1,10, 0, 15, 12,   8),
    },
    new AxisAlignedBB[] { // railing left
      Auxiliaries.getPixeledAABB(0.4,  0, 15, 0.6, 15, 16),
      Auxiliaries.getPixeledAABB(0.4,  1, 14, 0.6, 16, 15),
      Auxiliaries.getPixeledAABB(0.4,  2, 13, 0.6, 17, 14),
      Auxiliaries.getPixeledAABB(0.4,  3, 12, 0.6, 18, 13),
      Auxiliaries.getPixeledAABB(0.4,  4, 11, 0.6, 19, 12),
      Auxiliaries.getPixeledAABB(0.4,  5, 10, 0.6, 20, 11),
      Auxiliaries.getPixeledAABB(0.4,  6,  9, 0.6, 21, 10),
      Auxiliaries.getPixeledAABB(0.4,  7,  8, 0.6, 22,  9),
      Auxiliaries.getPixeledAABB(0.4,  8,  7, 0.6, 23,  8),
      Auxiliaries.getPixeledAABB(0.4,  9,  6, 0.6, 24,  7),
      Auxiliaries.getPixeledAABB(0.4, 10,  5, 0.6, 25,  6),
      Auxiliaries.getPixeledAABB(0.4, 11,  4, 0.6, 26,  5),
      Auxiliaries.getPixeledAABB(0.4, 12,  3, 0.6, 27,  4),
      Auxiliaries.getPixeledAABB(0.4, 13,  2, 0.6, 28,  3),
      Auxiliaries.getPixeledAABB(0.4, 14,  1, 0.6, 29,  2),
      Auxiliaries.getPixeledAABB(0.4, 15,  0, 0.6, 30,  1)
    }
  )).setRegistryName(new ResourceLocation(MODID, "steel_catwalk_stairs"));

  // -------------------------------------------------------------------------------------------------------------------

  public static final EdTestBlock.TestBlock TEST_BLOCK = (EdTestBlock.TestBlock)(new EdTestBlock.TestBlock(
    DecorBlock.CFG_LOOK_PLACEMENT,
    AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(0f, 32000f).sound(SoundType.METAL),
    Auxiliaries.getPixeledAABB(0,0,0, 16,16,16)
  )).setRegistryName(new ResourceLocation(MODID, "test_block"));

  // -------------------------------------------------------------------------------------------------------------------

  private static final Block modBlocks[] = {
    CRAFTING_TABLE,
    LABELED_CRATE,
    SMALL_LAB_FURNACE,
    SMALL_ELECTRICAL_FURNACE,
    FACTORY_HOPPER,
    FACTORY_DROPPER,
    FACTORY_PLACER,
    SMALL_BLOCK_BREAKER,
    SMALL_TREE_CUTTER,
    SMALL_SOLAR_PANEL,
    SMALL_WASTE_INCINERATOR,
    SMALL_MINERAL_SMELTER,
    SMALL_FREEZER,
    SMALL_MILKING_MACHINE,
    FLUID_BARREL,
    STRAIGHT_CHECK_VALVE,
    STRAIGHT_REDSTONE_VALVE,
    STRAIGHT_REDSTONE_ANALOG_VALVE,
    SMALL_FLUID_FUNNEL,
    DENSE_GRIT_SAND,
    DENSE_GRIT_DIRT,
    CLINKER_BRICK_BLOCK,
    CLINKER_BRICK_SLAB,
    CLINKER_BRICK_STAIRS,
    CLINKER_BRICK_WALL,
    CLINKER_BRICK_SASTOR_CORNER,
    CLINKER_BRICK_STAINED_BLOCK,
    CLINKER_BRICK_STAINED_SLAB,
    CLINKER_BRICK_STAINED_STAIRS,
    CLINKER_BRICK_SASTOR_VERTICAL_SLOTTED,
    CLINKER_BRICK_RECESSED,
    CLINKER_BRICK_VERTICAL_SLAB_STRUCTURED,
    SLAG_BRICK_BLOCK,
    SLAG_BRICK_SLAB,
    SLAG_BRICK_STAIRS,
    SLAG_BRICK_WALL,
    REBAR_CONCRETE_BLOCK,
    REBAR_CONCRETE_SLAB,
    REBAR_CONCRETE_STAIRS,
    REBAR_CONCRETE_WALL,
    REBAR_CONCRETE_TILE,
    REBAR_CONCRETE_TILE_SLAB,
    REBAR_CONCRETE_TILE_STAIRS,
    GAS_CONCRETE_BLOCK,
    GAS_CONCRETE_SLAB,
    GAS_CONCRETE_STAIRS,
    GAS_CONCRETE_WALL,
    HALFSLAB_REBARCONCRETE,
    HALFSLAB_GASCONCRETE,
    HALFSLAB_TREATEDWOOD,
    HALFSLAB_SHEETMETALIRON,
    HALFSLAB_SHEETMETALSTEEL,
    HALFSLAB_SHEETMETALCOPPER,
    HALFSLAB_SHEETMETALGOLD,
    HALFSLAB_SHEETMETALALUMINIUM,
    PANZERGLASS_BLOCK,
    PANZERGLASS_SLAB,
    DARK_CERAMIC_SHINGLE_ROOF,
    DARK_CERAMIC_SHINGLE_ROOF_METALIZED,
    DARK_CERAMIC_SHINGLE_ROOF_SKYLIGHT,
    DARK_CERAMIC_SHINGLE_ROOF_CHIMNEYTRUNK,
    DARK_CERAMIC_SHINGLE_ROOF_WIRECONDUIT,
    DARK_CERAMIC_SHINGLE_ROOF_BLOCK,
    DARK_CERAMIC_SHINGLE_ROOF_SLAB,
    HALFSLAB_DARK_CERAMIC_SHINGLE_ROOF,
    DARK_CERAMIC_SHINGLE_ROOF_CHIMNEY,
    METAL_RUNG_LADDER,
    METAL_RUNG_STEPS,
    TREATED_WOOD_LADDER,
    METAL_SLIDING_DOOR,
    IRON_HATCH,
    OLD_INDUSTRIAL_PLANKS,
    OLD_INDUSTRIAL_SLAB,
    OLD_INDUSTRIAL_STAIRS,
    OLD_INDUSTRIAL_SLABSLICE,
    OLD_INDUSTRIAL_WOOD_DOOR,
    TREATED_WOOD_TABLE,
    TREATED_WOOD_STOOL,
    TREATED_WOOD_SIDE_TABLE,
    TREATED_WOOD_WINDOWSILL,
    TREATED_WOOD_BROAD_WINDOWSILL,
    TREATED_WOOD_WINDOW,
    STEEL_FRAMED_WINDOW,
    STEEL_TABLE,
    INSET_LIGHT_IRON,
    FLOOR_EDGE_LIGHT_IRON,
    CEILING_EDGE_LIGHT_IRON,
    BULB_LIGHT_IRON,
    STEEL_FLOOR_GRATING,
    STEEL_MESH_FENCE,
    STEEL_MESH_FENCE_GATE,
    STEEL_CATWALK,
    STEEL_RAILING,
    STEEL_CATWALK_TOP_ALIGNED,
    STEEL_CATWALK_STAIRS,
    TREATED_WOOD_POLE,
    TREATED_WOOD_POLE_HEAD,
    TREATED_WOOD_POLE_SUPPORT,
    THIN_STEEL_POLE,
    THIN_STEEL_POLE_HEAD,
    THICK_STEEL_POLE,
    THICK_STEEL_POLE_HEAD,
    STEEL_DOUBLE_T_SUPPORT,
    SIGN_HOTWIRE,
    SIGN_DANGER,
    SIGN_DEFENSE,
    SIGN_FACTORY_AREA,
    SIGN_EXIT,
    SIGN_RADIOACTIVE,
    SIGN_LASER,
    SIGN_CAUTION,
    SIGN_MAGIC_HAZARD,
    SIGN_FIRE_HAZARD,
    SIGN_HOT_SURFACE,
    SIGN_MAGNETIC_FIELD,
    SIGN_FROST_WARNING,
    SIGN_MODLOGO,
  };

  private static final Block devBlocks[] = {
    TEST_BLOCK
  };

  //--------------------------------------------------------------------------------------------------------------------
  // Tile entities bound exclusively to the blocks above
  //--------------------------------------------------------------------------------------------------------------------

  public static final TileEntityType<?> TET_CRAFTING_TABLE = TileEntityType.Builder
    .of(EdCraftingTable.CraftingTableTileEntity::new, CRAFTING_TABLE)
    .build(null)
    .setRegistryName(MODID, "te_treated_wood_crafting_table");

  public static final TileEntityType<?> TET_LABELED_CRATE = TileEntityType.Builder
    .of(EdLabeledCrate.LabeledCrateTileEntity::new, LABELED_CRATE)
    .build(null)
    .setRegistryName(MODID, "te_labeled_crate");

  public static final TileEntityType<?> TET_SMALL_LAB_FURNACE = TileEntityType.Builder
    .of(FurnaceTileEntity::new, SMALL_LAB_FURNACE)
    .build(null)
    .setRegistryName(MODID, "te_small_lab_furnace");

  public static final TileEntityType<?> TET_SMALL_ELECTRICAL_FURNACE = TileEntityType.Builder
    .of(EdElectricalFurnace.ElectricalFurnaceTileEntity::new, SMALL_ELECTRICAL_FURNACE)
    .build(null)
    .setRegistryName(MODID, "te_small_electrical_furnace");

  public static final TileEntityType<?> TET_FACTORY_DROPPER = TileEntityType.Builder
    .of(EdDropper.DropperTileEntity::new, FACTORY_DROPPER)
    .build(null)
    .setRegistryName(MODID, "te_factory_dropper");

  public static final TileEntityType<?> TET_FACTORY_PLACER = TileEntityType.Builder
    .of(EdPlacer.PlacerTileEntity::new, FACTORY_PLACER)
    .build(null)
    .setRegistryName(MODID, "te_factory_placer");

  public static final TileEntityType<?> TET_SMALL_BLOCK_BREAKER = TileEntityType.Builder
    .of(EdBreaker.BreakerTileEntity::new, SMALL_BLOCK_BREAKER)
    .build(null)
    .setRegistryName(MODID, "te_small_block_breaker");

  public static final TileEntityType<?> TET_FACTORY_HOPPER = TileEntityType.Builder
    .of(EdHopper.HopperTileEntity::new, FACTORY_HOPPER)
    .build(null)
    .setRegistryName(MODID, "te_factory_hopper");

  public static final TileEntityType<?> TET_WASTE_INCINERATOR = TileEntityType.Builder
    .of(EdWasteIncinerator.WasteIncineratorTileEntity::new, SMALL_WASTE_INCINERATOR)
    .build(null)
    .setRegistryName(MODID, "te_small_waste_incinerator");

  public static final TileEntityType<?> TET_STRAIGHT_PIPE_VALVE = TileEntityType.Builder
    .of(EdPipeValve.PipeValveTileEntity::new, STRAIGHT_CHECK_VALVE, STRAIGHT_REDSTONE_VALVE, STRAIGHT_REDSTONE_ANALOG_VALVE)
    .build(null)
    .setRegistryName(MODID, "te_pipe_valve");

  public static final TileEntityType<?> TET_FLUID_BARREL = TileEntityType.Builder
    .of(EdFluidBarrel.FluidBarrelTileEntity::new, FLUID_BARREL)
    .build(null)
    .setRegistryName(MODID, "te_fluid_barrel");

  public static final TileEntityType<?> TET_SMALL_FLUID_FUNNEL = TileEntityType.Builder
    .of(EdFluidFunnel.FluidFunnelTileEntity::new, SMALL_FLUID_FUNNEL)
    .build(null)
    .setRegistryName(MODID, "te_small_fluid_funnel");

  public static final TileEntityType<?> TET_MINERAL_SMELTER = TileEntityType.Builder
    .of(EdMineralSmelter.MineralSmelterTileEntity::new, SMALL_MINERAL_SMELTER)
    .build(null)
    .setRegistryName(MODID, "te_small_mineral_smelter");

  public static final TileEntityType<?> TET_FREEZER = TileEntityType.Builder
    .of(EdFreezer.FreezerTileEntity::new, SMALL_FREEZER)
    .build(null)
    .setRegistryName(MODID, "te_small_freezer");

  public static final TileEntityType<?> TET_SMALL_SOLAR_PANEL = TileEntityType.Builder
    .of(EdSolarPanel.SolarPanelTileEntity::new, SMALL_SOLAR_PANEL)
    .build(null)
    .setRegistryName(MODID, "te_small_solar_panel");

  public static final TileEntityType<?> TET_SMALL_MILKING_MACHINE = TileEntityType.Builder
    .of(EdMilker.MilkerTileEntity::new, SMALL_MILKING_MACHINE)
    .build(null)
    .setRegistryName(MODID, "te_small_milking_machine");

  public static final TileEntityType<?> TET_SMALL_TREE_CUTTER = TileEntityType.Builder
    .of(EdTreeCutter.TreeCutterTileEntity::new, SMALL_TREE_CUTTER)
    .build(null)
    .setRegistryName(MODID, "te_small_tree_cutter");

  public static final TileEntityType<?> TET_TEST_BLOCK = TileEntityType.Builder
    .of(EdTestBlock.TestTileEntity::new, TEST_BLOCK)
    .build(null)
    .setRegistryName(MODID, "te_test_block");

  private static final TileEntityType<?> tile_entity_types[] = {
    TET_CRAFTING_TABLE,
    TET_LABELED_CRATE,
    TET_SMALL_LAB_FURNACE,
    TET_SMALL_ELECTRICAL_FURNACE,
    TET_FACTORY_HOPPER,
    TET_FACTORY_DROPPER,
    TET_FACTORY_PLACER,
    TET_SMALL_BLOCK_BREAKER,
    TET_SMALL_TREE_CUTTER,
    TET_WASTE_INCINERATOR,
    TET_MINERAL_SMELTER,
    TET_FREEZER,
    TET_SMALL_SOLAR_PANEL,
    TET_SMALL_MILKING_MACHINE,
    TET_STRAIGHT_PIPE_VALVE,
    TET_FLUID_BARREL,
    TET_SMALL_FLUID_FUNNEL,
    TET_TEST_BLOCK
  };

  //--------------------------------------------------------------------------------------------------------------------
  // Items
  //--------------------------------------------------------------------------------------------------------------------

  private static Item.Properties default_item_properties()
  { return (new Item.Properties()).tab(ModEngineersDecor.ITEMGROUP); }

  public static final EdItem METAL_BAR_ITEM = (EdItem)((new EdItem(default_item_properties()).setRegistryName(MODID, "metal_bar")));

  private static final EdItem modItems[] = {
    METAL_BAR_ITEM
  };

  //--------------------------------------------------------------------------------------------------------------------
  // Entities bound exclusively to the blocks above
  //--------------------------------------------------------------------------------------------------------------------

  @SuppressWarnings("unchecked")
  public static final EntityType<EdChair.EntityChair> ET_CHAIR = (EntityType<EdChair.EntityChair>)(
    EntityType.Builder
      .of(EdChair.EntityChair::new, EntityClassification.MISC)
      .fireImmune().sized(1e-3f, 1e-3f).noSave()
      .setShouldReceiveVelocityUpdates(false).setUpdateInterval(4)
      .setCustomClientFactory(EdChair.EntityChair::customClientFactory)
      .build(new ResourceLocation(MODID, "et_chair").toString())
      .setRegistryName(new ResourceLocation(MODID, "et_chair"))
  );

  private static final EntityType<?> entity_types[] = {
    ET_CHAIR
  };

  //--------------------------------------------------------------------------------------------------------------------
  // Container registration
  //--------------------------------------------------------------------------------------------------------------------

  public static final ContainerType<EdCraftingTable.CraftingTableUiContainer> CT_TREATED_WOOD_CRAFTING_TABLE;
  public static final ContainerType<EdDropper.DropperUiContainer> CT_FACTORY_DROPPER;
  public static final ContainerType<EdPlacer.PlacerContainer> CT_FACTORY_PLACER;
  public static final ContainerType<EdHopper.HopperContainer> CT_FACTORY_HOPPER;
  public static final ContainerType<FurnaceContainer> CT_SMALL_LAB_FURNACE;
  public static final ContainerType<EdElectricalFurnace.ElectricalFurnaceContainer> CT_SMALL_ELECTRICAL_FURNACE;
  public static final ContainerType<EdWasteIncinerator.WasteIncineratorContainer> CT_WASTE_INCINERATOR;
  public static final ContainerType<EdLabeledCrate.LabeledCrateContainer> CT_LABELED_CRATE;

  static {
    CT_TREATED_WOOD_CRAFTING_TABLE = (new ContainerType<EdCraftingTable.CraftingTableUiContainer>(EdCraftingTable.CraftingTableUiContainer::new));
    CT_TREATED_WOOD_CRAFTING_TABLE.setRegistryName(MODID,"ct_treated_wood_crafting_table");
    CT_FACTORY_DROPPER = (new ContainerType<EdDropper.DropperUiContainer>(EdDropper.DropperUiContainer::new));
    CT_FACTORY_DROPPER.setRegistryName(MODID,"ct_factory_dropper");
    CT_FACTORY_PLACER = (new ContainerType<EdPlacer.PlacerContainer>(EdPlacer.PlacerContainer::new));
    CT_FACTORY_PLACER.setRegistryName(MODID,"ct_factory_placer");
    CT_FACTORY_HOPPER = (new ContainerType<EdHopper.HopperContainer>(EdHopper.HopperContainer::new));
    CT_FACTORY_HOPPER.setRegistryName(MODID,"ct_factory_hopper");
    CT_SMALL_LAB_FURNACE = (new ContainerType<FurnaceContainer>(FurnaceContainer::new));
    CT_SMALL_LAB_FURNACE.setRegistryName(MODID,"ct_small_lab_furnace");
    CT_SMALL_ELECTRICAL_FURNACE = (new ContainerType<EdElectricalFurnace.ElectricalFurnaceContainer>(EdElectricalFurnace.ElectricalFurnaceContainer::new));
    CT_SMALL_ELECTRICAL_FURNACE.setRegistryName(MODID,"ct_small_electrical_furnace");
    CT_WASTE_INCINERATOR = (new ContainerType<EdWasteIncinerator.WasteIncineratorContainer>(EdWasteIncinerator.WasteIncineratorContainer::new));
    CT_WASTE_INCINERATOR.setRegistryName(MODID,"ct_small_waste_incinerator");
    CT_LABELED_CRATE = (new ContainerType<EdLabeledCrate.LabeledCrateContainer>(EdLabeledCrate.LabeledCrateContainer::new));
    CT_LABELED_CRATE.setRegistryName(MODID,"ct_labeled_crate");
  }

  private static final ContainerType<?> container_types[] = {
    CT_TREATED_WOOD_CRAFTING_TABLE,
    CT_LABELED_CRATE,
    CT_FACTORY_DROPPER,
    CT_FACTORY_PLACER,
    CT_FACTORY_HOPPER,
    CT_SMALL_LAB_FURNACE,
    CT_SMALL_ELECTRICAL_FURNACE,
    CT_WASTE_INCINERATOR
  };

  //--------------------------------------------------------------------------------------------------------------------
  // Initialisation events
  //--------------------------------------------------------------------------------------------------------------------

  private static ArrayList<Block> registeredBlocks = new ArrayList<>();

  public static ArrayList<Block> allBlocks()
  {
    ArrayList<Block> blocks = new ArrayList<>();
    Collections.addAll(blocks, modBlocks);
    Collections.addAll(blocks, devBlocks);
    return blocks;
  }

  @SuppressWarnings("deprecation")
  public static boolean isExperimentalBlock(Block block)
  { return ArrayUtils.contains(devBlocks, block) || ((block instanceof IDecorBlock) && ((((IDecorBlock)block).config() & DecorBlock.CFG_EXPERIMENTAL))!=0); }

  @Nonnull
  public static List<Block> getRegisteredBlocks()
  { return Collections.unmodifiableList(registeredBlocks); }

  @Nonnull
  public static List<Item> getRegisteredItems()
  { return new ArrayList<>(); }

  public static final void registerBlocks(final RegistryEvent.Register<Block> event)
  {
    boolean ie_available = Auxiliaries.isModLoaded("immersiveengineering");
    if(ie_available) {
      Auxiliaries.logInfo("Immersive Engineering also installed ...");
      registeredBlocks.addAll(allBlocks());
    } else {
      registeredBlocks.addAll(allBlocks().stream()
        .filter(block->
          ((!(block instanceof IDecorBlock)) || ((((IDecorBlock)block).config() & DecorBlock.CFG_HARD_IE_DEPENDENT)==0))
        )
        .collect(Collectors.toList())
      );
    }
    for(Block e:registeredBlocks) event.getRegistry().register(e);
    Auxiliaries.logInfo("Registered " + Integer.toString(registeredBlocks.size()) + " blocks.");
  }

  public static final void registerBlockItems(final RegistryEvent.Register<Item> event)
  {
    int n = 0;
    for(Block e:registeredBlocks) {
      ResourceLocation rl = e.getRegistryName();
      if(rl == null) continue;
      if(e instanceof StandardBlocks.IBlockItemFactory) {
        event.getRegistry().register(((StandardBlocks.IBlockItemFactory)e).getBlockItem(e, (new Item.Properties().tab(ModEngineersDecor.ITEMGROUP))).setRegistryName(rl));
      } else {
        event.getRegistry().register(new BlockItem(e, (new Item.Properties().tab(ModEngineersDecor.ITEMGROUP))).setRegistryName(rl));
      }
      ++n;
    }
  }

  public static final void registerItems(final RegistryEvent.Register<Item> event)
  { for(Item e:modItems) event.getRegistry().register(e); }

  public static final void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event)
  {
    int n_registered = 0;
    for(final TileEntityType<?> e:tile_entity_types) {
      event.getRegistry().register(e);
      ++n_registered;
    }
    Auxiliaries.logInfo("Registered " + Integer.toString(n_registered) + " tile entities.");
  }

  public static final void registerEntities(final RegistryEvent.Register<EntityType<?>> event)
  {
    int n_registered = 0;
    for(final EntityType<?> e:entity_types) {
      if((e==ET_CHAIR) && (!registeredBlocks.contains(TREATED_WOOD_STOOL))) continue;
      event.getRegistry().register(e);
      ++n_registered;
    }
    Auxiliaries.logInfo("Registered " + Integer.toString(n_registered) + " entities bound to blocks.");
  }

  public static final void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
  {
    int n_registered = 0;
    for(final ContainerType<?> e:container_types) {
      event.getRegistry().register(e);
      ++n_registered;
    }
    Auxiliaries.logInfo("Registered " + Integer.toString(n_registered) + " containers bound to tile entities.");
  }

  @OnlyIn(Dist.CLIENT)
  public static final void registerContainerGuis(final FMLClientSetupEvent event)
  {
    ScreenManager.register(CT_TREATED_WOOD_CRAFTING_TABLE, EdCraftingTable.CraftingTableGui::new);
    ScreenManager.register(CT_LABELED_CRATE, EdLabeledCrate.LabeledCrateGui::new);
    ScreenManager.register(CT_FACTORY_DROPPER, EdDropper.DropperGui::new);
    ScreenManager.register(CT_FACTORY_PLACER, EdPlacer.PlacerGui::new);
    ScreenManager.register(CT_FACTORY_HOPPER, EdHopper.HopperGui::new);
    ScreenManager.register(CT_SMALL_LAB_FURNACE, FurnaceGui::new);
    ScreenManager.register(CT_SMALL_ELECTRICAL_FURNACE, EdElectricalFurnace.ElectricalFurnaceGui::new);
    ScreenManager.register(CT_WASTE_INCINERATOR, EdWasteIncinerator.WasteIncineratorGui::new);
  }

  @OnlyIn(Dist.CLIENT)
  @SuppressWarnings("unchecked")
  public static final void registerTileEntityRenderers(final FMLClientSetupEvent event)
  {
    ClientRegistry.bindTileEntityRenderer(
      (TileEntityType<EdCraftingTable.CraftingTableTileEntity>)TET_CRAFTING_TABLE,
      wile.engineersdecor.detail.ModRenderers.CraftingTableTer::new
    );
    ClientRegistry.bindTileEntityRenderer(
      (TileEntityType<EdLabeledCrate.LabeledCrateTileEntity>)TET_LABELED_CRATE,
      wile.engineersdecor.detail.ModRenderers.DecorLabeledCrateTer::new
    );
  }

  @OnlyIn(Dist.CLIENT)
  public static final void processContentClientSide(final FMLClientSetupEvent event)
  {
    // Block renderer selection
    for(Block block: getRegisteredBlocks()) {
      if(block instanceof IStandardBlock) {
        switch(((IStandardBlock)block).getRenderTypeHint()) {
          case CUTOUT:
            RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
            break;
          case CUTOUT_MIPPED:
            RenderTypeLookup.setRenderLayer(block, RenderType.cutoutMipped());
            break;
          case TRANSLUCENT:
            RenderTypeLookup.setRenderLayer(block, RenderType.translucent());
            break;
          case TRANSLUCENT_NO_CRUMBLING:
            RenderTypeLookup.setRenderLayer(block, RenderType.translucentNoCrumbling());
            break;
          case SOLID:
            break;
        }
      }
    }
    // Entity renderers
    RenderingRegistry.registerEntityRenderingHandler(ET_CHAIR,
      manager->(new wile.engineersdecor.detail.ModRenderers.InvisibleEntityRenderer<EdChair.EntityChair>(manager))
    );
  }

}
