package princess.tenergistics;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import princess.tenergistics.blocks.PlacedToolBlock;
import princess.tenergistics.blocks.SearedCoilBlock;
import princess.tenergistics.blocks.tileentity.PlacedToolTileEntity;
import princess.tenergistics.blocks.tileentity.SearedCoilTileEntity;
import princess.tenergistics.book.EnergisticsBookItem;
import princess.tenergistics.book.EnergisticsBookItem.EnergisticsBookType;
import princess.tenergistics.client.ToolTileEntityRenderer;
import princess.tenergistics.data.EnergisticsLootTableProvider;
import princess.tenergistics.data.EnergisticsMaterialProvider;
import princess.tenergistics.data.EnergisticsRecipeProvider;
import princess.tenergistics.data.TagProvider;
import princess.tenergistics.library.PoweredToolModifier;
import princess.tenergistics.modifiers.BlockingModifier;
import princess.tenergistics.modifiers.CapacityModifier;
import princess.tenergistics.modifiers.EnergyCoilModifier;
import princess.tenergistics.modifiers.ExchangerModifier;
import princess.tenergistics.modifiers.FireboxModifier;
import princess.tenergistics.modifiers.ForceFieldModifier;
import princess.tenergistics.modifiers.ForceFieldModifier.ForceEnergyCoilModifier;
import princess.tenergistics.modifiers.ForceFieldModifier.ForceExchangerModifier;
import princess.tenergistics.modifiers.ForceFieldModifier.ForceFireboxModifier;
import princess.tenergistics.modifiers.OverclockModifier;
import princess.tenergistics.modifiers.PassthroughModifier;
import princess.tenergistics.modifiers.PlaceToolModifier;
import princess.tenergistics.modifiers.RTGModifier;
import princess.tenergistics.modifiers.WideFunnelModifier;
import princess.tenergistics.modifiers.WidePrincessModifier;
import princess.tenergistics.recipes.RefuelFireboxRecipe;
import princess.tenergistics.tools.BucketwheelTool;
import princess.tenergistics.tools.BuzzsawTool;
import princess.tenergistics.tools.JackhammerTool;
import princess.tenergistics.tools.ToolDefinitions;
import princess.tenergistics.tools.stats.GearboxMaterialStats;
import princess.tenergistics.tools.stats.PercentageToolStat;
import slimeknights.mantle.registration.RegistrationHelper;
import slimeknights.mantle.registration.deferred.ContainerTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.TileEntityTypeDeferredRegister;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.registration.BlockDeferredRegisterExtension;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.item.ToolPartItem;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.shared.CommonsClientEvents;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

