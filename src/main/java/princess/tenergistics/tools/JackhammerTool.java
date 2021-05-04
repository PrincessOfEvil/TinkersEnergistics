package princess.tenergistics.tools;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import princess.tenergistics.modifiers.WidePrincessModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.harvest.HarvestTool.MaterialHarvestLogic;

public class JackhammerTool extends PoweredTool
	{
	protected static final Set<Material>		EXTRA_MATERIALS	= Sets
			.newHashSet(Material.ROCK, Material.IRON, Material.ANVIL);
	public static final MaterialHarvestLogic	HARVEST_LOGIC	= new MaterialHarvestLogic(EXTRA_MATERIALS, 0, 0, 0)
																	{
																	@Override
																	public Iterable<BlockPos> getAOEBlocks(IModifierToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, AOEMatchType matchType)
																		{
																		if (!canAOE(tool, stack, state, matchType))
																			{ return Collections.emptyList(); }
																		// expanded gives an extra width every odd level, and an extra height every even level
																		int expanded = tool
																				.getModifierLevel(TinkerModifiers.expanded
																						.get()) + tool.getVolatileData()
																								.getInt(WidePrincessModifier.WIDE);
																		return calculate(this, tool, stack, world, player, origin, sideHit, extraWidth + ((expanded + 1) / 2), extraHeight + (expanded / 2), extraDepth, matchType);
																		}
																	};
	
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
