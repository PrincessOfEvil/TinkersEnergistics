package princess.tenergistics.data;

import static slimeknights.tconstruct.library.utils.HarvestLevels.*;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.tools.stats.GearboxMaterialStats;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.tools.data.MaterialIds;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public class EnergisticsMaterialProvider extends AbstractMaterialDataProvider
	{
	public static final MaterialId	malachite		= id("malachite");
	public static final MaterialId	glaucodot		= id("glaucodot");
	
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
			addMaterialStats(malachite, new HeadMaterialStats(150, 6f, IRON, 1.5f), new HandleMaterialStats(0.6f, 1.1f, 1.1f, 1f), ExtraMaterialStats.DEFAULT, new GearboxMaterialStats(0.6f, 1.1f));
			addMaterialStats(glaucodot, new HeadMaterialStats(900, 7f, DIAMOND, 2f), new HandleMaterialStats(1.13f, 1.13f, 1f, 1.13f), ExtraMaterialStats.DEFAULT, new GearboxMaterialStats(1.13f, 1.13f));
			
			//T1.1
			addMaterialStats(MaterialIds.wood, GearboxMaterialStats.DEFAULT);
			
			addMaterialStats(MaterialIds.flint, new GearboxMaterialStats(1.2f, 0.8f));
			addMaterialStats(MaterialIds.stone, new GearboxMaterialStats(0.7f, 1.1f));
			addMaterialStats(MaterialIds.bone, new GearboxMaterialStats(0.65f, 1.1f));
			//T1.3
			addMaterialStats(MaterialIds.necroticBone, new GearboxMaterialStats(0.5f, 1.15f));
			
			//T2.1
			addMaterialStats(MaterialIds.iron, new GearboxMaterialStats(1.3f, 1f));
			addMaterialStats(MaterialIds.copper, new GearboxMaterialStats(1.1f, 1.05f));
			
			addMaterialStats(MaterialIds.searedStone, new GearboxMaterialStats(0.65f, 1.2f));
			addMaterialStats(MaterialIds.scorchedStone, new GearboxMaterialStats(0.65f, 1.2f));
			
			addMaterialStats(MaterialIds.slimewood, new GearboxMaterialStats(1.6f, 0.85f));
			
			//T2.2
			addMaterialStats(MaterialIds.silver, new GearboxMaterialStats(0.85f, 1.1f));
			addMaterialStats(MaterialIds.lead, new GearboxMaterialStats(0.7f, 1.2f));
			
			//T3.1
			addMaterialStats(MaterialIds.tinkersBronze, new GearboxMaterialStats(1.2f, 1.15f));
			addMaterialStats(MaterialIds.nahuatl, new GearboxMaterialStats(1.8f, 0.8f));
			
			addMaterialStats(MaterialIds.slimesteel, new GearboxMaterialStats(1.5f, 0.95f));
			addMaterialStats(MaterialIds.pigIron, new GearboxMaterialStats(1.3f, 1f));
			addMaterialStats(MaterialIds.roseGold, new GearboxMaterialStats(0.25f, 1.5f));
			
			//T3.2
			addMaterialStats(MaterialIds.bronze, new GearboxMaterialStats(1.4f, 1.1f));
			addMaterialStats(MaterialIds.electrum, new GearboxMaterialStats(0.5f, 1.35f));
			addMaterialStats(MaterialIds.steel, new GearboxMaterialStats(1.3f, 1.15f));
			addMaterialStats(MaterialIds.constantan, new GearboxMaterialStats(0.8f, 1.25f));
			
			//T3.3
			addMaterialStats(MaterialIds.cobalt, new GearboxMaterialStats(1f, 1.2f));
			
			//T4.1
			addMaterialStats(MaterialIds.queensSlime, new GearboxMaterialStats(1.8f, 1f));
			addMaterialStats(MaterialIds.manyullyn, new GearboxMaterialStats(0.75f, 1.3f));
			addMaterialStats(MaterialIds.hepatizon, new GearboxMaterialStats(1.2f, 1.2f));
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