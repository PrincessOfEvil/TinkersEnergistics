package princess.tinkersenergistics.compat;

import java.util.ArrayList;
import java.util.List;

import princess.tinkersenergistics.library.CrusherRecipe;
import princess.tinkersenergistics.library.MachineRecipeHandler;

public class CrusherRecipeChecker
	{
	public static List<CrusherRecipe> getCrushingRecipes()
		{
		List<CrusherRecipe> recipes = new ArrayList<>();
		
		for (CrusherRecipe recipe : MachineRecipeHandler.getCrusherOreList())
			{
			if (recipe.input != null && !recipe.output.isEmpty())
				{
				recipes.add(recipe);
				}
			}
			
		return recipes;
		}
	}