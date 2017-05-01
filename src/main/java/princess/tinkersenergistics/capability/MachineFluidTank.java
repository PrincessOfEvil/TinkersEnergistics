package princess.tinkersenergistics.capability;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import slimeknights.tconstruct.library.TinkerRegistry;

public class MachineFluidTank extends FluidTank
	{
	public MachineFluidTank(int capacity)
		{
		super(capacity);
		}

	protected boolean canDrain = false;
		
	public boolean canFillFluidType(FluidStack fluid)
		{
		for (FluidStack stack : TinkerRegistry.getSmelteryFuels())
			{
			if (fluid.isFluidEqual(stack)) return true;
			}
		return false;
		}
	}
