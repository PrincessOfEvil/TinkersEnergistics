package princess.tenergistics.library;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class PoweredToolModifier extends Modifier
	{
	public static final ResourceLocation	EFFICIENCY	= new ResourceLocation(TEnergistics.modID, "efficiency");
	public static final String				TOOLTIP_KEY	= "modifier.tenergistics.powered.extra_tooltip";
	
	public PoweredToolModifier()
		{
		super(65535);
		}
		
	@Override
	public int getPriority()
		{
		// Classic builtin modifier value
		return Integer.MAX_VALUE - 50;
		}
		
	@Override
	public boolean shouldDisplay(boolean advanced)
		{
		return false;
		}
		
	@Override
	public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed)
		{
		tooltip.add(new TranslationTextComponent(TOOLTIP_KEY, tool.getVolatileData().getFloat(EFFICIENCY) * 100)
				.modifyStyle(style -> style.setColor(Color.fromInt(getColor()))));
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		volatileData.putFloat(PoweredToolModifier.EFFICIENCY, 1f);
		}
		
	@Override
	public int onDamageTool(IModifierToolStack tool, int level, int amount)
		{
		return (int) (amount / tool.getVolatileData().getFloat(EFFICIENCY));
		}
		
	/**ToolStatsBuilder#fetchStatsOrDefault*/
	@Nullable
	public static <T extends IMaterialStats> T fetchStatsOrDefault(MaterialStatsId statsId, IMaterial material, IToolPart requiredComponent)
		{
		if (statsId.equals(requiredComponent.getStatType()))
			{
			return MaterialRegistry.getInstance()
					.<T> getMaterialStats(material.getIdentifier(), statsId)
					.orElseGet(() -> MaterialRegistry.getInstance().getDefaultStats(statsId));
			}
		else
			{
			return null;
			}
		}
	}
