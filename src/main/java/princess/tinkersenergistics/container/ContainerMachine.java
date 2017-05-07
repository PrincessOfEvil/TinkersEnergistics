package princess.tinkersenergistics.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import princess.tinkersenergistics.block.tile.TileMachine;
import princess.tinkersenergistics.capability.MachineItemHandler;

public class ContainerMachine extends Container
	{
	private TileMachine	tile;
	
	private int			cookTime		= 100;
	private int			fireTicks		= 0;
	private int			fuelTicks		= 0;
	private int			fuelTicksMax	= 0;
	
	private boolean		fluidPowered	= false;
	private boolean		energyPowered	= false;
	
	private int			type			= 0;
	
	public ContainerMachine(IInventory playerInventory, TileMachine tile)
		{
		this.tile = tile;
		addPlayerSlots(playerInventory);
		addOwnSlots();
		}
		
	@Override
	public void detectAndSendChanges()
		{
		super.detectAndSendChanges();
		for (int i = 0; i < this.listeners.size(); i++)
			{
			IContainerListener icontainerlistener = (IContainerListener) this.listeners.get(i);
			
			if (cookTime != tile.cookTime)
				{
				icontainerlistener.sendProgressBarUpdate(this, 0, tile.cookTime);
				}
				
			if (fireTicks != tile.fireTicks)
				{
				icontainerlistener.sendProgressBarUpdate(this, 1, tile.fireTicks);
				}
				
			if (fuelTicks != tile.fuelTicks)
				{
				icontainerlistener.sendProgressBarUpdate(this, 2, tile.fuelTicks);
				}
				
			if (fuelTicksMax != tile.fuelTicksMax)
				{
				icontainerlistener.sendProgressBarUpdate(this, 3, tile.fuelTicksMax);
				}
				
			if (fluidPowered != tile.fluidPowered)
				{
				icontainerlistener.sendProgressBarUpdate(this, 4, tile.fluidPowered ? 1 : 0);
				}
				
			if (energyPowered != tile.energyPowered)
				{
				icontainerlistener.sendProgressBarUpdate(this, 5, tile.energyPowered ? 1 : 0);
				}
			if (type != tile.type)
				{
				icontainerlistener.sendProgressBarUpdate(this, 6, tile.type);
				}
			}
			
		cookTime = tile.cookTime;
		fireTicks = tile.fireTicks;
		fuelTicks = tile.fuelTicks;
		fuelTicksMax = tile.fuelTicksMax;
		
		fluidPowered = tile.fluidPowered;
		energyPowered = tile.energyPowered;
		
		type = tile.type;
		}
		
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
		{
		switch (id)
			{
			case 0:
				tile.cookTime = data;
				break;
			case 1:
				tile.fireTicks = data;
				break;
			case 2:
				tile.fuelTicks = data;
				break;
			case 3:
				tile.fuelTicksMax = data;
				break;
			case 4:
				tile.fluidPowered = data == 1;
				break;
			case 5:
				tile.energyPowered = data == 1;
				break;
			case 6:
				tile.type = data;
				break;
			}
		}
		
	public void addListener(IContainerListener listener)
		{
		super.addListener(listener);
		detectAndSendChangesAnyway();
		}
		
	private void detectAndSendChangesAnyway()
		{
		for (int i = 0; i < this.listeners.size(); i++)
			{
			IContainerListener icontainerlistener = (IContainerListener) this.listeners.get(i);
			
			icontainerlistener.sendProgressBarUpdate(this, 0, tile.cookTime);
			icontainerlistener.sendProgressBarUpdate(this, 1, tile.fireTicks);
			icontainerlistener.sendProgressBarUpdate(this, 2, tile.fuelTicks);
			icontainerlistener.sendProgressBarUpdate(this, 3, tile.fuelTicksMax);
			icontainerlistener.sendProgressBarUpdate(this, 4, tile.fluidPowered ? 1 : 0);
			icontainerlistener.sendProgressBarUpdate(this, 5, tile.energyPowered ? 1 : 0);
			icontainerlistener.sendProgressBarUpdate(this, 6, tile.type);
			}
		}
		
	private void addPlayerSlots(IInventory playerInventory)
		{
		// Slots for the main inventory
		int shift = 92;
		for (int row = 0; row < 3; ++row)
			{
			for (int col = 0; col < 9; ++col)
				{
				int x = 8 + col * 18;
				int y = row * 18 + shift;
				this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
				}
			}
			
		// Slots for the hotbar
		for (int row = 0; row < 9; ++row)
			{
			int x = 8 + row * 18;
			int y = 58 + shift;
			this.addSlotToContainer(new Slot(playerInventory, row, x, y));
			}
		}
		
	private void addOwnSlots()
		{
		MachineItemHandler itemHandler = (MachineItemHandler) this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		int x = 21;
		int y = 21;
		
		int slotIndex = 0;
		// inputs
		for (int i = 0; i < 3; i++)
			{
			for (int j = 0; j < 3; j++)
				{
				if (slotIndex < itemHandler.inputSlotLimit)
					{
					addSlotToContainer(new SlotMachine(itemHandler, slotIndex, x, y));
					}
				slotIndex++;
				x += 18;
				}
			x -= 18 * 3;
			y += 18;
			}
			
		x += 18 * 3 + 28;
		y -= 18 * 3;
		slotIndex = 9;
		// outputs
		for (int i = 0; i < 3; i++)
			{
			for (int j = 0; j < 3; j++)
				{
				if (slotIndex < itemHandler.outputSlotLimit + 9)
					{
					addSlotToContainer(new SlotMachine(itemHandler, slotIndex, x, y));
					}
				slotIndex++;
				x += 18;
				}
			x -= 18 * 3;
			y += 18;
			}
			
		x -= 18 + 5;
		y -= 18;
		// fuel slot
		addSlotToContainer(new SlotMachine(itemHandler, 18, x, y));
		}
		
	@Nullable
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
		{
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(index);
		
		if (slot != null && slot.getHasStack())
			{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			IItemHandler itemHandler = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			
			if (index < itemHandler.getSlots())
				{
				if (!this.mergeItemStack(itemstack1, itemHandler.getSlots(), this.inventorySlots.size(), true)) return null;
				}
			else
				if (!this.mergeItemStack(itemstack1, 0, itemHandler.getSlots(), false)) return null;
			
			if (itemstack1.stackSize == 0)
				{
				slot.putStack(null);
				}
			else
				{
				slot.onSlotChanged();
				}
			}
		return itemstack;
		}
		
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
		{
		return tile.canInteractWith(playerIn);
		}
	}
