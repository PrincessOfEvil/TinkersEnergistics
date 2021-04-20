package princess.tenergistics.modifiers;

import princess.tenergistics.tools.PoweredTool;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class CapacityModifier extends Modifier
	{
	public CapacityModifier(int color)
		{
		super(color);
		}
		
	@Override
	public int getPriority()
		{
		return -666;
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		super.addVolatileData(toolDefinition, baseStats, persistentData, level, volatileData);
		if (volatileData.getInt(PoweredTool.ITEM_LOCATION) > 0) volatileData
				.putInt(PoweredTool.ITEM_LOCATION, volatileData
						.getInt(PoweredTool.ITEM_LOCATION) + level * FireboxModifier.CAPACITY);
		
		if (volatileData.getInt(PoweredTool.FLUID_LOCATION) > 0) volatileData
				.putInt(PoweredTool.FLUID_LOCATION, volatileData
						.getInt(PoweredTool.FLUID_LOCATION) + level * ExchangerModifier.CAPACITY);
		
		if (volatileData.getInt(PoweredTool.ENERGY_LOCATION) > 0) volatileData
				.putInt(PoweredTool.ENERGY_LOCATION, volatileData
						.getInt(PoweredTool.ENERGY_LOCATION) + level * EnergyCoilModifier.CAPACITY);
		}
	}
