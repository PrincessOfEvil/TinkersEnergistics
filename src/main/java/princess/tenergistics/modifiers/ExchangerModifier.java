package princess.tenergistics.modifiers;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fluids.FluidStack;
import princess.tenergistics.tools.PoweredTool;
import princess.tenergistics.tools.ToolDefinitions;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class ExchangerModifier extends PowerSourceModifier
	{
	public static final int		CAPACITY				= 1000;
	private static final int	COLOR					= 0xffc12d;
	//Very non-descriptive name. When calculated, lava has a duration multiplier of 2, so the value becomes 10 (the actual amount is 5, or one stick).
	private static final int	LAVATICKS_PER_OPERATION	= 10;
	private static final float	LAVA_TEMPERATURE		= 1000;
	
	public ExchangerModifier()
		{
		super(COLOR);
		}
		
	@Override
	public boolean isPowered(IModifierToolStack tool, boolean dirty)
		{
		FluidStack stack = PoweredTool.getFluidStack(tool);
		
		return stack.getAmount() >= getTicks(stack.getFluid());
		}
		
	@Override
	public void drainPower(IModifierToolStack tool)
		{
		FluidStack stack = PoweredTool.getFluidStack(tool);
		
		stack.shrink(getTicks(stack.getFluid()));
		PoweredTool.setFluidStack(tool, stack);
		}
		
	@Override
	public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, boolean isEffective, float miningSpeedModifier)
		{
		if (isEffective && isPowered(tool, true))
			{
			float temperature = MeltingFuelLookup.findFuel(PoweredTool.getFluidStack(tool).getFluid())
					.getTemperature() / LAVA_TEMPERATURE;
			
			event.setNewSpeed(event.getNewSpeed() * ToolDefinitions.SPEED_MULTIPLIER * temperature);
			}
		}
		
	@Override
	public int getDurabilityRGB(IModifierToolStack tool, int level)
		{
		if (isPowered(tool, false))
			{
			FluidStack fluid = PoweredTool.getFluidStack(tool);
			switch (fluid.getDisplayName().getString())
				{
				case "Lava":
					return 0xff8000;
				case "Molten Blaze":
					return 0xffff00;
				default:
					return COLOR;
				}
			}
		return -1;
		}
		
	@Override
	public double getDamagePercentage(IModifierToolStack tool, int level)
		{
		FluidStack fluid = PoweredTool.getFluidStack(tool);
		if (!fluid.isEmpty())
			{
			int cap = tool.getVolatileData().getInt(PoweredTool.FLUID_LOCATION);
			int current = fluid.getAmount();
			if (current > cap)
				{ return 0; }
			return ((double) (cap - current) / cap);
			}
		return Double.NaN;
		}
		
	@Override
	public ITextComponent getDisplayName(IModifierToolStack tool, int level)
		{
		FluidStack fluid = PoweredTool.getFluidStack(tool);
		if (fluid.isEmpty()) return getDisplayName().deepCopy().appendString(" (EMPTY)");
		return getDisplayName().deepCopy()
				.appendString(": ")
				.append(fluid.getDisplayName())
				.appendString(String.format(" (%s / %s)", fluid.getAmount(), tool.getVolatileData()
						.getInt(PoweredTool.FLUID_LOCATION)));
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		super.addVolatileData(toolDefinition, baseStats, persistentData, level, volatileData);
		volatileData.putInt(PoweredTool.FLUID_LOCATION, CAPACITY);
		}
		
	private int getTicks(Fluid fluid)
		{
		MeltingFuel fuel = MeltingFuelLookup.findFuel(fluid);
		if (fuel == null) return Integer.MAX_VALUE;
		return Math.round(LAVATICKS_PER_OPERATION * fuel.getAmount(fluid) / ((float) fuel.getDuration()));
		}
	}