package princess.tenergistics.blocks.tileentity;

import net.minecraft.tileentity.TileEntityType;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

public class ScrewPumpTileEntity extends FaucetTileEntity
	{
	public ScrewPumpTileEntity()
		{
		this(TEnergistics.screwPump.get());
		}
		
	protected ScrewPumpTileEntity(TileEntityType<?> tileEntityTypeIn)
		{
		super(tileEntityTypeIn);
		}
	}