package princess.tenergistics.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import princess.tenergistics.tools.PoweredTool;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;

public class ToolFuelTank implements IFluidHandlerItem, ICapabilityProvider, IEnergyStorage
	{
	
	private final LazyOptional<IFluidHandlerItem>	fluidHolder			= LazyOptional.of(() -> this);
	private final LazyOptional<IEnergyStorage>		energyHolder		= LazyOptional.of(() -> this);
	
	private static final int						MAX_ENERGY_TRANSFER	= 500;
	
	@Nonnull
	private final ItemStack							container;
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
		{
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && getTankCapacity() > 0)
			{ return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, fluidHolder); }
		if (cap == CapabilityEnergy.ENERGY && getMaxEnergyStored() > 0)
			{ return CapabilityEnergy.ENERGY.orEmpty(cap, energyHolder); }
		return LazyOptional.empty();
		}
		
	public ToolFuelTank(ItemStack container)
		{
		this.container = container;
		}
		
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
		{
		int energyReceived = Math
				.min(getMaxEnergyStored() - getEnergyStored(), Math.min(MAX_ENERGY_TRANSFER, maxReceive));
		if (!simulate)
			{
			PoweredTool.setEnergy(container, getEnergyStored() + energyReceived);
			}
		return energyReceived;
		}
		
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
		{
		// fuck off with your nonsense
		return 0;
		}
		
	@Override
	public int getEnergyStored()
		{
		return PoweredTool.getEnergy(container);
		}
		
	@Override
	public int getMaxEnergyStored()
		{
		return PoweredTool.getMaxEnergy(container);
		}
		
	@Override
	public boolean canExtract()
		{
		//This ain't a battery.
		return false;
		}
		
	@Override
	public boolean canReceive()
		{
		return true;
		}
		
	@Override
	public int getTanks()
		{
		return 1;
		
		}
		
	@Override
	public boolean isFluidValid(int tank, FluidStack stack)
		{
		return isFluidValid(stack);
		}
		
	public boolean isFluidValid(FluidStack stack)
		{
		/*
		return stack.getFluid().isEquivalentTo(Fluids.LAVA);
		/**/
		return MeltingFuelLookup.isFuel(stack.getFluid());
		}
		
	public int getTankCapacity()
		{
		return PoweredTool.getFluidCapacity(container);
		}
		
	@Override
	public int getTankCapacity(int tank)
		{
		return getTankCapacity();
		}
		
	@Override
	public ItemStack getContainer()
		{
		return container;
		}
		
	@Override
	public FluidStack getFluidInTank(int tank)
		{
		return getFluidInTank();
		}
		
	public FluidStack getFluidInTank()
		{
		return PoweredTool.getFluidStack(container);
		}
		
	@Override
	public int fill(FluidStack resource, FluidAction doFill)
		{
		if (container.getCount() != 1 || resource.isEmpty() || !isFluidValid(resource))
			{ return 0; }
			
		FluidStack contained = this.getFluidInTank();
		if (contained.isEmpty())
			{
			int fillAmount = Math.min(getTankCapacity(), resource.getAmount());
			
			if (doFill.execute())
				{
				FluidStack filled = resource.copy();
				filled.setAmount(fillAmount);
				PoweredTool.setFluidStack(container, filled);
				}
				
			return fillAmount;
			}
		else
			{
			if (contained.isFluidEqual(resource))
				{
				int fillAmount = Math.min(getTankCapacity() - contained.getAmount(), resource.getAmount());
				
				if (doFill.execute() && fillAmount > 0)
					{
					contained.grow(fillAmount);
					PoweredTool.setFluidStack(container, contained);
					}
					
				return fillAmount;
				}
				
			return 0;
			}
		}
		
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action)
		{
		if (container.getCount() != 1 || resource.isEmpty() || !resource.isFluidEqual(getFluidInTank()))
			{ return FluidStack.EMPTY; }
		return drain(resource.getAmount(), action);
		}
		
	@Override
	public FluidStack drain(int maxDrain, FluidAction action)
		{
		if (container.getCount() != 1 || maxDrain <= 0)
			{ return FluidStack.EMPTY; }
			
		FluidStack contained = getFluidInTank();
		if (contained.isEmpty() || !isFluidValid(contained))
			{ return FluidStack.EMPTY; }
			
		final int drainAmount = Math.min(contained.getAmount(), maxDrain);
		
		FluidStack drained = contained.copy();
		drained.setAmount(drainAmount);
		
		if (action.execute())
			{
			contained.shrink(drainAmount);
			PoweredTool.setFluidStack(container, contained);
			}
			
		return drained;
		}
	}
