package princess.tenergistics.modifiers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.blocks.PlacedToolBlock;
import princess.tenergistics.library.PlaceBlockModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class PlaceToolModifier extends PlaceBlockModifier
	{
	public PlaceToolModifier()
		{
		super(0x624671);
		}
		
	@Override
	public ActionResultType afterBlockUse(IModifierToolStack tool, int level, ItemUseContext context)
		{
		PlayerEntity player = context.getPlayer();
		if (player == null || player.isSneaking())
			{ return tryPlace(new BlockItemUseContext(context)); }
		return ActionResultType.PASS;
		}
		
	@Override
	protected Block getBlock(BlockItemUseContext context)
		{
		return TEnergistics.placedToolBlock.get();
		}
		
	@Override
	protected SoundEvent getPlaceSound(BlockItemUseContext context, BlockState state, World world, BlockPos pos)
		{
		return world.getBlockState(pos.offset(PlacedToolBlock.getDirectionFromState(state))).getSoundType(world, pos, context.getPlayer()).getBreakSound();
		}
		
	@Override
	protected void onBlockPlacement(IModifierToolStack tool, ItemStack stack, PlayerEntity entity)
		{
		stack.setCount(0);
		}
	}
