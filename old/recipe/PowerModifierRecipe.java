package princess.tenergistics.recipe;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import princess.tenergistics.data.TagProvider;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.OverslimeModifierRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;

public class PowerModifierRecipe implements ITinkerStationRecipe, IDisplayModifierRecipe
	{
	
	private final ResourceLocation		id;
	private final MaterialIngredient	ingredient;
	private final ModifierEntry			result;
	
	public PowerModifierRecipe(ResourceLocation id, MaterialIngredient ingredient, ModifierEntry result)
		{
		this.id = id;
		this.ingredient = ingredient;
		this.result = result;
		//ModifierRecipeLookup.addIngredient(ingredient);
		}
		
	@Override
	public boolean matches(ITinkerStationInventory inv, World world)
		{
		// TODO Auto-generated method stub
		return false;
		}
		
	@Override
	public IRecipeSerializer<?> getSerializer()
		{
		// TODO Auto-generated method stub
		return null;
		}
		
	public static class Serializer extends RecipeSerializer<PowerModifierRecipe>
		{
		@Override
		public PowerModifierRecipe read(ResourceLocation recipeId, JsonObject json)
			{
			MaterialIngredient ingredient = MaterialIngredient.Serializer.INSTANCE.parse(JSONUtils.getJsonObject(json, "ingredient"));
			ModifierEntry result = ModifierEntry.fromJson(JSONUtils.getJsonObject(json, "result"));
			
			return new PowerModifierRecipe(recipeId, ingredient, result);
			}
			
		@Override
		public PowerModifierRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
			{
			MaterialIngredient ingredient = MaterialIngredient.Serializer.INSTANCE.parse(buffer);
			ModifierEntry result = ModifierEntry.read(buffer);
			
			return new PowerModifierRecipe(recipeId, ingredient, result);
			}
			
		@Override
		public void write(PacketBuffer buffer, PowerModifierRecipe recipe)
			{
			MaterialIngredient.Serializer.INSTANCE.write(buffer, recipe.ingredient);
			recipe.result.write(buffer);
			}
		}
		
	private static final Lazy<List<ItemStack>>	DISPLAY_TOOLS	= Lazy.of(() -> TagProvider.POWERED.getAllElements().stream().map(MAP_TOOL_FOR_RENDERING).collect(Collectors.toList()));
	private List<List<ItemStack>>				displayItems	= null;
	
	@Override
	public List<List<ItemStack>> getDisplayItems()
		{
		if (displayItems == null)
			{
			CompoundNBT volatileNBT = new CompoundNBT();
			//ModDataNBT volatileData = ModDataNBT.readFromNBT(volatileNBT);
			CompoundNBT persistentNBT = new CompoundNBT();
			//ToolDefinition.EMPTY, ModDataNBT.readFromNBT(persistentNBT), volatileData
			
			List<ItemStack> displayOutputs = TagProvider.POWERED.getAllElements().stream().map(MAP_TOOL_FOR_RENDERING).map(stack -> {
				ItemStack out = IDisplayModifierRecipe.withModifiers(stack, null, result);
				CompoundNBT nbt = out.getOrCreateTag();
				nbt.put(ToolStack.TAG_VOLATILE_MOD_DATA, volatileNBT);
				nbt.put(ToolStack.TAG_PERSISTENT_MOD_DATA, persistentNBT);
				return out;
				}).collect(Collectors.toList());
			displayItems = Arrays.asList(displayOutputs, DISPLAY_TOOLS.get(), Arrays.asList(ingredient.getMatchingStacks()));;
			}
		return displayItems;
		}
		
	@Override
	public ModifierEntry getDisplayResult()
		{
		return result;
		}
		
	@Deprecated
	@Override
	public ItemStack getRecipeOutput()
		{
		return ItemStack.EMPTY;
		}
		
	@Override
	public ResourceLocation getId()
		{
		return id;
		}
	}
