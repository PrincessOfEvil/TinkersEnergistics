package princess.tenergistics.tools;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;

public class BucketwheelTool extends PoweredTool
	{
	public static final AOEToolHarvestLogic BUCKETWEEL_LOGIC = new AOEToolHarvestLogic(1, 3, 1);
	
	public BucketwheelTool(Properties properties, ToolDefinition toolDefinition)
		{
		super(properties, toolDefinition);
		}
		
	@Override
	public AOEToolHarvestLogic getToolHarvestLogic()
		{
		return BUCKETWEEL_LOGIC;
		}
		
	@Override
	public ActionResultType onItemUse(ItemUseContext context)
		{
		return getToolHarvestLogic().transformBlocks(context, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN, true);
		}
		
	}
