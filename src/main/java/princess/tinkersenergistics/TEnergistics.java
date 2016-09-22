package princess.tinkersenergistics;

import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import princess.tinkersenergistics.common.ConfigHandler;
import princess.tinkersenergistics.library.MachineCore;
import princess.tinkersenergistics.library.MachineModPart;
import princess.tinkersenergistics.library.MachinePart;
import princess.tinkersenergistics.machines.MachineFurnace;
import princess.tinkersenergistics.proxy.CommonProxy;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolCore;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPEND)
public class TEnergistics
	{
	@Mod.Instance(ModInfo.ID)
	public static TEnergistics		instance;
	
	@SidedProxy(clientSide = ModInfo.CLIENTPROXY, serverSide = ModInfo.COMMONPROXY)
	public static CommonProxy		proxy;
	
	// TODO: Do we really need it? public static CreativeTabs		tab			= new CreativeTabEnergistics(ModInfo.ID + ".creativeTab");
	public static Logger			logger		= LogManager.getLogger(ModInfo.NAME);
	
	public static final Category	TIE_MACHINE = new Category("tie_machine");
	
	
	static List<ToolCore> tools = Lists.newLinkedList();
	
	
	public static MachineCore		furnace;
	public static MachineCore		crusher;

	public static MachineCore		converter;
	
	public static MachinePart		machineCasing;
	public static MachinePart		machineGearbox;
	public static MachinePart		machineHeater;
	public static MachinePart		machineMill;
	
	public static MachineModPart	machineFirebox;
	public static MachineModPart	machineExchanger;
	public static MachineModPart	machineCoil;
	
	
	public static Item				carbonBall;

	
	private void registerMachines()
		{
		furnace = registerTool(new MachineFurnace(), "machine_furnace");
		}
	
	private void registerMachineParts()
		{
		machineCasing = registerItem(new MachinePart(Material.VALUE_Ingot * 4), "machine_casing");
		machineGearbox = registerItem(new MachinePart(Material.VALUE_Ingot * 1), "machine_gearbox");
		
		machineHeater = registerItem(new MachinePart(Material.VALUE_Ingot * 2), "machine_heater");
		machineMill = registerItem(new MachinePart(Material.VALUE_Ingot * 2), "machine_mill");
		
		machineFirebox = registerItem(new MachineModPart(Material.VALUE_Ingot * 1), "machine_firebox");
		machineExchanger = registerItem(new MachineModPart(Material.VALUE_Ingot * 1), "machine_exchanger");
		machineCoil = registerItem(new MachineModPart(Material.VALUE_Ingot * 1), "machine_coil");
		}
		
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
		{
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		
		registerMachineParts();
		registerMachines();

		// NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		
		proxy.preInit();
		}
		

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
		{}
		
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
		{}
	
	
	/* Stolen Methods */
	
	private static <T extends ToolCore> T registerTool(T item, String unlocName)
		{
		tools.add(item);
		return registerItem(item, unlocName);
		}
	
	private static <T extends Item> T registerItem(T item, String name)
		{
		if (!name.equals(name.toLowerCase(Locale.US))) { throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! Item: %s", name)); }
		
		item.setUnlocalizedName(prefix(name));
		item.setRegistryName(new ResourceLocation(ModInfo.ID, name));
		GameRegistry.register(item);
		return item;
		}
	
	  public static String prefix(String name) {
	    return String.format("%s.%s", ModInfo.ID, name.toLowerCase(Locale.US));
	  }
	}