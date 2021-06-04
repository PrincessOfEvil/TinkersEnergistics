package princess.tenergistics.library;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class PoweredToolModifier extends Modifier
	{
	
	public PoweredToolModifier()
		{
		super(0x21dffc);
		}
		
	@Override
	public int getPriority()
		{
		// Classic builtin modifier value
		return Integer.MAX_VALUE - 50;
		}
		
	@Override
	public boolean shouldDisplay(boolean advanced)
		{
		return false;
		}
		
	@Override
	public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed)
		{
		tooltip.add(TEnergistics.EFFICIENCY.formatValue(tool.getStats().getFloat(TEnergistics.EFFICIENCY)));
		tooltip.add(TEnergistics.MACHINE_SPEED.formatValue(tool.getStats().getFloat(TEnergistics.MACHINE_SPEED)));
		}
		
	@Override
	public int onDamageTool(IModifierToolStack tool, int level, int amount)
		{
		return PoweredToolModifier.modifyDamageFor(tool, amount);
		}
		
	public static int modifyDamageFor(IModifierToolStack tool, int amount)
		{
		float ineff = 1 / tool.getStats().getFloat(TEnergistics.EFFICIENCY);
		int ret = (int) (amount * Math.floor(ineff));
		
		if ((ineff % 1) > RANDOM.nextDouble())
			{
			ret += amount;
			}
			
		return ret;
		}
	}
