package princess.tenergistics.tools;

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
	}
