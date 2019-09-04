package princess.tinkersenergistics.library;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import org.apache.commons.lang3.tuple.Pair;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialGUI;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IPattern;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;
import java.util.Locale;

public abstract class AbstractTinkerPulse extends TinkerPulse
	{
	
	protected static List<ToolCore>				tools				= Lists.newLinkedList();
	protected static List<ToolPart>				toolparts			= Lists.newLinkedList();
	protected static List<IModifier>			modifiers			= Lists.newLinkedList();
	protected static List<Pair<Item, ToolPart>>	toolPartPatterns	= Lists.newLinkedList();
	public static final List<Material>			materials			= Lists.newArrayList();
	
	protected void registerStencil(Item pattern, ToolPart toolPart)
		{
		for (ToolCore toolCore : TinkerRegistry.getTools())
			{
			for (PartMaterialType partMaterialType : toolCore.getRequiredComponents())
				{
				if (partMaterialType.getPossibleParts().contains(toolPart))
					{
					ItemStack stencil = new ItemStack(pattern);
					Pattern.setTagForPart(stencil, toolPart);
					TinkerRegistry.registerStencilTableCrafting(stencil);
					return;
					}
				}
			}
		}
		
	protected static <T extends ToolCore> T registerTool(IForgeRegistry<Item> registry, T item, String unlocName)
		{
		tools.add(item);
		return registerItem(registry, item, unlocName);
		}
		
	protected ToolPart registerToolPart(IForgeRegistry<Item> registry, ToolPart part, String name)
		{
		return registerToolPart(registry, part, name, TinkerTools.pattern);
		}
		
	protected <T extends Item & IPattern> ToolPart registerToolPart(IForgeRegistry<Item> registry, ToolPart part, String name, T pattern)
		{
		ToolPart ret = registerItem(registry, part, name);
		
		if (pattern != null)
			{
			toolPartPatterns.add(Pair.of(pattern, ret));
			}
			
		toolparts.add(ret);
		
		return ret;
		}
		
	protected <T extends IModifier> T registerModifier(T modifier)
		{
		modifiers.add(modifier);
		return modifier;
		}
		
	protected static <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name)
		{
		if (!name.equals(name.toLowerCase(Locale.US))) { throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! Item: %s", name)); }
		
		item.setUnlocalizedName(String.format("%s.%s", ModInfo.MODID, name.toLowerCase(Locale.US)));
		item.setRegistryName(new ResourceLocation(ModInfo.MODID, name));
		registry.register(item);
		return item;
		}
	
	  protected static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name) {
	    if(!name.equals(name.toLowerCase(Locale.US))) {
	      throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! Block: %s", name));
	    }

	    String prefixedName = String.format("%s.%s", ModInfo.MODID, name.toLowerCase(Locale.US));
	    block.setUnlocalizedName(prefixedName);

	    register(registry, block, name);
	    return block;
	  }

		
	protected static Material mat(String name, int color, boolean hidden)
		{
		Material mat = new Material(name, color, hidden);
		materials.add(mat);
		return mat;
		}
	
	protected static MaterialGUI mat(String name, int color)
		{
		MaterialGUI mat = new MaterialGUI(name);
		mat.materialTextColor = color;
		materials.add(mat);
		return mat;
		}
	}