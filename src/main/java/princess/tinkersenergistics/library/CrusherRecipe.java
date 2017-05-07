package princess.tinkersenergistics.library;

import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

public class CrusherRecipe
	{
	
	public final RecipeMatch	input;
	public final ItemStack		output;
	
	public CrusherRecipe(RecipeMatch input, ItemStack output)
		{
		this.input = input;
		this.output = output;
		}
		
	public boolean matches(ItemStack input)
		{
		return this.input != null && this.input.matches(new ItemStack[] { input }) != null;
		
		}
		
	public ItemStack getResult()
		{
		return output.copy();
		}
	}