package princess.tinkersenergistics.library;

import java.util.List;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

public class MachineRecipeHandler
	{
	private static List<CrusherRecipe> crusherRegistry = Lists.newLinkedList();
	
	/***Adds a crushing recipe*/
	public static void addCrushingRecipe(CrusherRecipe recipe)
		{
		crusherRegistry.add(recipe);
		}
		
	/***Adds a crushing recipe, itemstack version*/
	public static void addCrushingRecipe(ItemStack input, ItemStack output)
		{
		crusherRegistry.add(new CrusherRecipe(new RecipeMatch.Item(input, 1), output));
		}
		
	/***Returns the crushing result*/
	@Nullable
	public static ItemStack getCrushingResult(ItemStack input)
		{
		for (CrusherRecipe r : crusherRegistry)
			{
			if (r.matches(input)) return r.getResult();
			}
		return null;
		}
	
	public static List<CrusherRecipe> getCrusherOreList()
		{
		return ImmutableList.copyOf(crusherRegistry);
		}
	}
