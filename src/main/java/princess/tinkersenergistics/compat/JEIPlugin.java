package princess.tinkersenergistics.compat;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import princess.tinkersenergistics.TinkersEnergistics;
import princess.tinkersenergistics.library.MachineRecipeHandler;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin
	{
	public IJeiHelpers jeiHelpers;
	
	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry)
		{}
		
	@Override
	public void registerIngredients(IModIngredientRegistration registry)
		{}
		
	@Override
	public void register(IModRegistry registry)
		{
		jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
		registry.addRecipeCategories(new CrusherRecipeCategory(guiHelper));		
		registry.addRecipeHandlers(new CrusherRecipeHandler());
		registry.addRecipes(MachineRecipeHandler.getCrusherOreList());
		registry.addRecipeCategoryCraftingItem(TinkersEnergistics.crusher.buildItemForRenderingInGui(), CrusherRecipeCategory.CATEGORY);
		}
		
	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
		{}
	}