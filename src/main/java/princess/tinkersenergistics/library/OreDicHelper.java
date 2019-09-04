package princess.tinkersenergistics.library;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDicHelper
	{
	
	public static boolean isOre(String name, ItemStack stack)
		{
		return findStringInArray(name, getOreNamesFromStack(stack));
		}
		
	public static String[] getOreNamesFromStack(ItemStack stack)
		{
		int[] ids = OreDictionary.getOreIDs(stack);
		if (ids.length == 0) return null;
		String[] names = new String[ids.length];
		for (int i = 0; i < ids.length; i++)
			{
			names[i] = OreDictionary.getOreName(ids[i]);
			}
		return names;
		}
		
	public static boolean findStringInArray(String name, String[] array)
		{
		if (array == null) return false;
		for (int i = 0; i < array.length; i++)
			{
			if (array[i].contains(name)) return true;
			}
		return false;
		}
	}