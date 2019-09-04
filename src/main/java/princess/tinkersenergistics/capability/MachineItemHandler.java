package princess.tinkersenergistics.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import princess.tinkersenergistics.block.tile.TileMachine;

public class MachineItemHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound>
	{
	public static final int			absoluteSlotLimit	= 9;
	public static final int			eternalSlotLimit	= 18;
	public static final int			ultimateSlotLimit	= 19;
	
	public int						inputSlotLimit		= 2;
	public int						outputSlotLimit		= 2;
	
	/**
	0-8 - processing input;
	9-17 - processing output;
	18 - power input.
	 */
	private NonNullList<ItemStack>	inventory			= NonNullList.withSize(ultimateSlotLimit, ItemStack.EMPTY);
	private TileMachine				parentTile;
	
	public MachineItemHandler(TileMachine tileMachine)
		{
		parentTile = tileMachine;
		}
		
	@Override
	public NBTTagCompound serializeNBT()
		{
		NBTTagList nbtTagList = new NBTTagList();
		for (int i = 0; i < inventory.size(); i++)
			{
			if (!inventory.get(i).isEmpty())
				{
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				inventory.get(i).writeToNBT(itemTag);
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
			
			if (slot >= 0 && slot < inventory.size())
				{
				inventory.set(slot, new ItemStack(itemTags));
				}
			}
		}
		
	/* Processing i/o is exactly the same, but from opposite slots - internal use only.*/
	/** Autosorts the inventory as well if simulate = false. */
	public ItemStack extractItemProcessing(int amount, boolean simulate)
		{
		if (!simulate)
			{
			NonNullList<ItemStack> storage = NonNullList.withSize(ultimateSlotLimit, ItemStack.EMPTY);
			
			for (int i = 0; i < ultimateSlotLimit; i++)
				{
				storage.set(i, inventory.get(i));
				}
				
			for (int i = 0; i < absoluteSlotLimit; i++)
				{
				inventory.set(i, ItemStack.EMPTY);
				insertItem(storage.get(i), false);
				}
			}
			
		int j = 0;
		for (int i = 0; i < absoluteSlotLimit; i++)
			{
			if (!extractItemProcessing(i, amount, true).isEmpty()) j = i;
			}
			
		return extractItemProcessing(j, amount, simulate);
		}
		
	public ItemStack extractItemProcessing(int slot, int amount, boolean simulate)
		{
		if (slot < 0 || slot >= absoluteSlotLimit) return ItemStack.EMPTY;
		
		ItemStack existing = this.inventory.get(slot);
		
		if (existing.isEmpty()) return ItemStack.EMPTY;
		
		int toExtract = Math.min(amount, existing.getMaxStackSize());
		
		if (existing.getCount() <= toExtract)
			{
			if (!simulate)
				{
				this.inventory.set(slot, ItemStack.EMPTY);
				}
			onContentsChanged();
			return existing;
			}
		else
			{
			if (!simulate)
				{
				this.inventory.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				}
			onContentsChanged();
			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
			}
		}
		
	public ItemStack insertItemProcessing(ItemStack inputstack, boolean simulate)
		{
		if (inputstack.isEmpty()) return ItemStack.EMPTY;
		ItemStack stack = inputstack.copy();
		
		for (int i = absoluteSlotLimit; i < absoluteSlotLimit + outputSlotLimit; i++)
			{
			stack = insertItemProcessing(i, stack, simulate);
			if (stack.isEmpty()) break;
			}
			
		return stack;
		}
		
	public ItemStack insertItemProcessing(int slot, ItemStack stack, boolean simulate)
		{
		if (stack.isEmpty()) return ItemStack.EMPTY;
		
		if (slot < absoluteSlotLimit || slot >= absoluteSlotLimit + outputSlotLimit) return stack;
		
		ItemStack existing = this.inventory.get(slot);
		int limit = stack.getMaxStackSize();
		
		if (!existing.isEmpty())
			{
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) return stack;
			
			limit -= existing.getCount();
			}
			
		if (limit <= 0) return stack;
		
		boolean reachedLimit = stack.getCount() > limit;
		
		if (!simulate)
			{
			if (existing.isEmpty())
				{
				this.inventory.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
				}
			else
				{
				existing.grow(reachedLimit ? limit : stack.getCount());
				}
			}
		onContentsChanged();
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
		}
		
	public ItemStack insertItemFuel(ItemStack stack, boolean simulate)
		{
		if (TileEntityFurnace.getItemBurnTime(stack) == 0) return stack;
		int slot = 18;
		ItemStack existing = this.inventory.get(slot);
		int limit = stack.getMaxStackSize();
		
		if (!existing.isEmpty())
			{
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) return stack;
			
			limit -= existing.getCount();
			}
			
		if (limit <= 0) return stack;
		
		boolean reachedLimit = stack.getCount() > limit;
		
		if (!simulate)
			{
			if (existing.isEmpty())
				{
				this.inventory.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
				}
			else
				{
				existing.grow(reachedLimit ? limit : stack.getCount());
				}
			}
		onContentsChanged();
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
		}
		
	public ItemStack extractItemFuel(int amount, boolean simulate)
		{
		int slot = 18;
		if (amount == 0) return ItemStack.EMPTY;
		
		ItemStack existing = this.inventory.get(slot);
		
		if (existing.isEmpty()) return ItemStack.EMPTY;
		
		int toExtract = Math.min(amount, existing.getMaxStackSize());
		
		if (existing.getCount() <= toExtract)
			{
			if (!simulate)
				{
				this.inventory.set(slot, ItemStack.EMPTY);
				}
			onContentsChanged();
			return existing;
			}
		else
			{
			if (!simulate)
				{
				this.inventory.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				}
			onContentsChanged();
			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
			}
		}
		
	/** Inserts item somewhere */
	public ItemStack insertItem(ItemStack inputstack, boolean simulate)
		{
		if (inputstack.isEmpty()) return ItemStack.EMPTY;
		ItemStack stack = inputstack.copy();
		
		for (int i = 0; i < inputSlotLimit; i++)
			{
			stack = insertItem(i, stack, simulate);
			if (stack.isEmpty()) break;
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
	public int getSlotLimit(int slot)
		{
		return 64;
		}
		
	@Override
	public ItemStack getStackInSlot(int slot)
		{
		return inventory.get(slot);
		}
		
	@Override
	public void setStackInSlot(int slot, ItemStack stack)
		{
		if (ItemStack.areItemStacksEqual(inventory.get(slot), stack)) return;
		this.inventory.set(slot, stack);
		onContentsChanged();
		}
		
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
		if (stack.isEmpty()) return ItemStack.EMPTY;
		
		if (slot == 18) return insertItemFuel(stack, simulate);
		
		if (slot < 0 || slot >= inputSlotLimit) return stack;
		
		if (parentTile.craftingResult(stack).isEmpty()) return stack;
		
		ItemStack existing = this.inventory.get(slot);
		int limit = stack.getMaxStackSize();
		
		if (!existing.isEmpty())
			{
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) return stack;
			
			limit -= existing.getCount();
			}
			
		if (limit <= 0) return stack;
		
		boolean reachedLimit = stack.getCount() > limit;
		
		if (!simulate)
			{
			if (existing.isEmpty())
				{
				this.inventory.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
				}
			else
				{
				existing.grow(reachedLimit ? limit : stack.getCount());
				}
			}
			
		onContentsChanged();
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
		}
		
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
		if (amount == 0) return ItemStack.EMPTY;
		
		if (slot < absoluteSlotLimit || slot >= absoluteSlotLimit + outputSlotLimit) return ItemStack.EMPTY;
		
		ItemStack existing = this.inventory.get(slot);
		
		if (existing.isEmpty()) return ItemStack.EMPTY;
		
		int toExtract = Math.min(amount, existing.getMaxStackSize());
		
		if (existing.getCount() <= toExtract)
			{
			if (!simulate)
				{
				this.inventory.set(slot, ItemStack.EMPTY);
				}
			onContentsChanged();
			return existing;
			}
		else
			{
			if (!simulate)
				{
				this.inventory.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				}
			onContentsChanged();
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
		
	protected void onContentsChanged()
		{
		parentTile.markDirty();
		}
		
	public NonNullList<ItemStack> getInventory()
		{
		return inventory;
		}
		
	}
