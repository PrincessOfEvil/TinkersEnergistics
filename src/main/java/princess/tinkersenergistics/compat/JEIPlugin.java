package princess.tinkersenergistics.compat;

import java.util.Arrays;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import princess.tinkersenergistics.TinkersEnergistics;
import princess.tinkersenergistics.library.CrusherRecipe;
import slimeknights.tconstruct.tools.TinkerMaterials;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin
	{
	public IJeiHelpers					jeiHelpers;
	public static CrusherRecipeCategory	crushingCategory;
	
	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry)
		{}
		
	@Override
	public void registerIngredients(IModIngredientRegistration registry)
		{}
		
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
		{
		final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
		crushingCategory = new CrusherRecipeCategory(guiHelper);
		
		registry.addRecipeCategories(crushingCategory);
		}
		
	@Override
	public void register(IModRegistry registry)
		{
		jeiHelpers = registry.getJeiHelpers();
		
		registry.handleRecipes(CrusherRecipe.class, new CrusherRecipeHandler(), CrusherRecipeCategory.CATEGORY);
		registry.addRecipeCatalyst(TinkersEnergistics.crusher.buildItem(Arrays.asList(TinkerMaterials.paper, TinkerMaterials.paper, TinkerMaterials.paper)), CrusherRecipeCategory.CATEGORY);
		registry.addRecipes(CrusherRecipeChecker.getCrushingRecipes(), CrusherRecipeCategory.CATEGORY);
		
		registry.addRecipeCatalyst(TinkersEnergistics.furnace.buildItem(Arrays.asList(TinkerMaterials.paper, TinkerMaterials.paper, TinkerMaterials.paper)), VanillaRecipeCategoryUid.SMELTING);

		registry.addRecipeCatalyst(TinkersEnergistics.furnace.buildItem(Arrays.asList(TinkerMaterials.paper, TinkerMaterials.paper, TinkerMaterials.paper)), VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCatalyst(TinkersEnergistics.crusher.buildItem(Arrays.asList(TinkerMaterials.paper, TinkerMaterials.paper, TinkerMaterials.paper)), VanillaRecipeCategoryUid.FUEL);

		}
		
	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
		{}
		
	public class CrusherRecipeHandler implements IRecipeWrapperFactory<CrusherRecipe>
		{
		@Nonnull
		@Override
		public IRecipeWrapper getRecipeWrapper(@Nonnull CrusherRecipe recipe)
			{
			return new CrusherRecipeWrapper(recipe);
			}
		}
	}
