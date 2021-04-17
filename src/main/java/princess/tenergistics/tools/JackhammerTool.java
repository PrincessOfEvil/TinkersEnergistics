package princess.tenergistics.tools;

import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;

public class JackhammerTool extends PoweredTool
	{
	public static final AOEToolHarvestLogic HARVEST_LOGIC = PickaxeTool.HARVEST_LOGIC;
	
	public JackhammerTool(Properties properties, ToolDefinition toolDefinition)
		{
		super(properties, toolDefinition);
		}
		
	@Override
	public AOEToolHarvestLogic getToolHarvestLogic()
		{
		return HARVEST_LOGIC;
		}
	}
