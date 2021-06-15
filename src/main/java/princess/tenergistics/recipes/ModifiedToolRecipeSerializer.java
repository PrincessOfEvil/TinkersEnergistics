package princess.tenergistics.recipes;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import princess.tenergistics.TEnergistics;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.ToolCore;

public class ModifiedToolRecipeSerializer extends RecipeSerializer<ModifiedToolRecipe>
	{
	
	@Override
	public ModifiedToolRecipe read(ResourceLocation recipeId, JsonObject json)
		{
		String group = JSONUtils.getString(json, "group", "");
		
		List<Ingredient> inputs = JsonHelper.parseList(json, "inputs", Ingredient::deserialize);
		ModifierEntry modifier = ModifierEntry.fromJson(JSONUtils.getJsonObject(json, "modifier"));
		ToolCore tool = RecipeHelper.deserializeItem(JSONUtils.getString(json, "result"), "result", ToolCore.class);
		
		return new ModifiedToolRecipe(recipeId, group, inputs, modifier, tool);
		}
		
	@Override
	public ModifiedToolRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
		{
		try
			{
			String group = buffer.readString(Short.MAX_VALUE);
			
			int size = buffer.readVarInt();
			ImmutableList.Builder<Ingredient> builder = ImmutableList.builder();
			for (int i = 0; i < size; i++)
				{
				builder.add(Ingredient.read(buffer));
				}
			ModifierEntry modifier = ModifierEntry.read(buffer);
			ToolCore tool = RecipeHelper.readItem(buffer, ToolCore.class);
			
			return new ModifiedToolRecipe(recipeId, group, builder.build(), modifier, tool);
			}
		catch (Exception e)
			{
			TConstruct.log.error("Error reading tool building recipe from packet.", e);
			throw e;
			}
		}
		
	@Override
	public void write(PacketBuffer buffer, ModifiedToolRecipe recipe)
		{
		try
			{
			buffer.writeString(recipe.group);
			
			buffer.writeVarInt(recipe.inputs.size());
			for (Ingredient ingredient : recipe.inputs)
				{
				ingredient.write(buffer);
				}
			recipe.modifier.write(buffer);
			RecipeHelper.writeItem(buffer, recipe.result);
			}
		catch (Exception e)
			{
			TConstruct.log.error("Error writing tool building recipe to packet.", e);
			throw e;
			}
		}
		
	@RequiredArgsConstructor(staticName = "modifiedToolBuildingRecipe")
	public static class Builder extends AbstractRecipeBuilder<Builder>
		{
		protected List<Ingredient>	inputs;
		protected ModifierEntry		modifier;
		private final ToolCore		result;
		
		public Builder setModifier(ModifierEntry mod)
			{
			modifier = mod;
			return this;
			}
		
		public Builder setInputs(Ingredient... in)
			{
			inputs = ImmutableList.copyOf(in);
			return this;
			}
		
		public Builder setInputs(List<Ingredient> in)
			{
			inputs = in;
			return this;
			}
			
		@Override
		public void build(Consumer<IFinishedRecipe> consumerIn)
			{
			this.build(consumerIn, Objects.requireNonNull(this.result.asItem().getRegistryName()));
			}
			
		@Override
		public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id)
			{
			ResourceLocation advancementId = this.buildOptionalAdvancement(id, "parts");
			consumerIn.accept(new Builder.Result(id, advancementId));
			}
			
		private class Result extends AbstractFinishedRecipe
			{
			public Result(ResourceLocation ID, @Nullable ResourceLocation advancementID)
				{
				super(ID, advancementID);
				}
				
			@Override
			public void serialize(JsonObject json)
				{
				if (!group.isEmpty())
					{
					json.addProperty("group", group);
					}
					
				JsonArray array = new JsonArray();
				for (Ingredient ingredient : inputs)
					{
					array.add(ingredient.serialize());
					}
				json.add("inputs", array);
				json.add("modifier", modifier.toJson());
				json.addProperty("result", Objects.requireNonNull(result.getRegistryName()).toString());
				}
				
			@Override
			public IRecipeSerializer<?> getSerializer()
				{
				return TEnergistics.modifiedToolRecipeSerializer.get();
				}
			}
		}
	}
