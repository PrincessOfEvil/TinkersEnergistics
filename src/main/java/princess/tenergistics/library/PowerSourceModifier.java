package princess.tenergistics.library;

import java.util.function.BiConsumer;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.tools.PoweredTool;
import princess.tenergistics.tools.ToolDefinitions;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class PowerSourceModifier extends SingleUseModifier
	{
	//istg, why even is vanilla adding +1 to it?	
	protected static final AttributeModifier	MINING_MODIFIER	= new AttributeModifier(TEnergistics.modID + ".powered_mining", ToolDefinitions.SPEED_MULTIPLIER - 1f, Operation.MULTIPLY_BASE);
	protected static final AttributeModifier	ATTACK_MODIFIER	= new AttributeModifier(TEnergistics.modID + ".powered_attack", ToolDefinitions.ATTACK_MULTIPLIER - 1f, Operation.MULTIPLY_BASE);
	
	protected static final float				MINING_BOOST	= 1;
	
	protected static final String				KEY_INVALID		= "tenergistics.tool.already_powered";
	
	public PowerSourceModifier(int color)
		{
		super(color);
		}
		
	/**
	* @param dirty   If true, allows to make changes to the tool.
	 * */
	public boolean isPowered(IModifierToolStack tool, int level, boolean dirty)
		{
		return true;
		}
		
	public void drainPower(IModifierToolStack tool, int level)
		{}
		
	@Override
	public int getPriority()
		{
		return 666;
		}
		
	@Override
	public double getDamagePercentage(IModifierToolStack tool, int level)
		{
		return Double.NaN;
		}
		
	@Override
	public Boolean showDurabilityBar(IModifierToolStack tool, int level)
		{
		return isPowered(tool, level, false) ? true : null;
		}
		
	@Override
	public int getDurabilityRGB(IModifierToolStack tool, int level)
		{
		if (isPowered(tool, level, false))
			{ return getColor(); }
		return -1;
		}
		
	@Override
	public int onDamageTool(IModifierToolStack tool, int level, int amount)
		{
		int left = amount;
		for (; isPowered(tool, level, true) && left > 0; left--)
			drainPower(tool, level);
		return left;
		}
		
	@Override
	public void addAttributes(IModifierToolStack tool, int level, BiConsumer<Attribute, AttributeModifier> consumer)
		{
		if (isPowered(tool, level, false))
			{
			consumer.accept(TEnergistics.FAKE_HARVEST_SPEED.get(), getMiningModifier(tool, level));
			consumer.accept(Attributes.ATTACK_DAMAGE, getAttackModifier(tool, level));
			consumer.accept(Attributes.ATTACK_SPEED, getAttackModifier(tool, level));
			}
		}
		
	@Override
	public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) 
		{
		if (isEffective && isPowered(tool, level, true))
			{
			event.setNewSpeed(event.getNewSpeed() * ToolDefinitions.SPEED_MULTIPLIER * getMiningBoost(tool, level));
			}
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		volatileData.putInt(PoweredTool.POWERED, volatileData.getInt(PoweredTool.POWERED) + 1);
		volatileData.putBoolean(ToolCore.INDESTRUCTIBLE_ENTITY, true);
		}
		
	@Override
	public ValidatedResult validate(ToolStack tool, int level)
		{
		return tool.getVolatileData().getInt(PoweredTool.POWERED) > 1 ? ValidatedResult.failure(KEY_INVALID)
				: ValidatedResult.PASS;
		}
		
	public AttributeModifier getMiningModifier(IModifierToolStack tool, int level)
		{
		return MINING_MODIFIER;
		}
		
	public AttributeModifier getAttackModifier(IModifierToolStack tool, int level)
		{
		return ATTACK_MODIFIER;
		}
		
	public float getMiningBoost(IModifierToolStack tool, int level)
		{
		return MINING_BOOST;
		}
	}