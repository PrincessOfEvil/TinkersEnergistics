package princess.tenergistics.tools.stats;

import lombok.AccessLevel;
import lombok.Getter;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.library.exception.TinkerAPIMaterialException;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.AbstractToolStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.List;

/**
 * Standard stat builder for powered tools.
 */
@Getter(AccessLevel.PROTECTED)
public final class PoweredToolStatsBuilder extends AbstractToolStatsBuilder
	{
	private final List<HeadMaterialStats>		heads;
	private final List<HandleMaterialStats>		handles;
	private final List<ExtraMaterialStats>		extras;
	
	private final List<GearboxMaterialStats>	gearboxes;
	
	public PoweredToolStatsBuilder(ToolBaseStatDefinition baseStats, List<HeadMaterialStats> heads, List<HandleMaterialStats> handles, List<ExtraMaterialStats> extras, List<GearboxMaterialStats> gearboxes)
		{
		super(baseStats);
		this.heads = heads;
		this.handles = handles;
		this.extras = extras;
		this.gearboxes = gearboxes;
		}
		
	/** Creates a builder from the definition and materials */
	public static PoweredToolStatsBuilder from(ToolDefinition toolDefinition, List<IMaterial> materials)
		{
		List<IToolPart> requiredComponents = toolDefinition.getRequiredComponents();
		if (materials.size() != requiredComponents.size())
			{ throw TinkerAPIMaterialException.statBuilderWithInvalidMaterialCount(); }
			
		ToolBaseStatDefinition baseStats = toolDefinition.getBaseStatDefinition();
		List<HeadMaterialStats> headStats = listOfCompatibleWith(HeadMaterialStats.ID, materials, requiredComponents);
		int primaryWeight = baseStats.getPrimaryHeadWeight();
		if (primaryWeight > 1 && headStats.size() > 1)
			{
			for (int i = 1; i < primaryWeight; i++)
				{
				headStats.add(headStats.get(0));
				}
			}
			
		return new PoweredToolStatsBuilder(baseStats, headStats, listOfCompatibleWith(HandleMaterialStats.ID, materials, requiredComponents), listOfCompatibleWith(ExtraMaterialStats.ID, materials, requiredComponents), listOfCompatibleWith(GearboxMaterialStats.ID, materials, requiredComponents));
		}
		
	@Override
	protected void setStats(StatsNBT.Builder builder)
		{
		builder.set(ToolStats.DURABILITY, buildDurability());
		builder.set(ToolStats.HARVEST_LEVEL, buildHarvestLevel());
		builder.set(ToolStats.ATTACK_DAMAGE, buildAttackDamage());
		builder.set(ToolStats.ATTACK_SPEED, buildAttackSpeed());
		builder.set(ToolStats.MINING_SPEED, buildMiningSpeed());
		builder.set(TEnergistics.EFFICIENCY, (float) Math.max(0d, (1 + baseStats
				.getBonus(TEnergistics.EFFICIENCY)) * getAverageValue(gearboxes, GearboxMaterialStats::getEfficiency, 1)));
		builder.set(TEnergistics.MACHINE_SPEED, (float) Math.max(0d, (1 + baseStats
				.getBonus(TEnergistics.MACHINE_SPEED)) * getAverageValue(gearboxes, GearboxMaterialStats::getSpeed, 1)));
		}
		
	@Override
	protected boolean handles(IToolStat<?> stat)
		{
		return stat == ToolStats.DURABILITY || stat == ToolStats.HARVEST_LEVEL || stat == ToolStats.ATTACK_DAMAGE
				|| stat == ToolStats.ATTACK_SPEED || stat == ToolStats.MINING_SPEED || stat == TEnergistics.EFFICIENCY || stat == TEnergistics.MACHINE_SPEED;
		}
		
	/** Builds durability for the tool */
	public float buildDurability()
		{
		double averageHeadDurability = getAverageValue(heads, HeadMaterialStats::getDurability) + baseStats
				.getBonus(ToolStats.DURABILITY);
		double averageHandleModifier = getAverageValue(handles, HandleMaterialStats::getDurability, 1);
		// durability should never be below 1
		return Math.max(1, (int) (averageHeadDurability * averageHandleModifier));
		}
		
	/** Builds mining speed for the tool */
	public float buildMiningSpeed()
		{
		double averageHeadSpeed = getAverageValue(heads, HeadMaterialStats::getMiningSpeed) + baseStats
				.getBonus(ToolStats.MINING_SPEED);
		double averageHandleModifier = getAverageValue(handles, HandleMaterialStats::getMiningSpeed, 1);
		double averageGearboxModifier = getAverageValue(gearboxes, GearboxMaterialStats::getSpeed, 1);
		return (float) Math.max(0.1d, averageHeadSpeed * averageHandleModifier * averageGearboxModifier);
		}
		
	/** Builds attack speed for the tool */
	public float buildAttackSpeed()
		{
		float baseSpeed = 1 + baseStats.getBonus(ToolStats.ATTACK_SPEED);
		double averageHandleModifier = getAverageValue(handles, HandleMaterialStats::getAttackSpeed, 1);
		return (float) Math.max(0, baseSpeed * averageHandleModifier);
		}
		
	/** Builds the harvest level for the tool */
	public int buildHarvestLevel()
		{
		return heads.stream().mapToInt(HeadMaterialStats::getHarvestLevel).max().orElse(0);
		}
		
	/** Builds attack damage for the tool */
	public float buildAttackDamage()
		{
		double averageHeadAttack = getAverageValue(heads, HeadMaterialStats::getAttack) + baseStats
				.getBonus(ToolStats.ATTACK_DAMAGE);
		double averageHandle = getAverageValue(handles, HandleMaterialStats::getAttackDamage, 1.0f);
		double averageGearboxModifier = getAverageValue(gearboxes, GearboxMaterialStats::getSpeed, 1);
		return (float) Math.max(0.0d, averageHeadAttack * averageHandle * averageGearboxModifier);
		}
	}
