package princess.tenergistics.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import princess.tenergistics.blocks.tileentity.ScrewPumpTileEntity;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;

public class ScrewPumpBlock extends FaucetBlock
	{
	public ScrewPumpBlock(Properties properties)
		{
		super(properties);
		}
		
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
		{
		return new ScrewPumpTileEntity();
		}
	}
