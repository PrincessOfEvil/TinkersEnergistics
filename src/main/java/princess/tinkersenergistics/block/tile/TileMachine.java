package princess.tinkersenergistics.block.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import princess.tinkersenergistics.capability.MachineItemHandler;
import slimeknights.tconstruct.library.utils.TagUtil;

// This class is mostly boilerplate. The resulting tile can be used as a furnace tho.
public class TileMachine extends TileEntity implements ITickable
	{
	private ItemStack			parentItem	= null;
	
	private String[]			tags		= { "InputSlots", "OutputSlots", "CookTime", "FireTicks", "Inventory", "ParentItem" };
	
	private MachineItemHandler	inventory	= new MachineItemHandler();
	
	private int					cookTime	= 0;
	private int					fireTicks	= 0;
	
	@Override
	public void update()
		{
		if (!this.worldObj.isRemote)
			{
			if (canCraft()) craft();
			
			}
		}
		
	/** THE method to replace.*/
	private ItemStack craftingResult(ItemStack stack)
		{
		return FurnaceRecipes.instance().getSmeltingResult(stack);
		}
		
	private boolean canCraft()
		{
		ItemStack item = inventory.extractItemProcessing(1, true);
		
		if (item == null) return false;
		
		ItemStack stack = craftingResult(item);
		
		if (stack == null) return false;
		if (inventory.insertItemProcessing(stack, true) == null) return true;
		
		return false;
		}
		
	private void craft()
		{
		fireTicks = 0;
		ItemStack item = inventory.extractItemProcessing(1, false);
		inventory.insertItemProcessing(craftingResult(item), false);
		
		markDirty();
		}
		
	/* here come dat boi!! */
	
	public void readFromNBT(NBTTagCompound compound)
		{
		super.readFromNBT(compound);
		
		cookTime = compound.getInteger(tags[2]);
		fireTicks = compound.getInteger(tags[3]);
		
		inventory.deserializeNBT(compound.getCompoundTag(tags[4]));
		
		if (compound.hasKey(tags[5])) setParentItem(ItemStack.loadItemStackFromNBT(compound.getCompoundTag(tags[5])));
		}
		
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
		{
		super.writeToNBT(compound);
		
		compound.setInteger(tags[2], cookTime);
		compound.setInteger(tags[3], fireTicks);
		
		compound.setTag(tags[4], inventory.serializeNBT());
		
		if (parentItem != null) compound.setTag(tags[5], parentItem.writeToNBT(new NBTTagCompound()));
		
		return compound;
		}
		
	public String getName()
		{
		return parentItem == null ? parentItem.getDisplayName() : "container.furnace";
		}
		
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return true; }
		return super.hasCapability(capability, facing);
		}
		
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return (T) inventory; }
		return super.getCapability(capability, facing);
		}
		
	public ItemStack getParentItem()
		{
		return parentItem;
		}
		
	public void setParentItem(ItemStack parentItem)
		{
		this.parentItem = parentItem;
		NBTTagCompound tag = TagUtil.getTagSafe(parentItem);
		if (tag.getInteger(tags[0]) > 0) inventory.setInputSlotLimit(tag.getInteger(tags[0]));
		if (tag.getInteger(tags[1]) > 0) inventory.setOutputSlotLimit(tag.getInteger(tags[1]));
		if (tag.getInteger(tags[2]) > 0) cookTime = tag.getInteger(tags[2]);
		}
	}
