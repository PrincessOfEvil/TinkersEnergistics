package princess.tenergistics.modifiers;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.capabilities.ToolFuelCapability.PowerToolFluidTank;
import princess.tenergistics.library.PowerSourceModifier;
import princess.tenergistics.tools.PoweredTool;
import princess.tenergistics.tools.ToolDefinitions;
import slimeknights.tconstruct.library.modifiers.capability.ToolFluidCapability;
import slimeknights.tconstruct.library.modifiers.capability.ToolFluidCapability.IFluidModifier;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class ExchangerModifier extends PowerSourceModifier
	{
	public static final int												CAPACITY				= 1000;
	private static final int											COLOR					= 0xffc12d;
	//Very non-descriptive name. When calculated, lava has a duration multiplier of 2, so the value becomes 10 (the actual amount is 5, or one stick).
	protected static final int											LAVATICKS_PER_OPERATION	= 10;
	protected static final float										LAVA_TEMPERATURE		= 1000;
	
	protected static final HashMap<ResourceLocation, AttributeModifier>	MINING_MODIFIER_MAP		= new HashMap<ResourceLocation, AttributeModifier>();
	protected static final HashMap<ResourceLocation, AttributeModifier>	ATTACK_MODIFIER_MAP		= new HashMap<ResourceLocation, AttributeModifier>();
	
	public final PowerToolFluidTank										tank					= new PowerToolFluidTank();
	
	public ExchangerModifier()
		{
		super(COLOR);
		}
		
	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getModule(Class<T> type)
		{
		if (type == IFluidModifier.class)
			{ return (T) tank; }
		return super.getModule(type);
		}
		
	@Override
	public boolean isPowered(IModifierToolStack tool, int level, boolean dirty)
		{
		FluidStack stack = tank.getFluidInTank(tool, level, 0);
		
		return stack.getAmount() >= getTicks(stack.getFluid());
		}
		
	@Override
	public void drainPower(IModifierToolStack tool, int level)
		{
		FluidStack stack = tank.getFluidInTank(tool, level, 0);
		
		stack.setAmount(getTicks(stack.getFluid()));
		tank.drain(tool, level, stack, FluidAction.EXECUTE);
		}
		
	@Override
	public AttributeModifier getMiningModifier(IModifierToolStack tool, int level)
		{
		Fluid fluid = tank.getFluidInTank(tool, level, 0).getFluid();
		ResourceLocation name = fluid.getRegistryName();
		AttributeModifier out = MINING_MODIFIER_MAP.get(name);
		if (out == null)
			{
			out = new AttributeModifier(TEnergistics.modID + ".powered_mining", ToolDefinitions.SPEED_MULTIPLIER * MeltingFuelLookup
					.findFuel(fluid)
					.getTemperature() / LAVA_TEMPERATURE - 1f, Operation.MULTIPLY_BASE);
			MINING_MODIFIER_MAP.put(name, out);
			}
		return out;
		}
		
	@Override
	public AttributeModifier getAttackModifier(IModifierToolStack tool, int level)
		{
		Fluid fluid = tank.getFluidInTank(tool, level, 0).getFluid();
		ResourceLocation name = fluid.getRegistryName();
		AttributeModifier out = ATTACK_MODIFIER_MAP.get(name);
		if (out == null)
			{
			out = new AttributeModifier(TEnergistics.modID + ".powered_attack", (ToolDefinitions.ATTACK_MULTIPLIER - 1f) * MeltingFuelLookup
					.findFuel(fluid)
					.getTemperature() / LAVA_TEMPERATURE, Operation.MULTIPLY_BASE);
			ATTACK_MODIFIER_MAP.put(name, out);
			}
		return out;
		}
		
	@Override
	public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier)
		{
		if (isEffective && isPowered(tool, level, true))
			{
			float temperature = MeltingFuelLookup.findFuel(tank.getFluidInTank(tool, level, 0).getFluid())
					.getTemperature() / LAVA_TEMPERATURE;
			
			event.setNewSpeed(event.getNewSpeed() * ToolDefinitions.SPEED_MULTIPLIER * MINING_BOOST * temperature);
			}
		}
		
	@Override
	public int getDurabilityRGB(IModifierToolStack tool, int level)
		{
		if (isPowered(tool, level, false))
			{
			switch (tank.getFluidInTank(tool, level, 0).getDisplayName().getString())
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
		FluidStack fluid = tank.getFluidInTank(tool, level, 0);
		if (!fluid.isEmpty())
			{
			int cap = tank.getTankCapacity(tool, level, 0);
			int current = fluid.getAmount();
			if (current > cap)
				{ return 0; }
			return ((double) (cap - current) / cap);
			}
		return Double.NaN;
		}
		
	@Override
	public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed)
		{
		FluidStack fluid = tank.getFluidInTank(tool, level, 0);
		tooltip.add(fluid.getDisplayName()
				.deepCopy()
				.appendString(String.format(" (%s / %s)", fluid.getAmount(), tank.getTankCapacity(tool, level, 0)))
				.modifyStyle(style -> style.setColor(Color.fromInt(getColor()))));
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		super.addVolatileData(toolDefinition, baseStats, persistentData, level, volatileData);
		volatileData.putInt(PoweredTool.FLUID_LOCATION, CAPACITY);
		ToolFluidCapability.addTanks(volatileData, tank);
		}
		
	private int getTicks(Fluid fluid)
		{
		MeltingFuel fuel = MeltingFuelLookup.findFuel(fluid);
		if (fuel == null) return Integer.MAX_VALUE;
		return Math.round(LAVATICKS_PER_OPERATION * fuel.getAmount(fluid) / ((float) fuel.getDuration()));
		}
	}