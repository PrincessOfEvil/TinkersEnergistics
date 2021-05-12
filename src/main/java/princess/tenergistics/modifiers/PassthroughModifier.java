package princess.tenergistics.modifiers;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class PassthroughModifier extends SingleUseModifier
	{
	public PassthroughModifier()
		{
		super(0x483020);
		}
		
	@Override
	public int getPriority()
		{
		return 13666;
		}
		
	@Override
	public int onDamageTool(IModifierToolStack tool, int level, int amount)
		{
		FluidStack stack = TinkerModifiers.tank.get().getFluid(tool).copy();
		if (!stack.isEmpty())
			{
			TinkerModifiers.tank.get()
					.drain(tool, stack, TEnergistics.exchangerModifier.get().tank
							.fill(tool, level, stack.copy(), FluidAction.EXECUTE));
			}
		return amount;
		}
	}
