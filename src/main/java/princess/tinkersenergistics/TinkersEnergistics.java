package princess.tinkersenergistics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import princess.tinkersenergistics.block.BlockMachine;
import princess.tinkersenergistics.block.tile.TileMachine;
import princess.tinkersenergistics.container.GuiHandler;
import princess.tinkersenergistics.item.ItemDust;
import princess.tinkersenergistics.item.MachineCrusher;
import princess.tinkersenergistics.item.MachineFurnace;
import princess.tinkersenergistics.item.MachinePart;
import princess.tinkersenergistics.library.AbstractTinkerPulse;
import princess.tinkersenergistics.library.ModInfo;
import princess.tinkersenergistics.modifiers.MachineTrait;
import princess.tinkersenergistics.modifiers.ModDouble;
import princess.tinkersenergistics.modifiers.ModOverclock;
import princess.tinkersenergistics.proxy.CommonProxy;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.common.ModelRegisterUtil;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;

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
	public static CommonProxy		proxy;
	
	public static final Category	TIE_MACHINE	= new Category("tie_machine");
	public static final Category	TIE_CRUSHER	= new Category("tie_crusher");
	
	public static final String		One			= "CHAOS";
	
	public static ToolCore			furnace;
	public static ToolCore			crusher;
	/*
	public static ToolCore			converter;
	*/
	
	public static ToolPart			machineCasing;
	public static ToolPart			machineGearbox;
	
	public static ToolPart			machineHeater;
	public static ToolPart			machineMill;
	/*	
	public static ToolPart			machineFirebox;
	public static ToolPart			machineExchanger;
	public static ToolPart			machineCoil;
	
	public static Item				carbonBall;
	*/
	public static ItemDust			metalDust;
	
	public static Block				machineBlock;
	
	public static Modifier			modOverclock;
	public static Modifier			modDouble;
	
	public final static ItemStack[]	dusts		= new ItemStack[64];
	
	private void registerMachines()
		{
		machineBlock = registerBlock(new BlockMachine(), "machine_block");
		registerTE(TileMachine.class, "machine_tile");
		
		furnace = registerTool(new MachineFurnace(), "machine_furnace");
		crusher = registerTool(new MachineCrusher(), "machine_crusher");
		}
		
	private void registerMachineParts()
		{
		machineCasing = registerToolPart(new MachinePart(Material.VALUE_Ingot * 8, 0), "machine_casing");
		machineGearbox = registerToolPart(new MachinePart(Material.VALUE_Ingot * 4, 0), "machine_gearbox");
		
		machineHeater = registerToolPart(new MachinePart(Material.VALUE_Ingot * 2, 0), "machine_heater");
		machineMill = registerToolPart(new MachinePart(Material.VALUE_Ingot * 2, 1), "machine_mill");
		
		/*
		machineFirebox = registerToolPart(new MachineModPart(Material.VALUE_Ingot * 1), "machine_firebox");
		machineExchanger = registerToolPart(new MachineModPart(Material.VALUE_Ingot * 1), "machine_exchanger");
		machineCoil = registerToolPart(new MachineModPart(Material.VALUE_Ingot * 1), "machine_coil");
		*/
		
		metalDust = registerItem(new ItemDust(), "dusts");
		metalDust.setCreativeTab(TinkerRegistry.tabGeneral);
		}
		
	private void registerToolBuilding()
		{
		TinkerRegistry.registerToolForgeCrafting(furnace);
		TinkerRegistry.registerToolForgeCrafting(crusher);
		}
		
	private void registerModifiers()
		{
		modOverclock = registerModifier(new ModOverclock(50));
		modOverclock.addRecipeMatch(new RecipeMatch.ItemCombination(1, new ItemStack(Items.REDSTONE), new ItemStack(Items.GUNPOWDER)));
		modOverclock.addRecipeMatch(new RecipeMatch.ItemCombination(9, new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(Items.GUNPOWDER, 9)));
		
		modDouble = registerModifier(new ModDouble(25));
		modDouble.addRecipeMatch(new RecipeMatch.ItemCombination(1, new ItemStack(Blocks.LAPIS_BLOCK), new ItemStack(Items.ENDER_PEARL)));
		}
		
	@Subscribe
	public void preInit(FMLPreInitializationEvent event)
		{
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		
		registerMachineParts();
		registerMachines();
		registerModifiers();
		
		for (Pair<Item, ToolPart> toolPartPattern : toolPartPatterns)
			{
			registerStencil(toolPartPattern.getLeft(), toolPartPattern.getRight());
			}
			
		NetworkRegistry.INSTANCE.registerGuiHandler(ModRegistryHelper.INSTANCE, new GuiHandler());
		
		ItemDust.initializeDust();
		
		TinkerRegistry.addTrait(MachineTrait.INSTANCE);
		
		proxy.preInit(event);
		}
		
	@Subscribe
	public void init(FMLInitializationEvent event)
		{
		registerToolBuilding();
		
		proxy.init(event);
		}
		
	@Subscribe
	public void postInit(FMLPostInitializationEvent event)
		{
		/* testing testing 1 2 3
		if (Loader.isModLoaded("tconstruct"))
			{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.getByNameOrId("tconstruct:book"),1,0),"ingotIron","ingotIron"));
			}
		*/
		
		ItemDust.initializeDustRecipes(); // If someone *still* didn't add their ores to oredict that's not our problem.
		
		proxy.postInit(event);
		}
		
	public static class ClientProxy extends CommonProxy
		{
		
		@Override
		public void preInit(FMLPreInitializationEvent event)
			{
			super.preInit(event);
			
			toolParts.forEach(ModelRegisterUtil::registerPartModel);
			tools.forEach(ModelRegisterUtil::registerToolModel);
			
			List<IModifier> mods = new ArrayList<>();
			
			mods.add(modOverclock);
			
			for (IModifier modifier : mods)
				ModelRegisterUtil.registerModifierModel(modifier, new ResourceLocation(ModInfo.MODID, "models/item/modifiers/" + modifier.getIdentifier()));
			
			ModelRegisterUtil.registerMaterialItemModel(metalDust);
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