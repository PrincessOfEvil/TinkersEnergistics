package princess.tinkersenergistics.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import princess.tinkersenergistics.ModRegistryHelper;
import princess.tinkersenergistics.block.tile.TileMachine;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockMachine extends BlockContainer
	{
	private static int GUI_ID = 1;
	
	public BlockMachine()
		{
		super(Material.IRON);
		setHardness(4);
		setHarvestLevel("pickaxe", 1);
		setCreativeTab(TinkerRegistry.tabWorld);
		}
		
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
		{
		return new TileMachine();
		}
		
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
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
			
			ItemStack[] inv = tile.getInventory().getInventory();
			for (int i = 0; i < inv.length; i++)
				{
				if (inv[i] != null) spawnAsEntity(world, pos, inv[i]);
				}
			}
		}
	}