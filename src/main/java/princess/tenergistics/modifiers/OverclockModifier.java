package princess.tenergistics.modifiers;

import net.minecraft.util.ResourceLocation;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class OverclockModifier extends IncrementalModifier
	{
	public static final ResourceLocation OVERCLOCK = new ResourceLocation(TEnergistics.modID, "overclock");
	
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
	  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
	    float scaledLevel = getScaledLevel(persistentData, level);
	    
	    builder.multiplyMiningSpeed((float) Math.pow(1.2, scaledLevel));
	    builder.multiplyAttackDamage((float) Math.pow(1.2, scaledLevel));
	  }
	
	@Override
	public int onDamageTool(IModifierToolStack tool, int level, int amount)
		{
	    float scaledLevel = getScaledLevel(tool.getPersistentData(), level);
		return (int) (amount * Math.pow(2, scaledLevel));
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		super.addVolatileData(toolDefinition, baseStats, persistentData, level, volatileData);

	    float scaledLevel = getScaledLevel(persistentData, level);
		volatileData.putFloat(OVERCLOCK, (float) ((volatileData.getFloat(OVERCLOCK) + 1) * Math.pow(2, scaledLevel) - 1));
		}
	}