@Mod(TEnergistics.modID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TEnergistics
	{
	
	public static final String															modID									= "tenergistics";
	
	public static TEnergistics															instance;
	
	public static final Logger															log										= LogManager
			.getLogger(modID);
	
	protected static final BlockDeferredRegisterExtension								BLOCKS									= new BlockDeferredRegisterExtension(TEnergistics.modID);
	protected static final ItemDeferredRegisterExtension								ITEMS									= new ItemDeferredRegisterExtension(TEnergistics.modID);
	protected static final TileEntityTypeDeferredRegister								TILE_ENTITIES							= new TileEntityTypeDeferredRegister(TEnergistics.modID);
	protected static final ContainerTypeDeferredRegister								CONTAINERS								= new ContainerTypeDeferredRegister(TEnergistics.modID);
	protected static final EnergisticsFluidDeferredRegister								FLUIDS									= new EnergisticsFluidDeferredRegister(TEnergistics.modID);
	protected static final DeferredRegister<Modifier>									MODIFIERS								= DeferredRegister
			.create(Modifier.class, TEnergistics.modID);
	protected static final DeferredRegister<Attribute>									ATTRIBUTES								= DeferredRegister
			.create(ForgeRegistries.ATTRIBUTES, TEnergistics.modID);
	protected static final DeferredRegister<IRecipeSerializer<?>>						RECIPE_SERIALIZERS						= DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, TEnergistics.modID);
	protected static final DeferredRegister<SoundEvent>									SOUND_EVENTS							= DeferredRegister
			.create(ForgeRegistries.SOUND_EVENTS, TEnergistics.modID);
	
	@SuppressWarnings("unused")
	private static final Block.Properties												STONE									= builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL)
			.setRequiresTool()
			.hardnessAndResistance(3.0F, 9.0F);
	private static final Block.Properties												SMELTERY								= builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL)
			.setRequiresTool()
			.hardnessAndResistance(3.0F, 9.0F)
			.setAllowsSpawn((s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE)
					|| !s.get(SearedBlock.IN_STRUCTURE));
	private static final Block.Properties												TOOL_PROPERTIES							= builder(Material.ANVIL, null, SoundType.WOOD)
			.hardnessAndResistance(0.0F, 9.0F)
			.doesNotBlockMovement();
	private static final Supplier<Item.Properties>										TOOL									= () -> new Item.Properties()
			.group(TinkerTools.TAB_TOOLS);
	private static final Item.Properties												GENERAL_PROPS							= new Item.Properties()
			.group(TinkerModule.TAB_GENERAL);
	private static final Item.Properties												PARTS_PROPS								= new Item.Properties()
			.group(TinkerToolParts.TAB_TOOL_PARTS);
	private static final Item.Properties												SMELTERY_PROPS							= new Item.Properties()
			.group(TinkerSmeltery.TAB_SMELTERY);
	@SuppressWarnings("unused")
	private static final Item.Properties												GADGET_PROPS							= new Item.Properties()
			.group(TinkerGadgets.TAB_GADGETS);
	private static final Item.Properties												BOOK									= new Item.Properties()
			.group(TinkerModule.TAB_GENERAL)
			.maxStackSize(1);
	
	public static final SoundEvent														electricEvent							= new SoundEvent(new ResourceLocation(modID, "electric"));
	
	public static final ResourceLocation												ENERGY_STILL							= fluidTexture("energy", false);
	public static final ResourceLocation												ENERGY_FLOWING							= fluidTexture("energy", true);
	
	protected static final Function<Block, ? extends BlockItem>							SMELTERY_BLOCK_ITEM						= (b) -> new BlockItem(b, SMELTERY_PROPS);
	
	public static final ItemObject<SearedCoilBlock>										searedCoilBlock							= BLOCKS
			.register("seared_coil", () -> new SearedCoilBlock(SMELTERY), SMELTERY_BLOCK_ITEM);
	public static final ItemObject<SearedCoilBlock>										scorchedCoilBlock						= BLOCKS
			.register("scorched_coil", () -> new SearedCoilBlock(SMELTERY), SMELTERY_BLOCK_ITEM);
	public static final RegistryObject<TileEntityType<SearedCoilTileEntity>>			searedCoilTile							= TILE_ENTITIES
			.register("seared_coil", SearedCoilTileEntity::new, set -> {
																																		set.add(searedCoilBlock
																																				.get(), scorchedCoilBlock
																																						.get());
																																		});
	public static final FluidObject<ForgeFlowingFluid>									moltenEnergy							= FLUIDS
			.registerNoBucket("molten_energy", hotBuilder(ENERGY_STILL, ENERGY_FLOWING).temperature(1100)
					.density(3500)
					.sound(electricEvent, Sounds.DISCHARGE.getSound()), Material.LAVA, 14);
	
	public static final RegistryObject<PlacedToolBlock>									placedToolBlock							= BLOCKS
			.registerNoItem("placed_tool", () -> new PlacedToolBlock(TOOL_PROPERTIES));
	public static final RegistryObject<TileEntityType<PlacedToolTileEntity>>			placedToolTile							= TILE_ENTITIES
			.register("placed_tool", PlacedToolTileEntity::new, placedToolBlock);
	
	public static final ItemObject<EnergisticsBookItem>									miraculousMachinery						= ITEMS
			.register("miraculous_machinery", () -> new EnergisticsBookItem(BOOK, EnergisticsBookType.MIRACULOUS_MACHINERY));
	/*
	public static final ItemObject<ToolPartItem>										machineCasing							= ITEMS
			.register("machine_casing", () -> new ToolPartItem(PARTS_PROPS, ExtraMaterialStats.ID));*/
	public static final ItemObject<ToolPartItem>										toolCasing								= ITEMS
			.register("tool_casing", () -> new ToolPartItem(PARTS_PROPS, ExtraMaterialStats.ID));
	public static final ItemObject<ToolPartItem>										gearbox									= ITEMS
			.register("gearbox", () -> new ToolPartItem(PARTS_PROPS, GearboxMaterialStats.ID));
	
	public static final ItemObject<ToolPartItem>										jackhammerRod							= ITEMS
			.register("jackhammer_rod", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
	public static final ItemObject<ToolPartItem>										bucketwheelWheel						= ITEMS
			.register("bucketwheel_wheel", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
	public static final ItemObject<ToolPartItem>										buzzsawDisc								= ITEMS
			.register("buzzsaw_disc", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
	/*
	public static final CastItemObject													machineCasingCast						= ITEMS
			.registerCast("machine_casing", SMELTERY_PROPS);*/
	public static final CastItemObject													toolCasingCast							= ITEMS
			.registerCast("tool_casing", SMELTERY_PROPS);
	public static final CastItemObject													gearboxCast								= ITEMS
			.registerCast("gearbox", SMELTERY_PROPS);
	public static final CastItemObject													jackhammerRodCast						= ITEMS
			.registerCast("jackhammer_rod", SMELTERY_PROPS);
	public static final CastItemObject													bucketwheelWheelCast					= ITEMS
			.registerCast("bucketwheel_wheel", SMELTERY_PROPS);
	public static final CastItemObject													buzzsawDiscCast							= ITEMS
			.registerCast("buzzsaw_disc", SMELTERY_PROPS);
	
	public static final ItemObject<Item>												firebox									= ITEMS
			.register("firebox", GENERAL_PROPS);
	public static final ItemObject<Item>												exchanger								= ITEMS
			.register("exchanger", GENERAL_PROPS);
	public static final ItemObject<Item>												energyCoil								= ITEMS
			.register("energy_coil", GENERAL_PROPS);
	
	public static final ItemObject<JackhammerTool>										jackhammer								= ITEMS
			.register("jackhammer", () -> new JackhammerTool(TOOL.get()
					.addToolType(ToolType.PICKAXE, 0), ToolDefinitions.JACKHAMMER));
	
	public static final ItemObject<BucketwheelTool>										bucketwheel								= ITEMS
			.register("bucketwheel", () -> new BucketwheelTool(TOOL.get()
					.addToolType(ToolType.SHOVEL, 0), ToolDefinitions.BUCKETWHEEL));
	
	public static final ItemObject<BuzzsawTool>											buzzsaw									= ITEMS
			.register("buzzsaw", () -> new BuzzsawTool(TOOL.get()
					.addToolType(ToolType.AXE, 0), ToolDefinitions.BUZZSAW));
	
	public static final RegistryObject<PoweredToolModifier>								poweredToolModifier						= MODIFIERS
			.register("powered_internal", PoweredToolModifier::new);
	
	public static final RegistryObject<FireboxModifier>									fireboxModifier							= MODIFIERS
			.register("firebox", FireboxModifier::new);
	public static final RegistryObject<ExchangerModifier>								exchangerModifier						= MODIFIERS
			.register("exchanger", ExchangerModifier::new);
	public static final RegistryObject<EnergyCoilModifier>								energyCoilModifier						= MODIFIERS
			.register("energy_coil", EnergyCoilModifier::new);
	
	public static final RegistryObject<CapacityModifier>								capacityModifier						= MODIFIERS
			.register("capacity", () -> new CapacityModifier(0x6bdbdb));
	public static final RegistryObject<CapacityModifier>								capacityTrait							= MODIFIERS
			.register("capacity_trait", () -> new CapacityModifier(0x3ce186));
	public static final RegistryObject<OverclockModifier>								overclockModifier						= MODIFIERS
			.register("overclock", () -> new OverclockModifier(0x50e0ff));
	public static final RegistryObject<OverclockModifier>								overclockTrait							= MODIFIERS
			.register("overclock_trait", () -> new OverclockModifier(0xe8806c));
	public static final RegistryObject<WideFunnelModifier>								wideFunnelModifier						= MODIFIERS
			.register("wide_funnel", WideFunnelModifier::new);
	public static final RegistryObject<PassthroughModifier>								passthroughModifier						= MODIFIERS
			.register("passthrough", PassthroughModifier::new);
	public static final RegistryObject<RTGModifier>										rtgModifier								= MODIFIERS
			.register("rtg", RTGModifier::new);
	public static final RegistryObject<WidePrincessModifier>							wideChiselModifier						= MODIFIERS
			.register("wide_chisel", WidePrincessModifier::new);
	
	public static final RegistryObject<ForceFieldModifier>								forceFieldModifier						= MODIFIERS
			.register("force_field", ForceFieldModifier::new);
	public static final RegistryObject<ForceFireboxModifier>							forceFireboxModifier					= MODIFIERS
			.register("force_firebox", ForceFireboxModifier::new);
	public static final RegistryObject<ForceExchangerModifier>							forceExchangerModifier					= MODIFIERS
			.register("force_exchanger", ForceExchangerModifier::new);
	public static final RegistryObject<ForceEnergyCoilModifier>							forceEnergyCoilModifier					= MODIFIERS
			.register("force_energy_coil", ForceEnergyCoilModifier::new);
	
	public static final RegistryObject<PlaceToolModifier>								placeToolModifier						= MODIFIERS
			.register("place_tool", PlaceToolModifier::new);
	
	public static final RegistryObject<BlockingModifier>								blockingModifier						= MODIFIERS
			.register("blocking", BlockingModifier::new);
	
	public static final RegistryObject<Attribute>										FAKE_HARVEST_SPEED						= ATTRIBUTES
			.register("generic.fake_harvest_speed", () -> new RangedAttribute(modID + ".attribute.name.generic.fake_harvest_speed", 1.0D, 0.0D, 2048.0D));
	
	public static final RegistryObject<SpecialRecipeSerializer<RefuelFireboxRecipe>>	tinkerStationFireboxRefuelSerializer	= RECIPE_SERIALIZERS
			.register("tinker_station_firebox_refuel", () -> new SpecialRecipeSerializer<>(RefuelFireboxRecipe::new));
	
	public static final RegistryObject<SoundEvent>										electric								= SOUND_EVENTS
			.register("electric", () -> electricEvent);
	
	public static final PercentageToolStat												EFFICIENCY								= ToolStats
			.register(new PercentageToolStat(new ToolStatId(modID, "efficiency"), 0xff_21dffc, 1, 0, 1024f));
	public static final FloatToolStat													MACHINE_SPEED							= ToolStats
			.register(new FloatToolStat(new ToolStatId(modID, "machine_speed"), 0xff_fc3b21, 0, 0, 2048f));
	
	public TEnergistics()
		{
		instance = this;
		
		initRegisters();
		
		MinecraftForge.EVENT_BUS.register(this);
		
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> EnergisticsClient::onConstruct);
		}
		
	public static void initRegisters()
		{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		BLOCKS.register(bus);
		ITEMS.register(bus);
		TILE_ENTITIES.register(bus);
		CONTAINERS.register(bus);
		FLUIDS.register(bus);
		MODIFIERS.register(bus);
		ATTRIBUTES.register(bus);
		RECIPE_SERIALIZERS.register(bus);
		SOUND_EVENTS.register(bus);
		}
		
	@SubscribeEvent
	static void commonSetup(final FMLCommonSetupEvent event)
		{
		event.enqueueWork(() -> {
		if (MaterialRegistry.initialized())
			{
			MaterialRegistry.getInstance().registerStatType(GearboxMaterialStats.DEFAULT, GearboxMaterialStats.class);
			}
		});
		}
		
	@Nullable
	private static Block missingBlock(String name)
		{
		switch (name)
			{
			case "charger":
				return searedCoilBlock.get();
			}
		return null;
		}
		
	@SubscribeEvent
	void missingBlocks(final MissingMappings<Block> event)
		{
		RegistrationHelper.handleMissingMappings(event, modID, TEnergistics::missingBlock);
		}
		
	@SubscribeEvent
	void missingItems(final MissingMappings<Item> event)
		{
		RegistrationHelper.handleMissingMappings(event, modID, name -> {
		switch (name)
			{
			}
		IItemProvider block = missingBlock(name);
		return block == null ? null : block.asItem();
		});
		}
		
	@SubscribeEvent
	static void gatherData(final GatherDataEvent event)
		{
		if (event.includeServer())
			{
			DataGenerator generator = event.getGenerator();
			ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
			
			TagProvider blockTagProvider = new TagProvider(generator, modID, existingFileHelper);
			generator.addProvider(blockTagProvider);
			generator.addProvider(new TagProvider.ItemTag(generator, blockTagProvider, modID, existingFileHelper));
			generator.addProvider(new TagProvider.FluidTag(generator, modID, existingFileHelper));
			
			EnergisticsMaterialProvider materials = new EnergisticsMaterialProvider(generator);
			generator.addProvider(materials);
			generator.addProvider(new EnergisticsMaterialProvider.StatsProvider(generator, materials));
			generator.addProvider(new EnergisticsMaterialProvider.TraitProvider(generator, materials));
			
			generator.addProvider(new EnergisticsRecipeProvider(generator));
			generator.addProvider(new EnergisticsLootTableProvider(generator));
			}
		}
		
	protected static Block.Properties builder(Material material, @Nullable ToolType toolType, SoundType soundType)
		{
		//noinspection ConstantConditions
		return Block.Properties.create(material).harvestTool(toolType).sound(soundType);
		}
		
	private static FluidAttributes.Builder hotBuilder(ResourceLocation stillTexture, ResourceLocation flowingTexture)
		{
		return FluidAttributes.builder(stillTexture, flowingTexture).density(2000).viscosity(10000).temperature(1000);
		}
		
	private static ResourceLocation fluidTexture(String name, boolean flowing)
		{
		return new ResourceLocation(TEnergistics.modID, ("block/fluid/" + name + (flowing ? "_flowing" : "_still")));
		}
		
	@EventBusSubscriber(modid = modID, value = Dist.CLIENT, bus = Bus.MOD)
	public static class EnergisticsClient
		{
		@SubscribeEvent
		static void clientSetup(final FMLClientSetupEvent event)
			{
			FontRenderer unicode = CommonsClientEvents.unicodeFontRender();
			EnergisticsBookItem.EnergisticsBook.MIRACULOUS_MACHINERY.fontRenderer = unicode;
			
			ClientRegistry.bindTileEntityRenderer(TEnergistics.placedToolTile.get(), ToolTileEntityRenderer::new);
			}
			
		public static void onConstruct()
			{
			EnergisticsBookItem.EnergisticsBook.initBook();
			}
			
		@SubscribeEvent
		static void itemColors(ColorHandlerEvent.Item event)
			{
			
			final ItemColors colors = event.getItemColors();
			
			// tint tool textures for fallback
			registerToolItemColors(colors, jackhammer);
			registerToolItemColors(colors, bucketwheel);
			registerToolItemColors(colors, buzzsaw);
			
			registerMaterialItemColors(colors, toolCasing);
			registerMaterialItemColors(colors, gearbox);
			registerMaterialItemColors(colors, jackhammerRod);
			registerMaterialItemColors(colors, bucketwheelWheel);
			registerMaterialItemColors(colors, buzzsawDisc);
			}
			
		private static void registerMaterialItemColors(ItemColors colors, Supplier<? extends MaterialItem> item)
			{
			colors.register(materialColorHandler, item.get());
			}
			
		private static void registerToolItemColors(ItemColors colors, Supplier<? extends ToolCore> item)
			{
			colors.register(toolColorHandler, item.get());
			}
			
		private static final IItemColor	materialColorHandler	= (stack, index) -> {
																return Optional
																		.of(IMaterialItem.getMaterialFromStack(stack))
																		.filter((material) -> IMaterial.UNKNOWN != material)
																		.map(IMaterial::getIdentifier)
																		.flatMap(MaterialRenderInfoLoader.INSTANCE::getRenderInfo)
																		.map(MaterialRenderInfo::getVertexColor)
																		.orElse(-1);
																};
		private static final IItemColor	toolColorHandler		= (stack, index) -> {
																MaterialId material = MaterialIdNBT.from(stack)
																		.getMaterial(index);
																if (!IMaterial.UNKNOWN_ID.equals(material))
																	{
																	return MaterialRenderInfoLoader.INSTANCE
																			.getRenderInfo(material)
																			.map(MaterialRenderInfo::getVertexColor)
																			.orElse(-1);
																	}
																return -1;
																
																};
		}
	}
