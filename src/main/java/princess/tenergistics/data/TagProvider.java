package princess.tenergistics.data;

import java.util.function.Consumer;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.common.data.ExistingFileHelper;
import princess.tenergistics.TEnergistics;
import slimeknights.mantle.registration.object.FluidObject;
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
		this.getOrCreateBuilder(TinkerTags.Blocks.FUEL_TANKS)
				.add(TEnergistics.searedCoilBlock.get(), TEnergistics.scorchedCoilBlock.get());
		this.getOrCreateBuilder(TinkerTags.Blocks.SMELTERY_TANKS).add(TEnergistics.searedCoilBlock.get());
		this.getOrCreateBuilder(TinkerTags.Blocks.FOUNDRY_TANKS).add(TEnergistics.scorchedCoilBlock.get());
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
							.get(), TEnergistics.bucketwheelWheel.get(), TEnergistics.buzzsawDisc.get());
			this.getOrCreateBuilder(TinkerTags.Items.GUIDEBOOKS).add(TEnergistics.miraculousMachinery.get());
			}
			
		private void addTools()
			{
			this.getOrCreateBuilder(TinkerTags.Items.MULTIPART_TOOL)
					.add(TEnergistics.jackhammer.get(), TEnergistics.bucketwheel.get(), TEnergistics.buzzsaw.get());
			this.getOrCreateBuilder(TinkerTags.Items.AOE)
					.add(TEnergistics.jackhammer.get(), TEnergistics.bucketwheel.get(), TEnergistics.buzzsaw.get());
			this.getOrCreateBuilder(TinkerTags.Items.HARVEST)
					.add(TEnergistics.jackhammer.get(), TEnergistics.bucketwheel.get(), TEnergistics.buzzsaw.get());
			this.getOrCreateBuilder(TinkerTags.Items.MELEE)
					.add(TEnergistics.jackhammer.get(), TEnergistics.bucketwheel.get(), TEnergistics.buzzsaw.get());
			
			this.getOrCreateBuilder(TinkerTags.Items.STONE_HARVEST).add(TEnergistics.jackhammer.get());
			
			this.getOrCreateBuilder(POWERED)
					.add(TEnergistics.jackhammer.get(), TEnergistics.bucketwheel.get(), TEnergistics.buzzsaw.get());
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
			addCast.accept(TEnergistics.bucketwheelWheelCast);
			addCast.accept(TEnergistics.buzzsawDiscCast);
			}
		}
		
	public static class FluidTag extends FluidTagsProvider
		{
		public FluidTag(DataGenerator generatorIn, String modId, ExistingFileHelper existingFileHelper)
			{
			super(generatorIn, modId, existingFileHelper);
			}
			
		@Override
		public void registerTags()
			{
			tagLocal(TEnergistics.moltenEnergy);
			}
			
		@Override
		public String getName()
			{
			return "Tinkers' Energistics Fluid Tags";
			}
			
		private void tagLocal(FluidObject<?> fluid)
			{
			getOrCreateBuilder(fluid.getLocalTag()).add(fluid.getStill(), fluid.getFlowing());
			}
			
		@SuppressWarnings("unused")
		private void tagAll(FluidObject<?> fluid)
			{
			tagLocal(fluid);
			getOrCreateBuilder(fluid.getForgeTag()).addTag(fluid.getLocalTag());
			}
		}
		
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
