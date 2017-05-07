package princess.tinkersenergistics.item;

import princess.tinkersenergistics.TinkersEnergistics;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;

public class MachineCrusher extends MachineCore
	{
	public MachineCrusher()
		{
		super(1,
				PartMaterialType.handle(TinkersEnergistics.machineMill),
				PartMaterialType.head(TinkersEnergistics.machineCasing),
				PartMaterialType.extra(TinkersEnergistics.machineGearbox));
		addCategory(TinkersEnergistics.TIE_CRUSHER);
		}
	}
