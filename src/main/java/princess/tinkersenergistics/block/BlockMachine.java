package princess.tinkersenergistics.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import princess.tinkersenergistics.ModRegistryHelper;
import princess.tinkersenergistics.block.tile.TileMachine;
import princess.tinkersenergistics.client.UnlistedPropertyParent;

public class BlockMachine extends BlockContainer
	{
	private static int							GUI_ID		= 1;
	
	public static final UnlistedPropertyParent	PARENT_ITEM	= new UnlistedPropertyParent("machine");
	
	public BlockMachine()
		{
		super(Material.IRON);
		setHardness(4);
		setHarvestLevel("pickaxe", 1);
		}
		
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
		{
		return new TileMachine();
		}
		
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float clickX, float clickY, float clickZ)
		{
		if (world.isRemote) return true;
		
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileMachine)) return false;
		
		player.openGui(ModRegistryHelper.INSTANCE, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
		
		return true;
		}
		
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
		{
		return ((TileMachine) world.getTileEntity(pos)).getParentItem();
		}
		
	@Override
	public int quantityDropped(Random random)
		{
		return 0;
		}
		
	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
		{
		if (!world.isRemote && !world.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
			{
			TileMachine tile = (TileMachine) world.getTileEntity(pos);
			ItemStack item = tile.getParentItem();
			
			spawnAsEntity(world, pos, item);
			
			NonNullList<ItemStack> inv = tile.getInventory().getInventory();
			for (int i = 0; i < inv.size(); i++)
				{
				if (!inv.get(i).isEmpty()) spawnAsEntity(world, pos, inv.get(i));
				}
			}
		}
		
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
		{
		return false;
		}
		
	@Override
	public boolean isBlockNormalCube(IBlockState blockState)
		{
		return false;
		}
		
	@Override
	public boolean isOpaqueCube(IBlockState blockState)
		{
		return false;
		}
		
//	@SideOnly(Side.CLIENT)
//	public void initModel()
//		{
//		// To make sure that our baked model model is chosen for all states we use this custom state mapper:
//		StateMapperBase ignoreState = new StateMapperBase()
//			{
//			@Override
//			protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState)
//				{
//				return MachineBakedModel.BAKED_MODEL;
//				}
//			};
//		ModelLoader.setCustomStateMapper(this, ignoreState);
//		}
		
	@SuppressWarnings("rawtypes")
	@Override
	protected BlockStateContainer createBlockState()
		{
		IProperty[] listedProperties = new IProperty[0]; // no listed properties
		IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { PARENT_ITEM };
		return new ExtendedBlockState(this, listedProperties, unlistedProperties);
		}
		
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
		{
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		
		if (!(world.getTileEntity(pos) instanceof TileMachine)) return state;
		
		ItemStack parentItem = ((TileMachine) world.getTileEntity(pos)).getParentItem();
		
		return extendedBlockState.withProperty(PARENT_ITEM, parentItem);
		}
	}