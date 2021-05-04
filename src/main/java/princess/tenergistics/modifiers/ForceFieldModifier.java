package princess.tenergistics.modifiers;

import java.util.HashMap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.tools.PoweredTool;
import princess.tenergistics.tools.ToolDefinitions;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class ForceFieldModifier extends SingleUseModifier
	{
	public static final int		INVERSE_FORCE_CAPACITY_MULTIPLIER	= 4;
	
	public static final float	FORCE_SPEED_MULTIPLIER				= 0.85f;
	//sqrt(.85f)
	public static final float	FORCE_ATTACK_MULTIPLIER				= 0.92f;
	
	public ForceFieldModifier()
		{
		super(0x0eee0e);
		}
		
	@Override
	public int getPriority()
		{
		return -13666;
		}
		
	@Override
	public ITextComponent getDisplayName(IModifierToolStack tool, int level)
		{
		if (tool.getVolatileData().getInt(PoweredTool.POWERED) == 0)
			{
			return getDisplayName().deepCopy()
					.appendString(" (")
					.append(new TranslationTextComponent(PoweredTool.UNPOWERED)
							.mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_RED, TextFormatting.ITALIC))
					.appendString(")");
			}
		return super.getDisplayName(tool, level);
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		super.addVolatileData(toolDefinition, baseStats, persistentData, level, volatileData);
		if (volatileData.getInt(PoweredTool.ITEM_LOCATION) > 0) volatileData
				.putInt(PoweredTool.ITEM_LOCATION, volatileData
						.getInt(PoweredTool.ITEM_LOCATION) / INVERSE_FORCE_CAPACITY_MULTIPLIER);
		
		if (volatileData.getInt(PoweredTool.FLUID_LOCATION) > 0) volatileData
				.putInt(PoweredTool.FLUID_LOCATION, volatileData
						.getInt(PoweredTool.FLUID_LOCATION) / INVERSE_FORCE_CAPACITY_MULTIPLIER);
		
		if (volatileData.getInt(PoweredTool.ENERGY_LOCATION) > 0) volatileData
				.putInt(PoweredTool.ENERGY_LOCATION, volatileData
						.getInt(PoweredTool.ENERGY_LOCATION) / INVERSE_FORCE_CAPACITY_MULTIPLIER);
		}
		
	public static final AttributeModifier	MINING_MODIFIER	= new AttributeModifier(TEnergistics.modID + ".powered_mining", ForceFieldModifier.FORCE_SPEED_MULTIPLIER - 1f, Operation.MULTIPLY_BASE);
	public static final AttributeModifier	ATTACK_MODIFIER	= new AttributeModifier(TEnergistics.modID + ".powered_attack", ForceFieldModifier.FORCE_ATTACK_MULTIPLIER - 1f, Operation.MULTIPLY_BASE);
	
	public static final float				MINING_BOOST	= ForceFieldModifier.FORCE_SPEED_MULTIPLIER / ToolDefinitions.SPEED_MULTIPLIER;
	
	public static class ForceFireboxModifier extends FireboxModifier
		{
		@Override
		public AttributeModifier getMiningModifier(IModifierToolStack tool, int level)
			{
			return ForceFieldModifier.MINING_MODIFIER;
			}
			
		@Override
		public AttributeModifier getAttackModifier(IModifierToolStack tool, int level)
			{
			return ForceFieldModifier.ATTACK_MODIFIER;
			}
			
		@Override
		public float getMiningBoost(IModifierToolStack tool, int level)
			{
			return ForceFieldModifier.MINING_BOOST;
			}
		}
		
	public static class ForceExchangerModifier extends ExchangerModifier
		{
		protected static final HashMap<ResourceLocation, AttributeModifier>	FORCE_MINING_MODIFIER_MAP	= new HashMap<ResourceLocation, AttributeModifier>();
		protected static final HashMap<ResourceLocation, AttributeModifier>	FORCE_ATTACK_MODIFIER_MAP	= new HashMap<ResourceLocation, AttributeModifier>();
		
		@Override
		public AttributeModifier getMiningModifier(IModifierToolStack tool, int level)
			{
			Fluid fluid = tank.getFluidInTank(tool, level, 0).getFluid();
			ResourceLocation name = fluid.getRegistryName();
			AttributeModifier out = FORCE_MINING_MODIFIER_MAP.get(name);
			if (out == null)
				{
				out = new AttributeModifier(TEnergistics.modID + ".powered_mining", ForceFieldModifier.FORCE_SPEED_MULTIPLIER * (MeltingFuelLookup
						.findFuel(fluid)
						.getTemperature() / LAVA_TEMPERATURE) - 1f, Operation.MULTIPLY_BASE);
				FORCE_MINING_MODIFIER_MAP.put(name, out);
				}
			return out;
			}
			
		@Override
		public AttributeModifier getAttackModifier(IModifierToolStack tool, int level)
			{
			Fluid fluid = tank.getFluidInTank(tool, level, 0).getFluid();
			ResourceLocation name = fluid.getRegistryName();
			AttributeModifier out = FORCE_ATTACK_MODIFIER_MAP.get(name);
			if (out == null)
				{
				out = new AttributeModifier(TEnergistics.modID + ".powered_attack", ForceFieldModifier.FORCE_ATTACK_MULTIPLIER + (MeltingFuelLookup
						.findFuel(fluid)
						.getTemperature() / LAVA_TEMPERATURE) - 2f, Operation.MULTIPLY_BASE);
				FORCE_ATTACK_MODIFIER_MAP.put(name, out);
				}
			return out;
			}
			
		@Override
		public float getMiningBoost(IModifierToolStack tool, int level)
			{
			return ForceFieldModifier.MINING_BOOST * (MeltingFuelLookup
					.findFuel(tank.getFluidInTank(tool, level, 0).getFluid())
					.getTemperature() / LAVA_TEMPERATURE);
			}
		}
		
	public static class ForceEnergyCoilModifier extends EnergyCoilModifier
		{
		@Override
		public AttributeModifier getMiningModifier(IModifierToolStack tool, int level)
			{
			return ForceFieldModifier.MINING_MODIFIER;
			}
			
		@Override
		public AttributeModifier getAttackModifier(IModifierToolStack tool, int level)
			{
			return ForceFieldModifier.ATTACK_MODIFIER;
			}
			
		@Override
		public float getMiningBoost(IModifierToolStack tool, int level)
			{
			return ForceFieldModifier.MINING_BOOST;
			}
		}
	}
