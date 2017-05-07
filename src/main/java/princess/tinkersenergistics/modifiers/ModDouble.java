package princess.tinkersenergistics.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import princess.tinkersenergistics.TinkersEnergistics;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.modifiers.ToolModifier;

public class ModDouble extends ToolModifier implements IMachineMod
	{
	private final int max;
	
	public ModDouble(int max)
		{
		super("double", 0x488070);
		
		this.max = max;
		
		addAspects(new ModifierAspect.MultiAspect(this, 5, max, 1));
		}
		
	@Override
	public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag)
		{}
		
	@Override
	protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException
		{
		ToolCore item = ((ToolCore) stack.getItem());
		return item.hasCategory(TinkersEnergistics.TIE_MACHINE) && item.hasCategory(TinkersEnergistics.TIE_CRUSHER);
		}
		
	@Override
	public String getTooltip(NBTTagCompound modifierTag, boolean detailed)
		{
		return getLeveledTooltip(modifierTag, detailed);
		}
	}