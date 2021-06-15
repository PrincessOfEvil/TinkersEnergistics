package princess.tenergistics.book;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.ArrowButton;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ImageElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.book.elements.CycleRecipeElement;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IDisplayModifierRecipe;
import slimeknights.tconstruct.tools.modifiers.EmptyModifier;

//TODO: remove the reflection access when KM wakes up
@OnlyIn(Dist.CLIENT)
public class EnergisticsContentModifier extends ContentModifier
	{
	public static final transient String			ID						= "energistics_modifier";
	
	private transient Modifier						modifier;
	private transient List<IDisplayModifierRecipe>	recipes;
	
	private transient Field							currentRecipeField;
	private transient static final String			currentRecipeFieldName	= "currentRecipe";
	
	@SuppressWarnings("resource")
	@Override
	public void load()
		{
		super.load();
		
		if (this.modifier == null)
			{
			this.modifier = TinkerRegistries.MODIFIERS.getValue(new ModifierId(this.modifierID));
			}
			
		if (this.recipes == null)
			{
			assert Minecraft.getInstance().world != null;
			this.recipes = RecipeHelper
					.getJEIRecipes(Minecraft.getInstance().world
							.getRecipeManager(), RecipeTypes.TINKER_STATION, IDisplayModifierRecipe.class)
					.stream()
					.filter(recipe -> recipe.getDisplayResult().getModifier() == this.modifier)
					.collect(Collectors.toList());
			}
			
		try
			{
			@SuppressWarnings("unused")
			Field[] test = ContentModifier.class.getDeclaredFields();
			currentRecipeField = ContentModifier.class.getDeclaredField(currentRecipeFieldName);
			currentRecipeField.setAccessible(true);
			}
		catch (NoSuchFieldException e)
			{
			e.printStackTrace();
			}
		catch (SecurityException e)
			{
			e.printStackTrace();
			}
		}
		
	@Override
	public void build(BookData book, ArrayList<BookElement> list, boolean brightSide)
		{
		if (this.modifier == null || this.modifier instanceof EmptyModifier || this.recipes.isEmpty())
			{
			list.add(new ImageElement(0, 0, 32, 32, ImageData.MISSING));
			System.out.println("Modifier with id " + modifierID + " not found");
			return;
			}
		this.addTitle(list, this.modifier.getDisplayName().getString(), true, this.modifier.getColor());
		
		// description
		int h = (int) (BookScreen.PAGE_WIDTH / 2.5 - 5);
		list.add(new TextElement(10, 20, BookScreen.PAGE_WIDTH - 20, h, text));
		
		if (this.effects.length > 0)
			{
			TextData head = new TextData(this.parent.translate("modifier.effect"));
			head.underlined = true;
			
			list.add(new TextElement(10, 20 + h, BookScreen.PAGE_WIDTH / 2 - 5, BookScreen.PAGE_HEIGHT - h - 20, head));
			
			List<TextData> effectData = Lists.newArrayList();
			
			for (String e : this.effects)
				{
				effectData.add(new TextData("\u25CF "));
				effectData.add(new TextData(e));
				effectData.add(new TextData("\n"));
				}
				
			list.add(new TextElement(10, 30 + h, BookScreen.PAGE_WIDTH / 2 + 5, BookScreen.PAGE_HEIGHT - h - 20, effectData));
			}
			
		if (recipes.size() > 1)
			{
			int col = book.appearance.structureButtonColor;
			int colHover = book.appearance.structureButtonColorHovered;
			list.add(new CycleRecipeElement(BookScreen.PAGE_WIDTH - ArrowButton.ArrowType.RIGHT.w - 32, 160, ArrowButton.ArrowType.RIGHT, col, colHover, this, book, list));
			}
			
		this.buildAndAddRecipeDisplay(book, list, this.recipes.get(getCurrentRecipe()), null);
		}
		
	private int getCurrentRecipe()
		{
		int ret = 0;
		try
			{
			if (currentRecipeField != null)
				{
				ret = (int) currentRecipeField.get((ContentModifier) this);
				}
			}
		catch (IllegalArgumentException e)
			{
			e.printStackTrace();
			}
		catch (IllegalAccessException e)
			{
			e.printStackTrace();
			}
			
		return ret;
		}
	}
