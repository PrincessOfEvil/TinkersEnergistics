package princess.tinkersenergistics.compat;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import princess.tinkersenergistics.library.CrusherRecipe;

public class CrusherRecipeWrapper implements IRecipeWrapper
	{
	private final List<ItemStack>	input;
	private final List<ItemStack>	output;
	
	public CrusherRecipeWrapper(CrusherRecipe recipe)
		{
		this.input = recipe.input.getInputs();
		this.output = ImmutableList.of(recipe.output);
		}
		
	@Override
	public void getIngredients(IIngredients ingredients)
		{
		ingredients.setInputs(ItemStack.class, input);
		ingredients.setOutputs(ItemStack.class, output);
		}
		
	public List<List<ItemStack>> getInputs()
		{
		return Collections.singletonList(input);
		}
	}
