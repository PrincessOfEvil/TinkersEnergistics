package princess.tinkersenergistics.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import princess.tinkersenergistics.ModInfo;
import princess.tinkersenergistics.block.tile.TileMachine;

public class BlockMachine extends BlockContainer
	{
	public BlockMachine()
		{
		super(Material.IRON);
		setRegistryName(new ResourceLocation(ModInfo.MODID, "machine_block"));
		setHardness(4);
		setHarvestLevel("pickaxe", 1);
		}
		
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
		{
		return new TileMachine();
		}
		
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
		{
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		
		if (stack.getTagCompound() != null)
			{
			TileMachine tile = (TileMachine) worldIn.getTileEntity(pos);
			
			tile.setParentItem(stack);
			}
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
	}