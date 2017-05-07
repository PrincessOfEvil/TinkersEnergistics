package princess.tinkersenergistics.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import princess.tinkersenergistics.TinkersEnergistics;
import princess.tinkersenergistics.library.MachineNBT;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.modifiers.ToolModifier;

public class ModOverclock extends ToolModifier implements IMachineMod
	{
	private final int max;
	
	public ModOverclock(int max)
		{
		super("overclock", 0x910000);
		
		this.max = max;
		
		addAspects(new ModifierAspect.MultiAspect(this, 5, max, 1));
		}
		
	@Override
	public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag)
		{
		ModifierNBT.IntegerNBT modData = ModifierNBT.readInteger(modifierTag);
		
		MachineNBT data = new MachineNBT(TagUtil.getToolTag(rootCompound));
		
		int level = modData.current / max;
		
		applySpeedBoost(modData, data, level);
		applyFuelBoost(modData, data, level);
		
		TagUtil.setToolTag(rootCompound, data.get());
		}
		
	protected void applySpeedBoost(ModifierNBT.IntegerNBT modData, MachineNBT data, int level)
		{
		data.speedMultiplier = (float) (data.speedMultiplier * Math.pow(1.001, modData.current));
		}
		
	protected void applyFuelBoost(ModifierNBT.IntegerNBT modData, MachineNBT data, int level)
		{
		data.fuelMultiplier = (float) (data.fuelMultiplier * Math.pow(1.001, modData.current));
		}
		
	@Override
	protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException
		{
		return ((ToolCore) stack.getItem()).hasCategory(TinkersEnergistics.TIE_MACHINE);
		}
		
	@Override
	public String getTooltip(NBTTagCompound modifierTag, boolean detailed)
		{
		return getLeveledTooltip(modifierTag, detailed);
		}
	}