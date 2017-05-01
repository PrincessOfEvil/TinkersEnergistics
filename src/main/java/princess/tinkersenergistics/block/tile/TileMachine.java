package princess.tinkersenergistics.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import princess.tinkersenergistics.capability.MachineEnergyStorage;
import princess.tinkersenergistics.capability.MachineFluidTank;
import princess.tinkersenergistics.capability.MachineItemHandler;
import princess.tinkersenergistics.common.ConfigHandler;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.utils.TagUtil;

// This class is mostly boilerplate. The resulting tile can be used as a furnace tho.
public class TileMachine extends TileEntity implements ITickable
	{
	private ItemStack				parentItem		= null;
	
	public int						cookTime		= 100;									 // How much time it takes to cook
	public int						fireTicks		= 0;									 // Current progress, counts up
	public int						fuelTicks		= 0;									 // Consumed fuel, in fireticks
	
	public boolean					fluidPowered	= false;
	public boolean					energyPowered	= false;
	
	private MachineItemHandler		inventory;
	private MachineFluidTank		fuelTank;
	private MachineEnergyStorage	energyStorage;
	
	public TileMachine()
		{
		inventory = new MachineItemHandler(this);
		fuelTank = new MachineFluidTank(0);
		energyStorage = new MachineEnergyStorage(0);
		}
		
	@Override
	public void update()
		{
		if (!this.worldObj.isRemote)
			{
			if (canStartCrafting())
				{
				if (fuelTicks == 0)
					{
					consumeFuel();
					}
				if (fuelTicks > 0)
					{
					fireTicks++;
					fuelTicks--;
					}
				}
			if (canCraft()) craft();
			}
		}
		
	/** THE method to replace.*/
	public ItemStack craftingResult(ItemStack stack)
		{
		return FurnaceRecipes.instance().getSmeltingResult(stack);
		}
		
	// Method to balance
	private boolean consumeFuel()
		{
		if (fluidPowered)
			{
			FluidStack liquid = fuelTank.getFluid();
			if (liquid != null)
				{
				FluidStack in = liquid.copy();
				int amount = liquid.amount - in.amount;
				FluidStack drained = fuelTank.drain(amount, false);
				if (drained != null && drained.amount == amount)
					{
					fuelTank.drain(amount, true);
					fuelTicks += TinkerRegistry.consumeSmelteryFuel(in) * ConfigHandler.fluidFuelMultiplier;
					return true;
					}
				}
			return false;
			}
		else
			if (energyPowered)
				{
				if (energyStorage.getEnergyStored() >= ConfigHandler.energyPerCraft)
					{
					energyStorage.extractEnergyProcessing(ConfigHandler.energyPerCraft, false);
					fuelTicks += ConfigHandler.fireTicksInTwoSticks;
					return true;
					}
				else
					if (energyStorage.getEnergyStored() >= ConfigHandler.energyPerNanoCraft)
						{
						energyStorage.extractEnergyProcessing(ConfigHandler.energyPerNanoCraft, false);
						fuelTicks++;
						return true;
						}
				}
			else
				{
				ItemStack stack = inventory.extractItemFuel(1, false);
				if (stack == null) return false;
				fuelTicks += TileEntityFurnace.getItemBurnTime(stack);
				return true;
				}
		return false;
		}
		
	private boolean canStartCrafting()
		{
		ItemStack item = inventory.extractItemProcessing(1, true);
		
		if (item == null) return false;
		
		ItemStack stack = craftingResult(item);
		
		if (stack == null) return false;
		if (inventory.insertItemProcessing(stack, true) == null) return true;
		
		return false;
		}
		
	private boolean canCraft()
		{
		if (fireTicks < cookTime) return false;
		return canStartCrafting();
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
		
		cookTime = compound.getInteger("CookTime");
		fireTicks = compound.getInteger("FireTicks");
		fuelTicks = compound.getInteger("FuelTicks");
		
		inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
		
		if (compound.hasKey("ParentItem")) setParentItem(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("ParentItem")));
		if (fluidPowered) fuelTank.readFromNBT((NBTTagCompound) compound.getTag("Tank"));
		if (energyPowered) energyStorage.receiveEnergy(compound.getInteger("Energy"), false);
		}
		
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
		{
		super.writeToNBT(compound);
		
		compound.setInteger("CookTime", cookTime);
		compound.setInteger("FireTicks", fireTicks);
		compound.setInteger("FuelTicks", fuelTicks);
		
		compound.setTag("Inventory", inventory.serializeNBT());
		if (fluidPowered) compound.setTag("Tank", fuelTank.writeToNBT(new NBTTagCompound()));
		if (energyPowered) compound.setInteger("Energy", energyStorage.getEnergyStored());
		
		if (parentItem != null) compound.setTag("ParentItem", parentItem.writeToNBT(new NBTTagCompound()));
		
		return compound;
		}
		
	public String getName()
		{
		return parentItem != null ? parentItem.getDisplayName() : "container.furnace";
		}
		
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fluidPowered) return true;
		if (capability == CapabilityEnergy.ENERGY && energyPowered) return true;
		return super.hasCapability(capability, facing);
		}
		
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) inventory;
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fluidPowered) return (T) fuelTank;
		if (capability == CapabilityEnergy.ENERGY && energyPowered) return (T) energyStorage;
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
		
		if (tag.getInteger("InputSlots") > 0) inventory.setInputSlotLimit(tag.getInteger("InputSlots"));
		if (tag.getInteger("OutputSlots") > 0) inventory.setOutputSlotLimit(tag.getInteger("OutputSlots"));
		if (tag.getInteger("CookTime") > 0) cookTime = tag.getInteger("CookTime");
		
		if (tag.getInteger("Tank") > 0)
			{
			fuelTank = new MachineFluidTank(tag.getInteger("Tank"));
			fluidPowered = true;
			}
		if (tag.getInteger("EnergyStorage") > 0)
			{
			energyStorage = new MachineEnergyStorage(tag.getInteger("EnergyStorage"));
			energyPowered = true;
			}
		}

	public boolean canInteractWith(EntityPlayer playerIn)
		{
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
		}
	}
