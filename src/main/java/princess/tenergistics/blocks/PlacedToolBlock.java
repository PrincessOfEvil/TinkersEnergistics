package princess.tenergistics.blocks;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import princess.tenergistics.blocks.tileentity.PlacedToolTileEntity;
import slimeknights.mantle.util.TileEntityHelper;

public class PlacedToolBlock extends Block
	{
	public static final DirectionProperty			FACING		= BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<AttachFace>	ATTACH_TO	= BlockStateProperties.FACE;
	
	public PlacedToolBlock(Properties properties)
		{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(FACING, Direction.NORTH)
				.with(ATTACH_TO, AttachFace.FLOOR));
		}
		
	/* States */
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
		{
		builder.add(FACING, ATTACH_TO);
		}
		
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
		{
		Direction direction = context.getFace().getOpposite();
		BlockState state = this.getDefaultState()
				.with(ATTACH_TO, direction == Direction.UP ? AttachFace.CEILING
						: direction == Direction.DOWN ? AttachFace.FLOOR : AttachFace.WALL)
				.with(FACING, context.getPlacementHorizontalFacing());
		
		if (!state.isValidPosition(context.getWorld(), context.getPos()))
			{ return null; }
			
		return state;
		}
		
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
		{
		Direction direction = getDirectionFromState(state);
		BlockPos target = pos.offset(direction);
		return world.getBlockState(target).isSolidSide(world, target, direction.getOpposite());
		}
		
	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
		{
		Direction direction = getDirectionFromState(state);
		if (facing == direction && !state.isValidPosition(world, pos))
			{ return Blocks.AIR.getDefaultState(); }
		return state;
		}
		
	private static final ImmutableMap<Direction.Axis, VoxelShape> BOUNDS;
	static
		{
		ImmutableMap.Builder<Direction.Axis, VoxelShape> builder = ImmutableMap.builder();
		
		builder.put(Direction.Axis.X, VoxelShapes.create(0, 0, 15d / 32, 1, 1, 17d / 32));
		builder.put(Direction.Axis.Z, VoxelShapes.create(15d / 32, 0, 0, 17d / 32, 1, 1));
		
		BOUNDS = builder.build();
		}
		
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
		{
		return BOUNDS.get(state.get(FACING).getAxis());
		}
		
	public static Direction getDirectionFromState(BlockState state)
		{
		switch (state.get(ATTACH_TO))
			{
			case FLOOR:
				return Direction.DOWN;
			case CEILING:
				return Direction.UP;
			default:
				return state.get(FACING);
			}
		}
		
	/* TE based stuff */
	
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