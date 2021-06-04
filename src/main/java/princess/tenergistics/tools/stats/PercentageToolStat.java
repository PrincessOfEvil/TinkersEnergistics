package princess.tenergistics.tools.stats;

import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;

public class PercentageToolStat extends FloatToolStat
	{
	public PercentageToolStat(ToolStatId name, int color, float defaultValue, float minValue, float maxValue)
		{
		super(name, color, defaultValue, minValue, maxValue);
		}
		
	@Override
	public ITextComponent formatValue(float number)
		{
		return IToolStat.formatNumberPercent(Util.makeTranslationKey("tool_stat", getName()), getColor(), number);
		}
	}
