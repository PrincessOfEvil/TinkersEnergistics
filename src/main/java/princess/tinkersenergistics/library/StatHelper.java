package princess.tinkersenergistics.library;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.text.TextFormatting;
import princess.tinkersenergistics.ConfigHandler;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.MaterialTypes;

public class StatHelper
	{
	private static final float	FURNACE_COOKTIME		= 10F;
	private static final float	CRUSHER_COOKTIME		= 800F;
	
	private static final float	FURNACE_HANDLEMULT		= 146F;
	private static final float	CRUSHER_HANDLEMULT		= 1.75F;
	
	private static final double	HANDLE_FUELPOWER		= 1.2D;
	
	private static final float	EXTRAMULT				= 100F;
	
	public static final double	SOLIDBOOST				= 1.005;
	
	public final static String	LOC_Furnace				= ".furnace.name";
	public final static String	LOC_Crusher				= ".crusher.name";
	public final static String	LOC_Gearbox				= ".gearbox.name";
	
	public final static String	LOC_Firebox				= ".firebox.name";
	public final static String	LOC_Exchanger			= ".exchanger.name";
	public final static String	LOC_Coil				= ".coil.name";
	
	public final static String	LOC_Attack				= "stat.attack";
	
	public final static String	LOC_ÑookTime			= "stat.cooktime";
	public final static String	LOC_SpeedMultiplier		= "stat.speedmult";
	public final static String	LOC_FuelMultiplier		= "stat.fuelmulti";
	
	public final static String	LOC_ÑookTimeDesc		= "stat.cooktime.desc";
	public final static String	LOC_SpeedMultiplierDesc	= "stat.speedmult.desc";
	public final static String	LOC_FuelMultiplierDesc	= "stat.fuelmulti.desc";
	
	public final static String	LOC_StatName			= "stat.%s.machine.name";
	
	public final static String	COLOR_ÑookTime			= CustomFontColor.encodeColor(24, 128, 192);
	public final static String	COLOR_SpeedMultiplier	= CustomFontColor.encodeColor(96, 212, 0);
	public final static String	COLOR_FuelMultiplier	= CustomFontColor.encodeColor(255, 128, 0);
	public final static String	COLOR_Attack			= CustomFontColor.encodeColor(255, 72, 72);
	
	public static String formatCookTime(int cooktime, int type)
		{
		return formatNumber(LOC_ÑookTime + postfix(type), COLOR_ÑookTime, cooktime);
		}
		
	public static String formatSpeedMultiplier(float speedmult, int type)
		{
		return formatNumber(LOC_SpeedMultiplier + postfix(type), COLOR_SpeedMultiplier, speedmult);
		}
		
	public static String formatFuelMultiplier(float fuelmult, int type)
		{
		return formatNumber(LOC_FuelMultiplier + postfix(type), COLOR_FuelMultiplier, fuelmult);
		}
		
	public static String formatAttack(int attack, int type)
		{
		return formatNumber(LOC_Attack + postfixMod(type), COLOR_Attack, attack);
		}
		
	public static String postfix(int type)
		{
		switch (type)
			{
			case 0:
				return LOC_Furnace;
			case 1:
				return LOC_Crusher;
			default:
				return LOC_Gearbox;
			}
		}
		
	public static String postfixMod(int type)
		{
		switch (type)
			{
			case 0:
				return LOC_Firebox;
			case 1:
				return LOC_Exchanger;
			case 2:
				return LOC_Coil;
			}
		return "UNKNOWN";
		}
		
	public static List<String> getLocalizedDesc()
		{
		List<String> info = Lists.newArrayList();
		
		info.add(Util.translate(LOC_ÑookTimeDesc));
		info.add(Util.translate(LOC_SpeedMultiplierDesc));
		info.add(Util.translate(LOC_FuelMultiplierDesc));
		
		return info;
		}
		
	public static String getLocalizedName(IMaterialStats stat)
		{
		return Util.translate(String.format(LOC_StatName, stat.getIdentifier()));
		}
		
	public static String getLocalizedName(String stat)
		{
		return Util.translate(String.format(LOC_StatName, stat));
		}
		
	public static List<String> getLocalizedInfo(IMaterialStats stat, int type)
		{
		List<String> info = Lists.newArrayList();
		
		int cookTime;
		float speedMultiplier;
		float fuelMultiplier;
		
		switch (stat.getIdentifier())
			{
			case MaterialTypes.HEAD:
				cookTime = 1000;
				cookTime = cookTime(cookTime, 0, (HeadMaterialStats) stat);
				info.add(formatCookTime(cookTime, 0));
				
				cookTime = 1000;
				cookTime = cookTime(cookTime, 1, (HeadMaterialStats) stat);
				info.add(formatCookTime(cookTime, 1));
				
				break;
			
			case MaterialTypes.HANDLE:
				speedMultiplier = 1F;
				fuelMultiplier = 1F;
				
				speedMultiplier = speedMultiplier(speedMultiplier, type, (HandleMaterialStats) stat);
				fuelMultiplier = fuelMultiplier(fuelMultiplier, type, (HandleMaterialStats) stat);
				
				info.add(formatSpeedMultiplier(speedMultiplier, type));
				info.add(formatFuelMultiplier(fuelMultiplier, type));
				break;
			
			case MaterialTypes.EXTRA:
				speedMultiplier = 1F;
				fuelMultiplier = 5F;
				
				speedMultiplier = speedMultiplier(speedMultiplier, type, (ExtraMaterialStats) stat);
				fuelMultiplier = fuelMultiplier(fuelMultiplier, type, (ExtraMaterialStats) stat);
				
				info.add(formatSpeedMultiplier(speedMultiplier, -1));
				info.add(formatFuelMultiplier(fuelMultiplier, -1));
				break;
			}
		return info;
		}
		
	public static List<String> getLocalizedInfoMod(HeadMaterialStats stat, int type)
		{
		List<String> info = Lists.newArrayList();
		
		switch (type)
			{
			case 0:
				float speedMultiplier = 2F;
				float fuelMultiplier = 10F;
				
				speedMultiplier = speedMultiplier(speedMultiplier, (HeadMaterialStats) stat, false);
				fuelMultiplier = fuelMultiplier(fuelMultiplier, (HeadMaterialStats) stat, false);
				
				info.add(formatSpeedMultiplier(speedMultiplier, type));
				info.add(formatFuelMultiplier(fuelMultiplier, type));
				break;
			case 1:
				info.add(formatAttack(Math.round(stat.attack * ConfigHandler.tankCapacityPerAttack), type));
				break;
			
			case 2:
				info.add(formatAttack(Math.round(stat.attack * ConfigHandler.energyCapacityPerAttack), type));
				break;
			}
			
		return info;
		}
		
	public static int cookTime(int cookTime, int type, HeadMaterialStats head)
		{
		switch (type)
			{
			case 0:
				cookTime = Math.round(((float) cookTime) / (head.miningspeed / FURNACE_COOKTIME));
				break;
			case 1:
				cookTime = Math.round(((float) cookTime) / (((float) head.durability) / CRUSHER_COOKTIME));
				break;
			}
		return cookTime;
		}
		
	public static float speedMultiplier(float speedMultiplier, HeadMaterialStats head, boolean reverse)
		{
		if (reverse)
			return (float) Math.pow(speedMultiplier, 1D / Math.pow(SOLIDBOOST, head.attack));
		else
			return (float) Math.pow(speedMultiplier, Math.pow(SOLIDBOOST, head.attack));
		}
		
	public static float fuelMultiplier(float fuelMultiplier, HeadMaterialStats head, boolean reverse)
		{
		if (reverse)
			return (float) Math.pow(fuelMultiplier, 1D / Math.pow(SOLIDBOOST, head.attack));
		else
			return (float) Math.pow(fuelMultiplier, Math.pow(SOLIDBOOST, head.attack));
		}
		
	public static float speedMultiplier(float speedMultiplier, int type, HandleMaterialStats handle)
		{
		switch (type)
			{
			case 0:
				return speedMultiplier * ((float) Math.abs(handle.durability) / FURNACE_HANDLEMULT);
			case 1:
				return speedMultiplier * (handle.modifier * handle.modifier * CRUSHER_HANDLEMULT);
			}
		return speedMultiplier;
		}
		
	public static float fuelMultiplier(float fuelMultiplier, int type, HandleMaterialStats handle)
		{
		switch (type)
			{
			case 0:
				fuelMultiplier = fuelMultiplier * (float) (Math.pow(((float) Math.abs(handle.durability) / FURNACE_HANDLEMULT), HANDLE_FUELPOWER));
				break;
			case 1:
				fuelMultiplier = fuelMultiplier * (float) (Math.pow((handle.modifier * handle.modifier * CRUSHER_HANDLEMULT), HANDLE_FUELPOWER));
				break;
			}
		return fuelMultiplier;
		}
		
	public static float speedMultiplier(float speedMultiplier, int type, ExtraMaterialStats extra)
		{
		return speedMultiplier * ((float) Math.abs(extra.extraDurability) / EXTRAMULT);
		}
		
	public static float fuelMultiplier(float fuelMultiplier, int type, ExtraMaterialStats extra)
		{
		return fuelMultiplier * ((float) Math.abs(extra.extraDurability) / EXTRAMULT);
		}
		
	public static String formatNumber(String loc, String color, int number)
		{
		return String.format("%s: %s%s", Util.translate(loc), color, Util.df.format(number)) + TextFormatting.RESET;
		}
		
	public static String formatNumber(String loc, String color, float number)
		{
		return String.format("%s: %s%s", Util.translate(loc), color, Util.df.format(number)) + TextFormatting.RESET;
		}
	}
