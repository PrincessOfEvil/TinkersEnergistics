package princess.tinkersenergistics.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import princess.tinkersenergistics.capability.MachineItemHandler;

//hue
public class SlotMachine extends SlotItemHandler
	{
	
	private int index;
	
	public SlotMachine(IItemHandler itemHandler, int index, int xPosition, int yPosition)
		{
		super(itemHandler, index, xPosition, yPosition);
		this.index = index;
		}
		
	@Override
	public boolean canTakeStack(EntityPlayer playerIn)
		{
		return true;
		}
	
	@Override
	public ItemStack decrStackSize(int amount)
		{
		if (index == 18) return ((MachineItemHandler)getItemHandler()).extractItemFuel(amount, false);
		
		ItemStack stack = getItemHandler().extractItem(index, amount, false);
		if (stack.isEmpty()) stack = ((MachineItemHandler)getItemHandler()).extractItemProcessing(index, amount, false);		
		return stack;
		}
	}
