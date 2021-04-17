package princess.tenergistics.data;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class ToolsRecipeProvider extends RecipeProvider implements IConditionBuilder
	{
	//I swear to fuck, this has so much copypasta from TiC. I *wish* i could reuse their code, but i can't.
	public ToolsRecipeProvider(DataGenerator generator)
		{
		super(generator);
		}
		
	@Override
	public String getName()
		{
		return "Tinkers' Energistics Tool Recipes";
		}
		
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
		{
		addPartRecipes(consumer);
		addTinkerStationRecipes(consumer);
		addSmelteryRecipes(consumer);
		addMiscRecipes(consumer);
		}
		
	private void addPartRecipes(Consumer<IFinishedRecipe> consumer)
		{
		addPartRecipe(consumer, TEnergistics.toolCasing, 8, TEnergistics.toolCasingCast);
		addPartRecipe(consumer, TEnergistics.gearbox, 4, TEnergistics.gearboxCast);
		
		addPartRecipe(consumer, TEnergistics.jackhammerRod, 4, TEnergistics.jackhammerRodCast);
		addPartRecipe(consumer, TEnergistics.buzzsawDisc, 4, TEnergistics.buzzsawDiscCast);
		
		}
		
	private void addTinkerStationRecipes(Consumer<IFinishedRecipe> consumer)
		{
		String modifierFolder = "tools/modifiers/";
		String upgradeFolder = modifierFolder + "upgrade/";
		String powerGroup = "tenergistics:power_modifiers";
		
		addBuildingRecipe(consumer, TEnergistics.jackhammer);
		
		addBuildingRecipe(consumer, TEnergistics.buzzsaw);
		
		ShapedRecipeBuilder.shapedRecipe(TEnergistics.firebox)
				.key('-', TinkerSmeltery.searedBrick)
				.key('I', Items.IRON_INGOT)
				.key('n', Items.IRON_NUGGET)
				.patternLine("-I-")
				.patternLine("InI")
				.patternLine("-I-")
				.setGroup(powerGroup)
				.addCriterion("has_center", hasItem(Tags.Items.INGOTS_IRON))
				.build(consumer, prefix(TEnergistics.firebox, modifierFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.fireboxModifier.get())
				.addInput(TEnergistics.firebox)
				.setMaxLevel(1)
				.setTools(TagProvider.POWERED)
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.firebox, upgradeFolder));
		
		ShapedRecipeBuilder.shapedRecipe(TEnergistics.exchanger)
				.key('-', TinkerSmeltery.searedBrick)
				.key('I', Items.IRON_INGOT)
				.key('n', Items.IRON_NUGGET)
				.patternLine("I--")
				.patternLine("InI")
				.patternLine("--I")
				.setGroup(powerGroup)
				.addCriterion("has_center", hasItem(Tags.Items.INGOTS_IRON))
				.build(consumer, prefix(TEnergistics.exchanger, modifierFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.exchangerModifier.get())
				.addInput(TEnergistics.exchanger)
				.setMaxLevel(1)
				.setTools(TagProvider.POWERED)
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.exchanger, upgradeFolder));
		
		/*
		ShapedRecipeBuilder.shapedRecipe(TEnergistics.energyCoil)
				.key('-', TinkerSmeltery.searedBrick)
				.key('I', Items.IRON_INGOT)
				.key('n', Items.IRON_NUGGET)
				.patternLine("---")
				.patternLine("III")
				.patternLine("n-I")
				.setGroup(powerGroup)
				.addCriterion("has_center", hasItem(Tags.Items.INGOTS_IRON))
				.build(consumer, prefix(TEnergistics.energyCoil, modifierFolder));
				
		ModifierRecipeBuilder.modifier(TEnergistics.energyCoilModifier.get())
				.addInput(TEnergistics.energyCoil)
				.setMaxLevel(1)
				.setTools(TagProvider.POWERED)
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.energyCoil, upgradeFolder));
		*/
		
		ModifierRecipeBuilder.modifier(TEnergistics.capacityModifier.get())
				.addInput(TinkerMaterials.copper.getIngotTag())
				.addInput(TinkerMaterials.copper.getIngotTag())
				.addInput(ItemTags.LOGS)
				.addInput(Tags.Items.INGOTS_IRON)
				.addInput(Tags.Items.INGOTS_IRON)
				.setUpgradeSlots(1)
				.setMaxLevel(3)
				.setTools(TagProvider.POWERED)
				.setRequirements(ModifierMatch
						.list(1, ModifierMatch.entry(TEnergistics.fireboxModifier.get()), ModifierMatch
								.entry(TEnergistics.exchangerModifier.get())))
				.setRequirementsError("recipe.tenergistics.modifier.capacity_requirements")
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.capacityModifier, upgradeFolder));
		
		CustomRecipeBuilder.customRecipe(TEnergistics.tinkerStationFireboxRefuelSerializer.get())
				.build(consumer, location(modifierFolder + "tinker_station_firebox_refuel").toString());
		}
		
	private void addSmelteryRecipes(Consumer<IFinishedRecipe> consumer)
		{/*
			String folder = "smeltery/casting/";
			ContainerFillingRecipeBuilder.tableRecipe(TEnergistics.jackhammer, FluidAttributes.BUCKET_VOLUME).build(consumer, location(folder + "filling/jackhammer"));*/
		}
		
	private void addMiscRecipes(Consumer<IFinishedRecipe> consumer)
		{
		String folder = "misc/";
		ShapelessRecipeBuilder.shapelessRecipe(TEnergistics.miraculousMachinery)
				.addIngredient(Items.BOOK)
				.addIngredient(Items.COAL_BLOCK)
				.addIngredient(Items.LAVA_BUCKET)
				.addIngredient(Items.REDSTONE_BLOCK)
				.addCriterion("has_center", hasItem(TinkerCommons.mightySmelting))
				.build(consumer, prefix(TEnergistics.miraculousMachinery, folder));
		}
		
	private void addPartRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends IMaterialItem> sup, int cost, CastItemObject cast)
		{
		String folder = "tools/parts/";
		// Base data
		IMaterialItem part = sup.get();
		String name = Objects.requireNonNull(part.asItem().getRegistryName()).getPath();
		
		// Part Builder
		PartRecipeBuilder.partRecipe(part)
				.setPattern(location(name))
				.setCost(cost)
				.build(consumer, location(folder + "builder/" + name));
		
		// Material Casting
		String castingFolder = folder + "casting/";
		MaterialCastingRecipeBuilder.tableRecipe(part)
				.setItemCost(cost)
				.setCast(cast, false)
				.build(consumer, location(castingFolder + name + "_gold_cast"));
		MaterialCastingRecipeBuilder.tableRecipe(part)
				.setItemCost(cost)
				.setCast(cast.getSingleUseTag(), true)
				.build(consumer, location(castingFolder + name + "_sand_cast"));
		
		// Cast Casting
		MaterialIngredient ingredient = MaterialIngredient.fromItem(part);
		String partName = Objects.requireNonNull(part.asItem().getRegistryName()).getPath();
		ItemCastingRecipeBuilder.tableRecipe(cast)
				.setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT))
				.setCast(ingredient, true)
				.setSwitchSlots()
				.build(consumer, location("smeltery/casting/casts/" + partName));
		
		// sand cast molding
		MoldingRecipeBuilder.moldingTable(cast.getSand())
				.setMaterial(TinkerSmeltery.blankCast.getSand())
				.setPattern(ingredient, false)
				.build(consumer, location("smeltery/casting/sand_casts/" + partName));
		MoldingRecipeBuilder.moldingTable(cast.getRedSand())
				.setMaterial(TinkerSmeltery.blankCast.getRedSand())
				.setPattern(ingredient, false)
				.build(consumer, location("smeltery/casting/red_sand_casts/" + partName));
		
		// Part melting
		MaterialMeltingRecipeBuilder.melting(part, cost).build(consumer, location(folder + "melting/" + part));
		}
		
	private void addBuildingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends ToolCore> sup)
		{
		ToolCore toolCore = sup.get();
		String name = Objects.requireNonNull(toolCore.getRegistryName()).getPath();
		
		ToolBuildingRecipeBuilder.toolBuildingRecipe(toolCore).build(consumer, location("tools/building/" + name));
		}
		
	protected static ResourceLocation location(String id)
		{
		return new ResourceLocation(TEnergistics.modID, id);
		}
		
	protected static ResourceLocation prefix(IItemProvider item, String prefix)
		{
		ResourceLocation loc = Objects.requireNonNull(item.asItem().getRegistryName());
		return location(prefix + loc.getPath());
		}
		
	protected static ResourceLocation prefixR(Supplier<? extends IForgeRegistryEntry<?>> entry, String prefix)
		{
		ResourceLocation loc = Objects.requireNonNull(entry.get().getRegistryName());
		return location(prefix + loc.getPath());
		}
	}
