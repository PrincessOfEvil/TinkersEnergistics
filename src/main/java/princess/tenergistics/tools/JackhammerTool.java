package princess.tenergistics.tools;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.block.material.Material;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.tools.harvest.HarvestTool.MaterialHarvestLogic;

public class JackhammerTool extends PoweredTool
	{
	protected static final Set<Material>		EXTRA_MATERIALS	= Sets
			.newHashSet(Material.ROCK, Material.IRON, Material.ANVIL);
	public static final MaterialHarvestLogic	HARVEST_LOGIC	= new MaterialHarvestLogic(EXTRA_MATERIALS, 0, 0, 0);
	
	public JackhammerTool(Properties properties, ToolDefinition toolDefinition)
		{
		super(properties, toolDefinition);
		}
		
	@Override
	public ToolHarvestLogic getToolHarvestLogic()
		{
		return HARVEST_LOGIC;
		}
	}
