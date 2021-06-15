package princess.tenergistics.recipes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@AllArgsConstructor
public class ModifiedToolRecipe implements ITinkerStationRecipe
	{
	@Getter
	protected final ResourceLocation	id;
	@Getter
	protected final String				group;
	protected final List<Ingredient>	inputs;
	protected final ModifierEntry		modifier;
	protected final ToolCore			result;
	
	@Override
	public IRecipeSerializer<?> getSerializer()
		{
		return TEnergistics.modifiedToolRecipeSerializer.get();
		}
		
	@Override
	public boolean matches(ITinkerStationInventory inv, World world)
		{
		if (!inv.getTinkerableStack().isEmpty())
			{ return false; }
			
		// each part must match the given slot
		List<IToolPart> parts = result.getToolDefinition().getRequiredComponents();
		int i;
		for (i = 0; i < parts.size(); i++)
			{
			if (parts.get(i).asItem() != inv.getInput(i).getItem())
				{ return false; }
			}
			
		//check if sized inputs fit
		int j = i;
		for (; i < j + inputs.size(); i++)
			{
			if (!inputs.get(i - j).test(inv.getInput(i)))
				{ return false; }
			}
			
		// remaining slots must be empty
		for (; i < inv.getInputCount(); i++)
			{
			if (!inv.getInput(i).isEmpty())
				{ return false; }
			}
			
		return true;
		}
		
	@Override
	public ItemStack getCraftingResult(ITinkerStationInventory inv)
		{
		List<IMaterial> materials = IntStream.range(0, result.getToolDefinition().getRequiredComponents().size())
				.mapToObj(inv::getInput)
				.map(IMaterialItem::getMaterialFromStack)
				.collect(Collectors.toList());
		ToolStack tool = ToolStack.from(ToolBuildHandler.buildItemFromMaterials(this.result, materials));
		tool.addModifier(modifier.getModifier(), modifier.getLevel());
		
		return tool.createStack();
		}
		
	/** @deprecated Use {@link #getCraftingResult(ITinkerStationInventory)} */
	@Deprecated
	@Override
	public ItemStack getRecipeOutput()
		{
		return new ItemStack(this.result);
		}
	}
