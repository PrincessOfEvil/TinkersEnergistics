package princess.tenergistics.modifiers;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import princess.tenergistics.tools.PoweredTool;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class RTGModifier extends SingleUseModifier
	{
	private static final int ENERGY_PER_SECOND = 10;
	
	public RTGModifier()
		{
		super(0xffff00);
		}
		
	@Override
	public int getPriority()
		{
		return -666;
		}
		
	@Override
	public void onInventoryTick(IModifierToolStack tool, int level, World world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack)
		{
		if (!world.isRemote && holder.ticksExisted % 20 == 0)
			{
			PoweredTool.setEnergy(tool, Math
					.min(PoweredTool.getMaxEnergy(tool), PoweredTool.getEnergy(tool) + ENERGY_PER_SECOND * level));
			}
		}
	}
