package princess.tenergistics.tools;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.RectangleAOEHarvestLogic;

public class BucketwheelTool extends PoweredTool
	{
	public static final RectangleAOEHarvestLogic BUCKETWEEL_LOGIC = new RectangleAOEHarvestLogic(0, 1, 0);
	
	public BucketwheelTool(Properties properties, ToolDefinition toolDefinition)
		{
		super(properties, toolDefinition);
		}
		
	@Override
	public ToolHarvestLogic getToolHarvestLogic()
		{
		return BUCKETWEEL_LOGIC;
		}
		
	@Override
	public ActionResultType onItemUse(ItemUseContext context)
		{
		return getToolHarvestLogic().transformBlocks(context, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN, true);
		}
		
	}
