package princess.tinkersenergistics.item;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;
import princess.tinkersenergistics.ConfigHandler;
import princess.tinkersenergistics.TinkersEnergistics;
import princess.tinkersenergistics.library.MachineRecipeHandler;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.shared.item.ItemMetaDynamicTinkers;

public class ItemDust extends ItemMetaDynamicTinkers implements IMaterialItem
	{
	public TIntObjectHashMap<String> getNames()
		{
		return names;
		}
		
	@Nonnull
	@Override
	public String getItemStackDisplayName(@Nonnull ItemStack stack)
		{
		Material material = getMaterial(stack);
		if (material != Material.UNKNOWN)
			{
			String locString = getUnlocalizedName() + "." + material.getIdentifier();
			
			// custom name?
			if (I18n.canTranslate(locString)) { return Util.translate(locString); }
			
			// no, create the default name combo
			return material.getLocalizedItemName(Util.translate(getUnlocalizedName() + ".name"));
			}
		else
			return Util.translate("item.tenergistics.dusts.warp.name");
		}
		
	@Override
	public String getMaterialID(ItemStack stack)
		{
		return getMaterial(stack).identifier;
		}
		
	@Override
	public Material getMaterial(ItemStack stack)
		{
		return TinkerRegistry.getMaterial(names.get(stack.getMetadata()));
		}
		
	@Override
	public ItemStack getItemstackWithMaterial(Material material)
		{
		ItemStack stack = new ItemStack(this);
		for (int i = 0; i < 64; i++)
			{
			if (names.get(i).equals(material.identifier))
				{
				stack.setItemDamage(i);
				return stack;
				}
			}
		return null;
		}
		
	public static void initializeDust()
		{
		int i = 0;
		for (Material material : TinkerRegistry.getAllMaterials())
			{
			if (material.isCastable() && !material.isHidden())
				{
				TinkersEnergistics.dusts[i] = TinkersEnergistics.metalDust.addMeta(i, material.getIdentifier());
				i++;
				}
			}/*
		TinkersEnergistics.dusts[i] = TinkersEnergistics.metalDust.addMeta(i, TinkersEnergistics.gold.getIdentifier());*/
		}
		
	public static void initializeDustRecipes()
		{
		for (int i = 0; i < 64; i++)
			{
			ItemStack dust = TinkersEnergistics.dusts[i];
			
			String name = TinkersEnergistics.metalDust.getNames().get(i);
			if (name == null) return;
			name = StringUtils.capitalize(name);
			
			String dustName = "dust" + name;
			OreDictionary.registerOre(dustName, dust);
			
			List<ItemStack> ingots = OreDictionary.getOres("ingot" + name, true);
			if (ingots.isEmpty()) continue;
			
			FurnaceRecipes.instance().addSmeltingRecipe(dust, ingots.get(0), 0);
			
			for (int j = 0; j < ingots.size(); j++)
				MachineRecipeHandler.addCrushingRecipe(ingots.get(j), dust);
			
			try
				{
				TinkerRegistry.registerMelting(dust, TinkerRegistry.getMelting(ingots.get(0)).output.getFluid(), 144);
				}
			catch (Exception e)
				{
				System.out.println(name);
				}
				
			ItemStack dust2 = dust.copy(); // No, i cannot stop doing this. Java is weird like that.
			dust2.setCount(ConfigHandler.dustsPerOre == -1 ? (int) (Config.oreToIngotRatio) : ConfigHandler.dustsPerOre);
			
			List<ItemStack> ores = OreDictionary.getOres("ore" + name, true);
			if (ores.isEmpty()) continue;
			
			for (int j = 0; j < ores.size(); j++)
				MachineRecipeHandler.addCrushingRecipe(ores.get(j), dust2);
			}
		}
		
	}