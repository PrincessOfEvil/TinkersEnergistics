package princess.tinkersenergistics.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TileMachine extends TileEntity implements ISidedInventory
	{
	private ItemStack	parentItem;
	
	private int			absoluteSlotLimit	= 9;
	private int			inputSlotLimit		= 9;
	private int			outputSlotLimit		= 9;
	private String		inputSlots			= "InputSlots";
	private String		outputSlots			= "OutputSlots";
	
	/**
	First axis:
	0 - processing input;
	1 - processing output;
	2 - power input;
	Second axis is the internal slot limit.
	There is no third axis. Move along.
	 */
	private ItemStack[]	inventory[]			= new ItemStack[3][absoluteSlotLimit];
	
	@Override
	public int getSizeInventory()
		{
		return inputSlotLimit + outputSlotLimit + 1;
		}
		
	@Override
	public ItemStack getStackInSlot(int index)
		{
//		if (index < inputSlotLimit) return inventory[0][index];
//		if (index < inputSlotLimit + outputSlotLimit) return inventory[1][index-inputSlotLimit];
//		return inventory[2][0];
		// TODO Auto-generated method stub
		return null;
		}
		
	@Override
	public ItemStack decrStackSize(int index, int count)
		{
		// TODO Auto-generated method stub
		return null;
		}
		
	@Override
	public ItemStack removeStackFromSlot(int index)
		{
		// TODO Auto-generated method stub
		return null;
		}
		
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
		{
		// TODO Auto-generated method stub
		
		}
		
	@Override
	public int getInventoryStackLimit()
		{
		return 64;
		}
		
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
		{
		// TODO Auto-generated method stub
		return false;
		}
		
	@Override
	public void openInventory(EntityPlayer player)
		{
		// TODO Auto-generated method stub
		
		}
		
	@Override
	public void closeInventory(EntityPlayer player)
		{
		// TODO Auto-generated method stub
		
		}
		
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
		{
		// TODO Auto-generated method stub
		return false;
		}
		
	@Override
	public int getField(int id)
		{
		// TODO Auto-generated method stub
		return 0;
		}
		
	@Override
	public void setField(int id, int value)
		{
		// TODO Auto-generated method stub
		
		}
		
	@Override
	public int getFieldCount()
		{
		// TODO Auto-generated method stub
		return 0;
		}
		
	@Override
	public void clear()
		{
		// TODO Auto-generated method stub
		
		}
		
	@Override
	public String getName()
		{
		// TODO Auto-generated method stub
		return null;
		}
		
	@Override
	public boolean hasCustomName()
		{
		// TODO Auto-generated method stub
		return false;
		}
		
	@Override
	public int[] getSlotsForFace(EnumFacing side)
		{
		// TODO Auto-generated method stub
		return null;
		}
		
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
		{
		// TODO Auto-generated method stub
		return false;
		}
		
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
		{
		// TODO Auto-generated method stub
		return false;
		}
		
	public ItemStack getParentItem()
		{
		return parentItem;
		}
		
	public void setParentItem(ItemStack parentItem)
		{
		this.parentItem = parentItem;
		NBTTagCompound tag = TagUtil.getTagSafe(parentItem);
		if (tag.getInteger(inputSlots) > 0) inputSlotLimit = tag.getInteger(inputSlots);
		if (tag.getInteger(outputSlots) > 0) outputSlotLimit = tag.getInteger(outputSlots);
		}
	}
