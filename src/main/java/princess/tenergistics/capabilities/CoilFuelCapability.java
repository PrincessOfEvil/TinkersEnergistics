package princess.tenergistics.capabilities;

import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.blocks.tileentity.SearedCoilTileEntity;

public class CoilFuelCapability<T extends SearedCoilTileEntity> implements IFluidHandler
	{
	protected T parent;
	
	public CoilFuelCapability(int capacity, T parent)
		{
		this.parent = parent;
		}
		
	@Override
	public int getTanks()
		{
		return 1;
		}
		
	@Override
	public FluidStack getFluidInTank(int tank)
		{
		return new FluidStack(TEnergistics.moltenEnergy.get(), parent.getCapability(CapabilityEnergy.ENERGY)
				.orElse(SearedCoilTileEntity.EMPTY)
				.getEnergyStored());
		}
		
	@Override
	public int getTankCapacity(int tank)
		{
		return parent.getCapability(CapabilityEnergy.ENERGY).orElse(SearedCoilTileEntity.EMPTY).getMaxEnergyStored();
		}
		
	@Override
	public boolean isFluidValid(int tank, FluidStack stack)
		{
		return stack.getFluid().isEquivalentTo(TEnergistics.moltenEnergy.get());
		}
		
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action)
		{
		if (resource.isEmpty() || !resource.isFluidEqual(getFluidInTank(0)))
			{ return FluidStack.EMPTY; }
		return drain(resource.getAmount(), action);
		}
		
	@Override
	public FluidStack drain(int maxDrain, FluidAction action)
		{
		if (maxDrain <= 0)
			{ return FluidStack.EMPTY; }
			
		FluidStack contained = getFluidInTank(0);
		if (contained.isEmpty() || !isFluidValid(0, contained))
			{ return FluidStack.EMPTY; }
			
		final int drainAmount = Math.min(contained.getAmount(), maxDrain);
		
		FluidStack drained = contained.copy();
		drained.setAmount(drainAmount);
		
		if (action.execute())
			{
			parent.getCapability(CapabilityEnergy.ENERGY)
					.orElse(SearedCoilTileEntity.EMPTY)
					.extractEnergy(drainAmount, false);
			
			parent.onContentsChanged();
			}
			
		return drained;
		}
		
	@Override
	public int fill(FluidStack resource, FluidAction action)
		{
		return 0;
		}
	}
