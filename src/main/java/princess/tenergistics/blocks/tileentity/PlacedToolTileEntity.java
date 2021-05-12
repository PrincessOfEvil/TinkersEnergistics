package princess.tenergistics.blocks.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import princess.tenergistics.TEnergistics;

public class PlacedToolTileEntity extends TileEntity
	{
	private static final String	TAG_TOOL	= "tool";
	
	protected ItemStack			tool		= ItemStack.EMPTY;
	
	public PlacedToolTileEntity()
		{
		super(TEnergistics.placedToolTile.get());
		}
		
	public void setTool(ItemStack stack)
		{
		if (tool.isEmpty()) tool = stack;
		}
		
	public ItemStack getTool()
		{
		return tool;
		}
		
	public boolean bitesIn()
		{
		return true;
		}
		
	/*
	@Override
	@Nonnull
	public IModelData getModelData()
		{
		return new SinglePropertyData(null, tool);
		}
	*/
	
	@Override
	public void read(BlockState blockState, CompoundNBT tags)
		{
		super.read(blockState, tags);
		setTool(ItemStack.read(tags.getCompound(TAG_TOOL)));
		}
		
	@Override
	public CompoundNBT write(CompoundNBT tags)
		{
		super.write(tags);
		CompoundNBT nbt = new CompoundNBT();
		getTool().write(nbt);
		tags.put(TAG_TOOL, nbt);
		return tags;
		}
		
	@Override
	public CompoundNBT getUpdateTag()
		{
		CompoundNBT tags = super.getUpdateTag();
		CompoundNBT nbt = new CompoundNBT();
		getTool().write(nbt);
		tags.put(TAG_TOOL, nbt);
		return tags;
		}
		
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket()
		{
		return new SUpdateTileEntityPacket(pos, 0, getTool().write((new CompoundNBT())));
		}
		
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
		{
		setTool(ItemStack.read((pkt.getNbtCompound())));
		}
	}
