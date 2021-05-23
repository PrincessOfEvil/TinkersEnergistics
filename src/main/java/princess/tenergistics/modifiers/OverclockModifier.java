package princess.tenergistics.modifiers;

import java.util.List;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.library.PoweredToolModifier;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class OverclockModifier extends IncrementalModifier
	{
	private static final String TOOLTIP_KEY = "modifier.tenergistics.overclock.extra_tooltip";
	
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
			tooltip.add(new TranslationTextComponent(PoweredToolModifier.TOOLTIP_KEY, tool.getVolatileData()
					.getFloat(PoweredToolModifier.EFFICIENCY) * 100)
							.modifyStyle(style -> style
									.setColor(Color.fromInt(TEnergistics.poweredToolModifier.get().getColor()))));
			}
			
		float scaledLevel = getScaledLevel(tool.getPersistentData(), level);
		tooltip.add(new TranslationTextComponent(TOOLTIP_KEY, Math.pow(1.2, scaledLevel))
				.modifyStyle(style -> style.setColor(Color.fromInt(getColor()))));
		}
		
	@Override
	public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder)
		{
		float scaledLevel = getScaledLevel(persistentData, level);
		
		builder.multiplyMiningSpeed((float) Math.pow(1.2, scaledLevel));
		builder.multiplyAttackDamage((float) Math.pow(1.2, scaledLevel));
		}
		
	@Override
	public int onDamageTool(IModifierToolStack tool, int level, int amount)
		{
		if (tool.getModifierLevel(TEnergistics.poweredToolModifier.get()) == 0)
			{ return (int) (amount / tool.getVolatileData().getFloat(PoweredToolModifier.EFFICIENCY)); }
		return amount;
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		float scaledLevel = getScaledLevel(persistentData, level);
		float eff = volatileData.getFloat(PoweredToolModifier.EFFICIENCY);
		
		if (eff == 0)
			{
			eff = 1f;
			}
			
		volatileData.putFloat(PoweredToolModifier.EFFICIENCY, (float) (eff / Math.pow(2, scaledLevel)));
		}
	}
