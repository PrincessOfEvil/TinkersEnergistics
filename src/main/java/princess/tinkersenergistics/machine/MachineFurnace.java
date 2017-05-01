package princess.tinkersenergistics.machine;

import princess.tinkersenergistics.TinkersEnergistics;
import princess.tinkersenergistics.library.MachineCore;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;

public class MachineFurnace extends MachineCore
	{
	public MachineFurnace()
		{
		super(	PartMaterialType.handle(TinkersEnergistics.machineGearbox),
				PartMaterialType.head(TinkersEnergistics.machineCasing),
				PartMaterialType.extra(TinkersEnergistics.machineHeater));
		}
	}
