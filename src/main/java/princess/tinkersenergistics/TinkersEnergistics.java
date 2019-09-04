package princess.tinkersenergistics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import princess.tinkersenergistics.block.BlockMachine;
import princess.tinkersenergistics.block.tile.TileMachine;
import princess.tinkersenergistics.container.GuiHandler;
import princess.tinkersenergistics.item.ItemDust;
import princess.tinkersenergistics.item.MachineCrusher;
import princess.tinkersenergistics.item.MachineFurnace;
import princess.tinkersenergistics.item.MachineModPart;
import princess.tinkersenergistics.item.MachinePart;
import princess.tinkersenergistics.library.AbstractTinkerPulse;
import princess.tinkersenergistics.library.ModInfo;
import princess.tinkersenergistics.modifiers.MachineTrait;
import princess.tinkersenergistics.modifiers.ModDouble;
import princess.tinkersenergistics.modifiers.ModMachinePart;
import princess.tinkersenergistics.modifiers.ModMachinePartDisplay;
import princess.tinkersenergistics.modifiers.ModOverclock;
import princess.tinkersenergistics.proxy.CommonProxy;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.common.ModelRegisterUtil;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.library.fluid.FluidColored;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierAspect.CategoryAspect;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.shared.TinkerCommons;

/**For the brave souls who get this far: You are the chosen ones,
*  the valiant knights of programming who toil away, without rest,
*  fixing our most awful code. To you, true saviors, kings of men,
*  I say this: never gonna give you up, never gonna let you down,
*  never gonna run around and desert you. Never gonna make you cry,
*  never gonna say goodbye. Never gonna tell a lie and hurt you.
*/
@Pulse(id = ModInfo.MODID, description = ModInfo.DESCRIPTION, forced = ModInfo.TRUE, modsRequired = ModInfo.DEPEND)
public class TinkersEnergistics extends AbstractTinkerPulse
	{
	@SidedProxy(clientSide = ModInfo.CLIENTPROXY, serverSide = ModInfo.COMMONPROXY)
	public static CommonProxy			proxy;
	
	public static final Category		TIE_MACHINE	= new Category("tie_machine");
	public static final Category		TIE_CRUSHER	= new Category("tie_crusher");
	
	public static final ModifierAspect	machineOnly	= new CategoryAspect(TIE_MACHINE);
	
	public static final String			One			= "CHAOS";
	
	public static ToolCore				furnace;
	public static ToolCore				crusher;
	/*
	public static ToolCore				converter;
	*/
	
	public static ToolPart				machineCasing;
	public static ToolPart				machineGearbox;
	
	public static ToolPart				machineHeater;
	public static ToolPart				machineMill;
	
	public static ToolPart				machineFirebox;
	public static ToolPart				machineExchanger;
	public static ToolPart				machineCoil;
	
	public static Item					carbonBall;
	
	public static ItemDust				metalDust;
	
	public static Block					machineBlock;
	
	public static Modifier				modOverclock;
	public static Modifier				modDouble;
	
	public static List<Modifier>		fireboxMods;
	
	public final static ItemStack[]		dusts		= new ItemStack[64];
	
	//public static final Material		gold		= mat("gold", 0xf6d609, true);
	
	static
		{
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
			{
			TinkerBook.INSTANCE.addRepository(new FileRepository("tenergistics:book"));
			}
		}
		
	@SubscribeEvent
	public void registerBlocks(Register<Block> event)
		{
		IForgeRegistry<Block> registry = event.getRegistry();
		
		machineBlock = registerBlock(registry, new BlockMachine(), "machine_block");
		registerTE(TileMachine.class, "machine_tile");
		}
		
	@SubscribeEvent
	public void registerItems(Register<Item> event)
		{
		IForgeRegistry<Item> registry = event.getRegistry();
		
		machineCasing = registerToolPart(registry, new MachinePart(Material.VALUE_Ingot * 8, 0), "machine_casing");
		machineGearbox = registerToolPart(registry, new MachinePart(Material.VALUE_Ingot * 4, 0), "machine_gearbox");
		
		machineHeater = registerToolPart(registry, new MachinePart(Material.VALUE_Ingot * 2, 0), "machine_heater");
		machineMill = registerToolPart(registry, new MachinePart(Material.VALUE_Ingot * 2, 1), "machine_mill");
		
		machineFirebox = registerToolPart(registry, new MachineModPart(Material.VALUE_Ingot * 1, 0), "machine_firebox");
		machineExchanger = registerToolPart(registry, new MachineModPart(Material.VALUE_Ingot * 1, 1), "machine_exchanger");
		machineCoil = registerToolPart(registry, new MachineModPart(Material.VALUE_Ingot * 1, 2), "machine_coil");
		
		TinkerRegistry.registerToolPart(machineFirebox);
		machineFirebox.setCreativeTab(TinkerRegistry.tabParts);
		TinkerRegistry.registerToolPart(machineExchanger);
		machineExchanger.setCreativeTab(TinkerRegistry.tabParts);
		TinkerRegistry.registerToolPart(machineCoil);
		machineCoil.setCreativeTab(TinkerRegistry.tabParts);
		
		furnace = registerTool(registry, new MachineFurnace(), "machine_furnace");
		crusher = registerTool(registry, new MachineCrusher(), "machine_crusher");
		
		metalDust = registerItem(registry, new ItemDust(), "dusts");
		metalDust.setCreativeTab(TinkerRegistry.tabGeneral);
		}
		
	private void registerToolBuilding()
		{
		TinkerRegistry.registerToolForgeCrafting(furnace);
		TinkerRegistry.registerToolForgeCrafting(crusher);
		}
		
	private void registerMaterials()
		{
		/* Materials with no stats are essentially for render only - code does not allow for statless tools.
		 * We add them on server anyway.
		 */
		/*
		gold.addCommonItems("Gold");
		gold.setRepresentativeItem(new ItemStack(Items.GOLD_INGOT));
		
		TinkerRegistry.integrate(gold, TinkerFluids.gold, "Gold");*/
		}
		
	private void registerModifiers()
		{
		modOverclock = registerModifier(new ModOverclock(50));
		modDouble = registerModifier(new ModDouble(25));
		}
		
	@Subscribe
	public void preInit(FMLPreInitializationEvent event)
		{
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		/*
		TinkerRegistry.addMaterial(gold);
		*/
		for (Pair<Item, ToolPart> toolPartPattern : toolPartPatterns)
			{
			registerStencil(toolPartPattern.getLeft(), toolPartPattern.getRight());
			}
			
		NetworkRegistry.INSTANCE.registerGuiHandler(ModRegistryHelper.INSTANCE, new GuiHandler());
		
		TinkerRegistry.addTrait(MachineTrait.INSTANCE);
		
		registerModifiers();
		
		Fluid myFluid = new FluidColored("fluff", 0xFF00FF, new ResourceLocation("tconstruct:blocks/fluids/molten_metal"), new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow"));		FluidRegistry.registerFluid(myFluid);		FluidRegistry.addBucketForFluid(myFluid);

		NBTTagList alloysTagList = new NBTTagList();
		NBTTagCompound fluid = new NBTTagCompound();
		fluid.setString("FluidName", "fluff");
		fluid.setInteger("Amount", 1000);
		alloysTagList.appendTag(fluid);

		fluid = new NBTTagCompound();
		fluid.setString("FluidName", "lava");
		fluid.setInteger("Amount", 1000);
		alloysTagList.appendTag(fluid);

		fluid = new NBTTagCompound();
		fluid.setString("FluidName", "iron");
		fluid.setInteger("Amount", 144);
		alloysTagList.appendTag(fluid);

		NBTTagCompound tag = new NBTTagCompound();

		tag.setString("ore", "Fluff");
		tag.setTag("alloy", alloysTagList);
		tag.setString("fluid", myFluid.getName());

		FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
		
		proxy.preInit(event);
		}
		
	@Subscribe
	public void init(FMLInitializationEvent event)
		{
		registerToolBuilding();
		registerMaterials();
		
		GameRegistry.addSmelting(Blocks.STONE, new ItemStack(Blocks.SAND, 2), 0.4f);
		
		proxy.init(event);
		}
		
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event)
		{
		proxy.registerModels();
		}
		
	@Subscribe
	public void postInit(FMLPostInitializationEvent event)
		{
		/* testing testing 1 2 3
		if (Loader.isModLoaded("tconstruct"))
			{
			GameRegistry.addShapelessRecipe(new ResourceLocation(ModInfo.MODID,"axeToBooks"), new ResourceLocation(ModInfo.MODID,"axe"), new ItemStack(Item.getByNameOrId("tconstruct:book"),1,0), new OreIngredient("toolAx"));
			}
		*/
		
		
		fireboxMods = Lists.newArrayList();
		for (Material mat : TinkerRegistry.getAllMaterialsWithStats(MaterialTypes.HEAD))
			{
			fireboxMods.add(new ModMachinePart(mat, "firebox", machineFirebox));
			}
			
		ItemDust.initializeDust();
		ItemDust.initializeDustRecipes(); // If someone *still* didn't add their ores to oredict that's not our problem.
		
		modOverclock.addRecipeMatch(new RecipeMatch.ItemCombination(1, new ItemStack(Items.REDSTONE), new ItemStack(Items.GUNPOWDER)));
		modOverclock.addRecipeMatch(new RecipeMatch.ItemCombination(9, new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(Items.GUNPOWDER, 9)));
		
		modDouble.addRecipeMatch(new RecipeMatch.ItemCombination(1, TinkerCommons.matSlimeBallPurple.copy(), new ItemStack(Items.ENDER_PEARL)));
		
		proxy.postInit(event);
		}
		
	public static class ClientProxy extends CommonProxy
		{
		
		@Override
		public void preInit(FMLPreInitializationEvent event)
			{
			super.preInit(event);
			}
			
		@Override
		public void registerModels()
			{
			toolparts.forEach(ModelRegisterUtil::registerPartModel);
			tools.forEach(ModelRegisterUtil::registerToolModel);
			
			List<IModifier> mods = new ArrayList<>();
			
			mods.add(modOverclock);
			mods.add(modDouble);
			
			for (IModifier modifier : mods)
				ModelRegisterUtil.registerModifierModel(modifier, new ResourceLocation(ModInfo.MODID, "models/item/modifiers/" + modifier.getIdentifier()));
			
			ModelRegisterUtil.registerMaterialItemModel(metalDust);
			
			ModelRegisterUtil.registerModifierModel(new ModMachinePartDisplay("firebox", machineFirebox), new ResourceLocation(ModInfo.MODID, "models/item/modifiers/firebox"));
			}
			
		@Override
		public void postInit(FMLPostInitializationEvent event)
			{
			super.postInit(event);
			
			ToolBuildGuiInfo info = new ToolBuildGuiInfo(furnace);
			info.addSlotPosition(30, 44);
			info.addSlotPosition(7, 64);
			info.addSlotPosition(30, 64);
			TinkerRegistryClient.addToolBuilding(info);
			
			ToolBuildGuiInfo info2 = new ToolBuildGuiInfo(crusher);
			info2.addSlotPosition(30, 44);
			info2.addSlotPosition(7, 64);
			info2.addSlotPosition(30, 64);
			TinkerRegistryClient.addToolBuilding(info2);
			}
			
		@Override
		public boolean isServer()
			{
			return false;
			}
		}
		
	public static final EnumRarity	SKAIAN	= EnumHelper.addRarity("tie_skaian", TextFormatting.GREEN, "Skaian");
	public static final EnumRarity	DIAMOND	= EnumHelper.addRarity("tie_diamond", TextFormatting.AQUA, "Diamond");
	}