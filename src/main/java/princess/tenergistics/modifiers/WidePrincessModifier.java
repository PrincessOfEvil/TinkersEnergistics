package princess.tenergistics.modifiers;

import net.minecraft.util.ResourceLocation;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.tools.ToolDefinitions;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class WidePrincessModifier extends SingleUseModifier
	{
	public static final ResourceLocation WIDE = new ResourceLocation(TEnergistics.modID, "wide");
	
	public WidePrincessModifier()
		{
		super(0x80b0d0);
		}
		
	@Override
	public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder)
		{		
		builder.multiplyMiningSpeed(ToolDefinitions.MEDIUM_SPEED_MULTIPLIER);
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		super.addVolatileData(toolDefinition, baseStats, persistentData, level, volatileData);
		
		volatileData.putInt(WIDE, volatileData.getInt(WIDE) + level);
		}
	}
