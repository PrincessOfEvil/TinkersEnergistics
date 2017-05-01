package princess.tinkersenergistics.capability;

import net.minecraftforge.energy.EnergyStorage;

public class MachineEnergyStorage extends EnergyStorage
	{
	
	public MachineEnergyStorage(int capacity)
		{
		super(capacity, capacity, 0);
		}
		
	public int extractEnergyProcessing(int maxExtract, boolean simulate)
		{
		int energyExtracted = Math.min(energy, maxExtract);
		if (!simulate) energy -= energyExtracted;
		return energyExtracted;
		}
	}
