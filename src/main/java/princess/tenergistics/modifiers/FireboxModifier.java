package princess.tenergistics.modifiers;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import princess.tenergistics.tools.PoweredTool;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class FireboxModifier extends PowerSourceModifier
	{
	public static final int		CAPACITY			= 16;
	private static final int	COLOR				= 0xffff00;
	
	private static final int	TICKDOWN_PER_SECOND	= 4;
	private static final int	FIRETICKS_PER_FUEL	= 3;
	
	public FireboxModifier()
		{
		super(0xd46428);
		}
		
	@Override
	public boolean isPowered(IModifierToolStack tool, boolean dirty)
		{
		ModDataNBT data = tool.getPersistentData();
		if (data.getInt(PoweredTool.TICKER) > 0) return true;
		
		ItemStack fuel = PoweredTool.getItemStack(tool);
		int time = ForgeHooks.getBurnTime(fuel);
		if (time == 0) return false;
		
		if (dirty)
			{
			time *= FIRETICKS_PER_FUEL;
			fuel.split(1);
			
			PoweredTool.setItemStack(tool, fuel);
			data.putInt(PoweredTool.TICKER, time);
			data.putInt(PoweredTool.TICKER_MAX, time);
			data.putInt(PoweredTool.TICKER_LEFTOVER, -1);
			}
			
		return true;
		}
		
	@Override
	public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData)
		{
		super.addVolatileData(toolDefinition, baseStats, persistentData, level, volatileData);
		volatileData.putInt(PoweredTool.ITEM_LOCATION, CAPACITY);
		}
		
	@Override
	public void onInventoryTick(IModifierToolStack tool, int level, World world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack)
		{
		ModDataNBT data = tool.getPersistentData();
		if (data.getInt(PoweredTool.TICKER) > 0 && !world.isRemote)
			{
			if (holder.ticksExisted % (20 / TICKDOWN_PER_SECOND) == data
					.getInt(PoweredTool.TICKER_LEFTOVER) /* && holder.getActiveItemStack() != stack*/)
				{
				data.putInt(PoweredTool.TICKER, (int) Math
						.max(data.getInt(PoweredTool.TICKER) - TICKDOWN_PER_SECOND * Math
								.round(Math.ceil(tool.getStats().getMiningSpeed() * (tool.getVolatileData().getFloat(OverclockModifier.OVERCLOCK) + 1))), 0));
				}
				
			if (data.getInt(PoweredTool.TICKER_LEFTOVER) == -1) data
					.putInt(PoweredTool.TICKER_LEFTOVER, holder.ticksExisted % (20 / TICKDOWN_PER_SECOND));
			}
		}
		
	@Override
	public double getDamagePercentage(IModifierToolStack tool, int level)
		{
		ModDataNBT data = tool.getPersistentData();
		if (isPowered(tool, false) && data.getInt(PoweredTool.TICKER) > 0)
			{ return 1f - data.getInt(PoweredTool.TICKER) / (double) data.getInt(PoweredTool.TICKER_MAX); }
		if (hasFuel(tool))
			{
			ItemStack fuel = PoweredTool.getItemStack(tool);
			return 1f - fuel.getCount() / (double) maxFuel(tool, fuel);
			}
		return Double.NaN;
		}
		
	@Override
	public ITextComponent getDisplayName(IModifierToolStack tool, int level)
		{
		ItemStack fuel = PoweredTool.getItemStack(tool);
		if (fuel.isEmpty()) return getDisplayName().deepCopy().appendString(" (EMPTY)");
		return getDisplayName().deepCopy()
				.appendString(": ")
				.append(fuel.getDisplayName())
				.appendString(String.format(" (%s / %s)", fuel.getCount(), maxFuel(tool, fuel)));
		}
		
	@Override
	public Boolean showDurabilityBar(IModifierToolStack tool, int level)
		{
		return isPowered(tool, false) || hasFuel(tool) ? true : null;
		}
		
	@Override
	public int getDurabilityRGB(IModifierToolStack tool, int level)
		{
		if (isPowered(tool, false) && tool.getPersistentData().getInt(PoweredTool.TICKER) > 0) return COLOR;
		if (hasFuel(tool)) return getColor();
		return -1;
		}
		
	private boolean hasFuel(IModifierToolStack tool)
		{
		return ForgeHooks.getBurnTime(PoweredTool.getItemStack(tool)) != 0;
		}
		
	private int maxFuel(IModifierToolStack tool, ItemStack stack)
		{
		return Math.min(PoweredTool.getItemCapacity(tool), stack.getMaxStackSize());
		}
	}
