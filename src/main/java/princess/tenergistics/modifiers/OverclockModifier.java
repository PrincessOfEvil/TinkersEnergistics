package princess.tenergistics.modifiers;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.library.PoweredToolModifier;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class OverclockModifier extends IncrementalModifier
	{
	public static final double OC_POWER = 1.1;
	
	public OverclockModifier(int color)
		{
		super(color);
		}
		
	@Override
	public int getPriority()
		{
		return 13666;
		}
		
	@Override
	public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed)
		{
		if (tool.getModifierLevel(TEnergistics.poweredToolModifier.get()) == 0)
			{
			tooltip.add(TEnergistics.EFFICIENCY.formatValue(tool.getStats().getFloat(TEnergistics.EFFICIENCY)));
			tooltip.add(TEnergistics.MACHINE_SPEED.formatValue(tool.getStats().getFloat(TEnergistics.MACHINE_SPEED)));
			}
		}
		
	@Override
	public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder)
		{
		float scaledLevel = getScaledLevel(persistentData, level);
		
		ToolStats.MINING_SPEED.multiply(builder, Math.pow(OC_POWER, scaledLevel));
		ToolStats.ATTACK_DAMAGE.multiply(builder, Math.pow(OC_POWER, scaledLevel));
		
		ensureSpeedIsInitialized(baseStats, builder);
		TEnergistics.MACHINE_SPEED.multiply(builder, Math.pow(OC_POWER, scaledLevel));
		TEnergistics.EFFICIENCY.multiply(builder, 1 / Math.pow(2, scaledLevel));
		}
		
	@Override
	public int onDamageTool(IModifierToolStack tool, int level, int amount)
		{
		if (tool.getModifierLevel(TEnergistics.poweredToolModifier.get()) == 0)
			{ return PoweredToolModifier.modifyDamageFor(tool, amount); }
		return amount;
		}
		
	private void ensureSpeedIsInitialized(StatsNBT baseStats, ModifierStatsBuilder builder)
		{
		if (baseStats.getFloat(TEnergistics.MACHINE_SPEED) == TEnergistics.MACHINE_SPEED.getDefaultValue())
			{
			TEnergistics.MACHINE_SPEED.add(builder, 1);
			}
		}
	}
