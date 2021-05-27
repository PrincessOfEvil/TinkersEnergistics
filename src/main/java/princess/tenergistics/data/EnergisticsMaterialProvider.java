package princess.tenergistics.data;

import static slimeknights.tconstruct.library.utils.HarvestLevels.*;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public class EnergisticsMaterialProvider extends AbstractMaterialDataProvider
	{
	public static final MaterialId	malachite	= id("malachite");
	public static final MaterialId	glaucodot	= id("glaucodot");
	
	public EnergisticsMaterialProvider(DataGenerator gen)
		{
		super(gen);
		}
		
	@Override
	public String getName()
		{
		return "Tinkers' Energistics Materials";
		}
		
	@Override
	protected void addMaterials()
		{
		addMaterialNoFluid(malachite, 2, ORDER_SPECIAL, false, 0x3ce186);
		addMaterialNoFluid(glaucodot, 3, ORDER_NETHER, false, 0xe8806c);
		}
		
	private static MaterialId id(String name)
		{
		return new MaterialId(new ResourceLocation(TEnergistics.modID, name));
		}
		
	public static class StatsProvider extends AbstractMaterialStatsDataProvider
		{
		public StatsProvider(DataGenerator gen, AbstractMaterialDataProvider materials)
			{
			super(gen, materials);
			}
			
		@Override
		public String getName()
			{
			return "Tinkers' Energistics Material Stats";
			}
			
		@Override
		protected void addMaterialStats()
			{
			addMaterialStats(malachite, new HeadMaterialStats(150, 6f, IRON, 1.5f), new HandleMaterialStats(0.6f, 1.1f, 1.1f, 1f), ExtraMaterialStats.DEFAULT);
			addMaterialStats(glaucodot, new HeadMaterialStats(900, 7f, DIAMOND, 2f), new HandleMaterialStats(1.13f, 1.13f, 1f, 1.13f), ExtraMaterialStats.DEFAULT);
			}
		}
		
	public static class TraitProvider extends AbstractMaterialTraitDataProvider
		{
		
		public TraitProvider(DataGenerator gen, AbstractMaterialDataProvider materials)
			{
			super(gen, materials);
			}
			
		@Override
		public String getName()
			{
			return "Tinkers' Energistics Material Traits";
			}

		@Override
		protected void addMaterialTraits()
			{
		    addDefaultTraits(malachite, TEnergistics.capacityTrait.get());
		    addDefaultTraits(glaucodot, TEnergistics.overclockTrait.get());
			}
		}
	}