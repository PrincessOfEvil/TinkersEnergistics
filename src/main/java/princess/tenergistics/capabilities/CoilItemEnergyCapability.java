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
import princess.tenergistics.blocks.tileentity.SearedCoilTileEntity;
import princess.tenergistics.items.SearedCoilItemBlock;

public class CoilItemEnergyCapability implements ICapabilityProvider, IEnergyStorage
	{
	private final LazyOptional<IEnergyStorage>	energyHolder	= LazyOptional.of(() -> this);
	
	@Nonnull
	private final ItemStack						container;
	
	public CoilItemEnergyCapability(ItemStack container)
		{
		this.container = container;
		}
		
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
		{
		if (cap == CapabilityEnergy.ENERGY && getMaxEnergyStored() > 0)
			{ return CapabilityEnergy.ENERGY.orEmpty(cap, energyHolder); }
		return LazyOptional.empty();
		}
		
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
		{
		int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), maxReceive);
		if (!simulate)
			{
			SearedCoilItemBlock.setEnergy(container, getEnergyStored() + energyReceived);
			}
		return energyReceived;
		}
		
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
		{
		int energyExtracted = Math.min(getEnergyStored(), maxExtract);
		if (!simulate)
			{
			SearedCoilItemBlock.setEnergy(container, getEnergyStored() - energyExtracted);
			}
		return energyExtracted;
		}
		
	@Override
	public int getEnergyStored()
		{
		return SearedCoilItemBlock.getEnergy(container);
		}
		
	@Override
	public int getMaxEnergyStored()
		{
		return SearedCoilTileEntity.CAPACITY;
		}
		
	@Override
	public boolean canExtract()
		{
		return true;
		}
		
	@Override
	public boolean canReceive()
		{
		return true;
		}
	}
