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
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import princess.tenergistics.tools.PoweredTool;
import slimeknights.tconstruct.library.modifiers.capability.ToolFluidCapability.IFluidModifier;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class ToolFuelCapability implements ICapabilityProvider, IEnergyStorage
	{
	private final LazyOptional<IEnergyStorage>	energyHolder		= LazyOptional.of(() -> this);
	
	private static final int					MAX_ENERGY_TRANSFER	= 500;
	
	@Nonnull
	private final ItemStack						container;
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
		{
		if (cap == CapabilityEnergy.ENERGY && getMaxEnergyStored() > 0)
			{ return CapabilityEnergy.ENERGY.orEmpty(cap, energyHolder); }
		return LazyOptional.empty();
		}
		
	public ToolFuelCapability(ItemStack container)
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
		
	public static class PowerToolFluidTank implements IFluidModifier
		{
		@Override
		public int getTanks(IModDataReadOnly volatileData)
			{
			return 1;
			}
			
		@Override
		public boolean isFluidValid(IModifierToolStack tool, int level, int tank, FluidStack stack)
			{
			return MeltingFuelLookup.isFuel(stack.getFluid());
			}

		@Override
		public int getTankCapacity(IModifierToolStack tool, int level, int tank)
			{
			return PoweredTool.getFluidCapacity(tool);
			}
			
		@Override
		public FluidStack getFluidInTank(IModifierToolStack tool, int level, int tank)
			{
			return PoweredTool.getFluidStack(tool);
			}
			
		@Override
		public int fill(IModifierToolStack tool, int level, FluidStack resource, FluidAction action)
			{
			if (resource.isEmpty() || !isFluidValid(tool, level, 0, resource))
				{ return 0; }
				
			FluidStack contained = this.getFluidInTank(tool, level, 0);
			if (contained.isEmpty())
				{
				int fillAmount = Math.min(getTankCapacity(tool, level, 0), resource.getAmount());
				
				if (action.execute())
					{
					FluidStack filled = resource.copy();
					filled.setAmount(fillAmount);
					PoweredTool.setFluidStack(tool, filled);
					}
					
				return fillAmount;
				}
			else
				{
				if (contained.isFluidEqual(resource))
					{
					int fillAmount = Math.min(getTankCapacity(tool, level, 0) - contained.getAmount(), resource.getAmount());
					
					if (action.execute() && fillAmount > 0)
						{
						contained.grow(fillAmount);
						PoweredTool.setFluidStack(tool, contained);
						}
						
					return fillAmount;
					}
					
				return 0;
				}
			}
			
		@Override
		public FluidStack drain(IModifierToolStack tool, int level, FluidStack resource, FluidAction action)
			{
			if (resource.isEmpty() || !resource.isFluidEqual(getFluidInTank(tool, level, 0)))
				{ return FluidStack.EMPTY; }
			return drain(tool, level, resource.getAmount(), action);
			}
			
		@Override
		public FluidStack drain(IModifierToolStack tool, int level, int maxDrain, FluidAction action)
			{
			if (maxDrain <= 0)
				{ return FluidStack.EMPTY; }
				
			FluidStack contained = getFluidInTank(tool, level, 0);
			if (contained.isEmpty() || !isFluidValid(tool, level, 0, contained))
				{ return FluidStack.EMPTY; }
				
			final int drainAmount = Math.min(contained.getAmount(), maxDrain);
			
			FluidStack drained = contained.copy();
			drained.setAmount(drainAmount);
			
			if (action.execute())
				{
				contained.shrink(drainAmount);
				PoweredTool.setFluidStack(tool, contained);
				}
				
			return drained;
			}
		}
	}
