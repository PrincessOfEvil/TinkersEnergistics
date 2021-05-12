package princess.tenergistics.modifiers;

import java.util.List;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import princess.tenergistics.library.PowerSourceModifier;
import princess.tenergistics.tools.PoweredTool;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class EnergyCoilModifier extends PowerSourceModifier
	{
	private static final int	ENERGY_PER_OPERATION	= 200;
	public static final int		CAPACITY				= ENERGY_PER_OPERATION * 250;
	
	private static final int	COLOR					= 0xf02f2f;
	
	private static final String	TOOLTIP_KEY				= "modifier.tenergistics.energy_coil.extra_tooltip";
	
	public EnergyCoilModifier()
		{
		super(COLOR);
		}
		
	@Override
	public boolean isPowered(IModifierToolStack tool, int level, boolean dirty)
		{
		return PoweredTool.getEnergy(tool) > ENERGY_PER_OPERATION;
		}
		
	@Override
	public void drainPower(IModifierToolStack tool, int level)
		{
		PoweredTool.setEnergy(tool, PoweredTool.getEnergy(tool) - ENERGY_PER_OPERATION);
		}
		
	@Override
	public double getDamagePercentage(IModifierToolStack tool, int level)
		{
		if (PoweredTool.getEnergy(tool) > ENERGY_PER_OPERATION)
			{ return 1d - PoweredTool.getEnergy(tool) / (double) PoweredTool.getMaxEnergy(tool); }
		return Double.NaN;
		}
		
	@Override
	public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed)
		{
		tooltip.add(new TranslationTextComponent(TOOLTIP_KEY, PoweredTool.getEnergy(tool), PoweredTool
				.getMaxEnergy(tool)).modifyStyle(style -> style.setColor(Color.fromInt(getColor()))));
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		super.addVolatileData(toolDefinition, baseStats, persistentData, level, volatileData);
		volatileData.putInt(PoweredTool.ENERGY_LOCATION, CAPACITY);
		}
	}
