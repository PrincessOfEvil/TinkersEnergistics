package princess.tinkersenergistics.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class MachineItemHandler implements IItemHandler, INBTSerializable<NBTTagCompound>
	{
	private static final int	absoluteSlotLimit	= 9;
	private static final int	eternalSlotLimit	= 18;
	private static final int	ultimateSlotLimit	= 19;
	
	private int					inputSlotLimit		= 2;
	private int					outputSlotLimit		= 2;
	
	/**
	0-8 - processing input;
	9-17 - processing output;
	18 - power input.
	 */
	private ItemStack			inventory[]			= new ItemStack[ultimateSlotLimit];
	
	@Override
	public NBTTagCompound serializeNBT()
		{
		NBTTagList nbtTagList = new NBTTagList();
		for (int i = 0; i < inventory.length; i++)
			{
			if (inventory[i] != null)
				{
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				inventory[i].writeToNBT(itemTag);
				nbtTagList.appendTag(itemTag);
				}
			}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Items", nbtTagList);
		return nbt;
		}
		
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
		{
		NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++)
			{
			NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
			int slot = itemTags.getInteger("Slot");
			
			if (slot >= 0 && slot < inventory.length)
				{
				inventory[slot] = ItemStack.loadItemStackFromNBT(itemTags);
				}
			}
		}
		
	/* Processing i/o is exactly the same, but from opposite slots - internal use only.*/
	/** Autosorts the inventory as well if simulate = false. */
	public ItemStack extractItemProcessing(int amount, boolean simulate)
		{
		if (!simulate)
			{
			ItemStack[] storage = inventory.clone();
			for (int i = 0; i < absoluteSlotLimit; i++)
				{
				inventory[i] = null;
				insertItem(storage[i], false);
				}
			}
		
		int j = 0;
		for (int i = 0; i < absoluteSlotLimit; i++)
			{
			if (extractItemProcessing(i, amount, true) != null) j = i; 
			}
		
		return extractItemProcessing(j, amount, simulate);
		}
	
	public ItemStack extractItemProcessing(int slot, int amount, boolean simulate)
		{
		if (amount == 0) return null;
		
		if (slot < 0 || slot >= absoluteSlotLimit) return null;
		
		ItemStack existing = this.inventory[slot];
		
		if (existing == null) return null;
		
		int toExtract = Math.min(amount, existing.getMaxStackSize());
		
		if (existing.stackSize <= toExtract)
			{
			if (!simulate)
				{
				this.inventory[slot] = null;
				}
			return existing;
			}
		else
			{
			if (!simulate)
				{
				this.inventory[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract);
				}
				
			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
			}
		}
		
	public ItemStack insertItemProcessing(ItemStack inputstack, boolean simulate)
		{
		if (inputstack == null || inputstack.stackSize == 0) return null;
		ItemStack stack = inputstack.copy();
		
		for (int i = absoluteSlotLimit; i < absoluteSlotLimit + outputSlotLimit; i++)
			{
			stack = insertItemProcessing(i, stack, simulate);
			if (stack == null) break;
			}
			
		return stack;
		}
		
	public ItemStack insertItemProcessing(int slot, ItemStack stack, boolean simulate)
		{
		if (stack == null || stack.stackSize == 0) return null;
		
		if (slot < absoluteSlotLimit || slot >= absoluteSlotLimit + outputSlotLimit) return stack;
		
		ItemStack existing = this.inventory[slot];
		int limit = stack.getMaxStackSize();
		
		if (existing != null)
			{
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) return stack;
			
			limit -= existing.stackSize;
			}
			
		if (limit <= 0) return stack;
		
		boolean reachedLimit = stack.stackSize > limit;
		
		if (!simulate)
			{
			if (existing == null)
				{
				this.inventory[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
				}
			else
				{
				existing.stackSize += reachedLimit ? limit : stack.stackSize;
				}
			}
			
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
		}
		
	/** Inserts item somewhere */
	public ItemStack insertItem(ItemStack inputstack, boolean simulate)
		{
		if (inputstack == null || inputstack.stackSize == 0) return null;
		ItemStack stack = inputstack.copy();
		
		for (int i = 0; i < inputSlotLimit; i++)
			{
			stack = insertItem(i, stack, simulate);
			if (stack == null) break;
			}
			
		return stack;
		}
		
	// IItemHandlerCode
	@Override
	public int getSlots()
		{
		return ultimateSlotLimit;
		}
		
	@Override
	public ItemStack getStackInSlot(int slot)
		{
		return inventory[slot];
		}
		
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
		if (stack == null || stack.stackSize == 0) return null;
		
		if (slot < 0 || slot >= inputSlotLimit) return stack;
		
		ItemStack existing = this.inventory[slot];
		int limit = stack.getMaxStackSize();
		
		if (existing != null)
			{
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) return stack;
			
			limit -= existing.stackSize;
			}
			
		if (limit <= 0) return stack;
		
		boolean reachedLimit = stack.stackSize > limit;
		
		if (!simulate)
			{
			if (existing == null)
				{
				this.inventory[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
				}
			else
				{
				existing.stackSize += reachedLimit ? limit : stack.stackSize;
				}
			}
			
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
		}
		
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
		if (amount == 0) return null;
		
		if (slot < absoluteSlotLimit || slot >= absoluteSlotLimit + outputSlotLimit) return null;
		
		ItemStack existing = this.inventory[slot];
		
		if (existing == null) return null;
		
		int toExtract = Math.min(amount, existing.getMaxStackSize());
		
		if (existing.stackSize <= toExtract)
			{
			if (!simulate)
				{
				this.inventory[slot] = null;
				}
			return existing;
			}
		else
			{
			if (!simulate)
				{
				this.inventory[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract);
				}
				
			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
			}
		}
		
	public void setInputSlotLimit(int limit)
		{
		inputSlotLimit = limit;
		}
		
	public void setOutputSlotLimit(int limit)
		{
		outputSlotLimit = limit;
		}
	}
