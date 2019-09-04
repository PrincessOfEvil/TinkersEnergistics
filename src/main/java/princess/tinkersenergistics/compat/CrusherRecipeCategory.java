package princess.tinkersenergistics.compat;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import princess.tinkersenergistics.library.ModInfo;
import slimeknights.tconstruct.library.Util;


public class CrusherRecipeCategory implements IRecipeCategory<CrusherRecipeWrapper>
	{
	public static String		CATEGORY		= Util.prefix("crusher");
	
	private ResourceLocation	background_loc	= new ResourceLocation(ModInfo.MODID, "textures/gui/crusherjei.png");
	private IDrawableAnimated	arrow;
	private final IDrawable		background;
	
	public CrusherRecipeCategory(IGuiHelper guiHelper)
		{
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(background_loc, 160, 0, 18, 14);
		arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
		background = guiHelper.createDrawable(background_loc, 0, 0, 160, 60, 0, 0, 0, 0);
		}
		
	@Override
	public String getUid()
		{
		return CATEGORY;
		}
		
	@Override
	public String getTitle()
		{
		return Util.translate("tile.tenergistics.machine_crusher.name");
		}
		
	@Override
	public IDrawable getBackground()
		{
		return background;
		}
		
	@Override
	public IDrawable getIcon()
		{
		return null;
		}
		
	@Override
	public void drawExtras(Minecraft minecraft)
		{
		arrow.draw(minecraft, 70, 20);
		}
		
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CrusherRecipeWrapper recipeWrapper, IIngredients ingredients)
		{
		IGuiItemStackGroup items = recipeLayout.getItemStacks();
		
		items.init(0, true, 43, 17);
		items.set(ingredients);
		
		items.init(1, false, 97, 17);
		items.set(ingredients);
		}
		
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
		{
		return Lists.newLinkedList();
		}

	@Override
	public String getModName()
		{
		return ModInfo.MODID;
		}
		
	}
