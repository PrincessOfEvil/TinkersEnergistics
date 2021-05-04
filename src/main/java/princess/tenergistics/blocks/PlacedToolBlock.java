package princess.tenergistics.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import princess.tenergistics.blocks.tileentity.PlacedToolTileEntity;
import slimeknights.mantle.util.TileEntityHelper;

public class PlacedToolBlock extends Block
	{
	public PlacedToolBlock(Properties properties)
		{
		super(properties);
		}
		
	public Class<? extends PlacedToolTileEntity> getEntityType()
		{
		return PlacedToolTileEntity.class;
		}
		
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
		{
		return new PlacedToolTileEntity();
		}
		
	@Override
	public boolean hasTileEntity(BlockState state)
		{
		return true;
		}
		
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
		{
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		
		TileEntityHelper.getTile(getEntityType(), world, pos).ifPresent(te -> te.setTool(stack.copy()));
		}
		
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
		{
		return TileEntityHelper.getTile(getEntityType(), world, pos)
				.map(te -> te.getTool().copy())
				.orElse(ItemStack.EMPTY);
		}
		
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
		{
		if (state.getBlock() != newState.getBlock())
			{
			TileEntityHelper.getTile(getEntityType(), world, pos)
					.ifPresent(te -> spawnAsEntity(world, pos, te.getTool()));
			world.updateComparatorOutputLevel(pos, this);
			}
		super.onReplaced(state, world, pos, newState, isMoving);
		}
	}