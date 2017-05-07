package princess.tinkersenergistics.compat;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import princess.tinkersenergistics.library.CrusherRecipe;

public class CrusherRecipeHandler implements IRecipeHandler<CrusherRecipe>
	{
	
	@Override
	public Class<CrusherRecipe> getRecipeClass()
		{
		return CrusherRecipe.class;
		}
		
	@Override
	public String getRecipeCategoryUid()
		{
		return CrusherRecipeCategory.CATEGORY;
		}
		
	@Override
	public String getRecipeCategoryUid(CrusherRecipe recipe)
		{
		return CrusherRecipeCategory.CATEGORY;
		}
		
	@Override
	public IRecipeWrapper getRecipeWrapper(CrusherRecipe recipe)
		{
		return new CrusherRecipeWrapper(recipe);
		}
		
	@Override
	public boolean isRecipeValid(CrusherRecipe recipe)
		{
		return recipe.output != null && recipe.input != null && recipe.input.getInputs() != null && recipe.input.getInputs().size() > 0;
		}
		
	}