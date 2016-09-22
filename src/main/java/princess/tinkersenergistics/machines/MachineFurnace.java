package princess.tinkersenergistics.machines;

import princess.tinkersenergistics.TEnergistics;
import princess.tinkersenergistics.library.MachineCore;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;

public class MachineFurnace extends MachineCore
	{
	public MachineFurnace()
		{
		super(	PartMaterialType.head(TEnergistics.machineCasing),
				PartMaterialType.handle(TEnergistics.machineGearbox),
				PartMaterialType.extra(TEnergistics.machineHeater));
		}
	}
