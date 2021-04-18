package princess.tenergistics.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import princess.tenergistics.blocks.tileentity.ChargerTileEntity;
import slimeknights.mantle.block.InventoryBlock;

public class ChargerBlock extends InventoryBlock
	{

	public ChargerBlock(Properties builder)
		{
		super(builder);
		}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
		{
	    return new ChargerTileEntity();
		}
	
	}
