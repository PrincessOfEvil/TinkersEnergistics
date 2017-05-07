package princess.tinkersenergistics.item;

import princess.tinkersenergistics.TinkersEnergistics;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;

public class MachineFurnace extends MachineCore
	{
	public MachineFurnace()
		{
		super(0,
				PartMaterialType.handle(TinkersEnergistics.machineHeater),
				PartMaterialType.head(TinkersEnergistics.machineCasing),
				PartMaterialType.extra(TinkersEnergistics.machineGearbox));
		}
	}
