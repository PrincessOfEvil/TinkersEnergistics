package princess.tinkersenergistics.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import princess.tinkersenergistics.block.tile.TileMachine;

public class GuiHandler implements IGuiHandler
	{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
		{
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileMachine) { return new ContainerMachine(player.inventory, (TileMachine) tile); }
		return null;
		}
		
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
		{
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileMachine)
			{
			TileMachine containerTileEntity = (TileMachine) tile;
			return new GuiMachine(containerTileEntity, new ContainerMachine(player.inventory, containerTileEntity));
			}
		return null;
		}
	}
