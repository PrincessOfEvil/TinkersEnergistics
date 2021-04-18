package princess.tenergistics.data;

import java.util.function.Consumer;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.common.data.ExistingFileHelper;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;

public class TagProvider extends BlockTagsProvider
	{
	
	public static final IOptionalNamedTag<Item> POWERED = tag("modifiable/powered");
	
	public TagProvider(DataGenerator generatorIn, String modId, ExistingFileHelper existingFileHelper)
		{
		super(generatorIn, modId, existingFileHelper);
		}
		
	@Override
	public String getName()
		{
		return "Tinkers' Energistics Block Tags";
		}
		
	@Override
	protected void registerTags()
		{
		
		}
		
	public static class ItemTag extends ItemTagsProvider
		{
		
		public ItemTag(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId, ExistingFileHelper existingFileHelper)
			{
			super(dataGenerator, blockTagProvider, modId, existingFileHelper);
			}
			
		@Override
		public String getName()
			{
			return "Tinkers' Energistics Item Tags";
			}
			
		@Override
		protected void registerTags()
			{
			addParts();
			addTools();
			addSmeltery();
			}
			
		private void addParts()
			{
			this.getOrCreateBuilder(TinkerTags.Items.TOOL_PARTS)
					.add(TEnergistics.toolCasing.get(), TEnergistics.gearbox.get(), TEnergistics.jackhammerRod
							.get(), TEnergistics.buzzsawDisc.get());
			}
			
		private void addTools()
			{
			this.getOrCreateBuilder(TinkerTags.Items.MULTIPART_TOOL)
					.add(TEnergistics.jackhammer.get(), TEnergistics.buzzsaw.get());
			this.getOrCreateBuilder(TinkerTags.Items.AOE)
					.add(TEnergistics.jackhammer.get(), TEnergistics.buzzsaw.get());
			this.getOrCreateBuilder(TinkerTags.Items.HARVEST)
					.add(TEnergistics.jackhammer.get(), TEnergistics.buzzsaw.get());
			this.getOrCreateBuilder(TinkerTags.Items.MELEE)
					.add(TEnergistics.jackhammer.get(), TEnergistics.buzzsaw.get());
			
			this.getOrCreateBuilder(TinkerTags.Items.STONE_HARVEST).add(TEnergistics.jackhammer.get());
			
			this.getOrCreateBuilder(POWERED).add(TEnergistics.jackhammer.get(), TEnergistics.buzzsaw.get());
			}
			
		private void addSmeltery()
			{
			TagsProvider.Builder<Item> goldCasts = this.getOrCreateBuilder(TinkerTags.Items.GOLD_CASTS);
			TagsProvider.Builder<Item> sandCasts = this.getOrCreateBuilder(TinkerTags.Items.SAND_CASTS);
			TagsProvider.Builder<Item> redSandCasts = this.getOrCreateBuilder(TinkerTags.Items.RED_SAND_CASTS);
			Consumer<CastItemObject> addCast = cast -> {
			goldCasts.add(cast.get());
			sandCasts.add(cast.getSand());
			redSandCasts.add(cast.getRedSand());
			this.getOrCreateBuilder(cast.getSingleUseTag()).add(cast.getSand(), cast.getRedSand());
			};
			
			addCast.accept(TEnergistics.toolCasingCast);
			addCast.accept(TEnergistics.gearboxCast);
			addCast.accept(TEnergistics.jackhammerRodCast);
			addCast.accept(TEnergistics.buzzsawDiscCast);
			}
		}
		
	/*
	public static class FluidTag extends FluidTagsProvider
	{
	public static final IOptionalNamedTag<Fluid> SMELTERY_FUELS = tag("smeltery/fuels");
	
	public FluidTag(DataGenerator generatorIn, String modId, ExistingFileHelper existingFileHelper)
		{
		super(generatorIn, modId, existingFileHelper);
		}
		
	@Override
	public void registerTags()
		{
		this.getOrCreateBuilder(SMELTERY_FUELS).add(Fluids.LAVA).add(TinkerFluids.moltenBlaze.get());
		}
		
	private static IOptionalNamedTag<Fluid> tag(String name)
		{
		return FluidTags.createOptional(new ResourceLocation("tconstruct", name));
		}
	}
	*/
	private static IOptionalNamedTag<Item> tag(String name)
		{
		return ItemTags.createOptional(new ResourceLocation(TEnergistics.modID, name));
		}
		
	@SuppressWarnings("unused")
	private static IOptionalNamedTag<Item> forgeTag(String name)
		{
		return ItemTags.createOptional(new ResourceLocation("forge", name));
		}
	}
