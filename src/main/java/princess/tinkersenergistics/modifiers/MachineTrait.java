package princess.tinkersenergistics.modifiers;

import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.tools.modifiers.ModCreative;
import slimeknights.tconstruct.tools.traits.TraitWritable;

public class MachineTrait extends AbstractTrait implements IMachineMod
	{
	public static MachineTrait INSTANCE = new MachineTrait();
	
	public MachineTrait()
		{
		super("machinetrait", 0x4000ff);
		}
		
	@Override
	public boolean canApplyTogether(IToolMod modifier)
		{
		return (modifier instanceof IMachineMod) || (modifier instanceof TraitWritable) || (modifier instanceof ModCreative);
		}
	}