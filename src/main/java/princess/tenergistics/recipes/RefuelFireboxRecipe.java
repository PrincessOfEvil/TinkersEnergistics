package princess.tenergistics.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.tools.PoweredTool;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class RefuelFireboxRecipe implements ITinkerStationRecipe
	{
	private static final ValidatedResult	FULLY_FUELED	= ValidatedResult
			.failure(Util.makeTranslationKey("recipe", "tool_repair.fully_fueled"));
	private static final ValidatedResult	FUEL_LEFT		= ValidatedResult
			.failure(Util.makeTranslationKey("recipe", "tool_repair.fuel_left"));
	private final ResourceLocation			id;
	
	public RefuelFireboxRecipe(ResourceLocation id)
		{
		this.id = id;
		}
		
	@Override
	public void updateInputs(ItemStack result, IMutableTinkerStationInventory inv)
		{
		ItemStack fuelIn = PoweredTool.getItemStack(inv.getTinkerableStack());
		ItemStack fuelOut = PoweredTool.getItemStack(result);
		
		int fuelNeeded = fuelOut.getCount() - fuelIn.getCount();
		
		for (int i = 0; i < inv.getInputCount() && fuelNeeded > 0; i++)
			{
			ItemStack stack = inv.getInput(i);
			if (stack.isEmpty())
				{
				continue;
				}
				
			int fuelToConsume = Math.min(fuelNeeded, stack.getCount());
			inv.shrinkInput(i, fuelToConsume);
			fuelNeeded -= fuelToConsume;
			}
		}
		
	@Override
	public ValidatedResult getValidatedResult(ITinkerStationInventory inv)
		{
		ToolStack tool = ToolStack.from(inv.getTinkerableStack());
		if (tool.getDefinition() == ToolDefinition.EMPTY) return ValidatedResult.PASS;
		ItemStack fuelIn = PoweredTool.getItemStack(tool);
		
		int fuelNeeded = Math.min(fuelIn.getMaxStackSize(), tool.getVolatileData()
				.getInt(PoweredTool.ITEM_LOCATION)) - fuelIn.getCount();
		
		ItemStack out = ItemStack.EMPTY;
		
		for (int i = 0; i < inv.getInputCount() && fuelNeeded > 0; i++)
			{
			ItemStack stack = inv.getInput(i);
			if (stack.isEmpty())
				{
				continue;
				}
				
			if (!fuelIn.isEmpty() && (!ItemStack.areItemsEqual(stack, fuelIn)
					|| !ItemStack.areItemStackTagsEqual(stack, fuelIn))) return FUEL_LEFT;
			
			if (out.isEmpty())
				{
				out = stack.copy();
				out.setCount(Math.min(fuelNeeded, out.getCount()));
				}
			else
				out.setCount(Math.min(fuelNeeded, out.getCount() + stack.getCount()));
			}
			
		if (fuelNeeded == 0)
			{ return FULLY_FUELED; }
			
		if (out.getCount() > 0)
			{
			tool = tool.copy();
			
			out.setCount(out.getCount() + fuelIn.getCount());
			PoweredTool.setItemStack(tool, out);
			
			return ValidatedResult.success(tool.createStack());
			}
			
		return ValidatedResult.PASS;
		}
		
	@Override
	public boolean matches(ITinkerStationInventory inv, World world)
		{
		ItemStack tinkerable = inv.getTinkerableStack();
		if (tinkerable.isEmpty()) return false;
		
		if (ToolStack.from(tinkerable).getModifiers().getLevel(TEnergistics.fireboxModifier.get()) == 0) return false;
		
		ItemStack out = ItemStack.EMPTY;
		for (int i = 0; i < inv.getInputCount(); i++)
			{
			ItemStack stack = inv.getInput(i);
			if (stack.isEmpty())
				{
				continue;
				}
			if (out.isEmpty()) out = stack.copy();
			if (!ItemStack.areItemsEqual(stack, out) || !ItemStack.areItemStackTagsEqual(stack, out)) return false;
			if (ForgeHooks.getBurnTime(stack) == 0) return false;
			}
		if (out.isEmpty()) return false;
		
		return true;
		}
		
	@Override
	public IRecipeSerializer<?> getSerializer()
		{
		return TEnergistics.tinkerStationFireboxRefuelSerializer.get();
		}
		
	@Override
	public ResourceLocation getId()
		{
		return id;
		}
		
	/** @deprecated Use {@link #getCraftingResult(ITinkerStationInventory)} */
	@Deprecated
	@Override
	public ItemStack getRecipeOutput()
		{
		return ItemStack.EMPTY;
		}
	}
