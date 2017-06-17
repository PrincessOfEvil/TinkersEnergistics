package princess.tinkersenergistics;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler
	{
	public static int	dustsPerOre				= -1;
	public static int	fluidFuelMultiplier		= 16;
	public static int	energyPerCraft			= 800;
	public static int	energyPerNanoCraft		= 4;
	public static int	fireTicksInTwoSticks	= 200;
	
	public static int	tankCapacityPerAttack	= 2000;
	public static int	energyCapacityPerAttack	= 4000;
	
	public static void init(File cfg)
		{
		
		Configuration config = new Configuration(cfg);
		
		config.load();
		String descDPO = "Dusts per one ore block. If -1, uses TiC smeltery setting.";
		dustsPerOre = config.getInt("dustsPerOre", "Balance", -1, -1, 8, descDPO);
		
		String descFFM = "Modifies amount of fluid fuel converted into fireticks. Fluid heat exchangers use Smeltery fuels to work.";
		fluidFuelMultiplier = config.getInt("fluidFuelMultiplier", "Balance", 16, 1, 100500, descFFM);
		
		String descEPC = "Amount of FU converted into a firetick.";
		energyPerNanoCraft = config.getInt("energyPerNanoCraft", "Balance", 4, 1, 100500, descEPC);
		energyPerCraft = energyPerNanoCraft * fireTicksInTwoSticks;
		
		String descTCD = "Tank Capacity per attack point of a burner";
		tankCapacityPerAttack = config.getInt("tankCapacityPerAttack", "Balance", 2000, 1, 100500, descTCD);
		
		String descECD = "Energy Capacity per attack point of a burner";
		energyCapacityPerAttack = config.getInt("energyCapacityPerAttack", "Balance", 4000, 1, 100500, descECD);
		
		config.save();
		}
	}
