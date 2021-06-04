package princess.tenergistics.tools.stats;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@With
public class GearboxMaterialStats extends BaseMaterialStats
	{
	public static final MaterialStatsId			ID						= new MaterialStatsId(TEnergistics.modID, "gearbox");
	public static final GearboxMaterialStats	DEFAULT					= new GearboxMaterialStats(1f, 1f);
	
	private static final String					EFFICIENCY_PREFIX		= makeTooltipKey("gearbox.efficiency");
	private static final String					SPEED_PREFIX			= makeTooltipKey("gearbox.machine_speed");
	
	private static final ITextComponent			EFFICIENCY_DESCRIPTION	= makeTooltip("gearbox.efficiency.description");
	private static final ITextComponent			SPEED_DESCRIPTION		= makeTooltip("gearbox.machine_speed.description");
	private static final List<ITextComponent>	DESCRIPTION				= ImmutableList
			.of(EFFICIENCY_DESCRIPTION, SPEED_DESCRIPTION);
	
	private float								efficiency;
	private float								speed;
	
	@Override
	public IFormattableTextComponent getLocalizedName()
		{
		return new TranslationTextComponent(String.format("stat.%s.%s", this.getIdentifier().getNamespace(), this.getIdentifier().getPath()));
		}
		
	@Override
	public MaterialStatsId getIdentifier()
		{
		return ID;
		}
		
	@Override
	public List<ITextComponent> getLocalizedInfo()
		{
		List<ITextComponent> list = new ArrayList<>();
		list.add(IToolStat.formatColoredMultiplier(EFFICIENCY_PREFIX, efficiency));
		list.add(IToolStat.formatColoredMultiplier(SPEED_PREFIX, speed));
		return list;
		}
		
	@Override
	public List<ITextComponent> getLocalizedDescriptions()
		{
		return DESCRIPTION;
		}
		
	@Override
	public void encode(PacketBuffer buffer)
		{
		buffer.writeFloat(this.efficiency);
		buffer.writeFloat(this.speed);
		}
		
	@Override
	public void decode(PacketBuffer buffer)
		{
		this.efficiency = buffer.readFloat();
		this.speed = buffer.readFloat();
		}
		
	protected static String makeTooltipKey(String name)
		{
		return net.minecraft.util.Util.makeTranslationKey("stat", new ResourceLocation(TEnergistics.modID, name));
		}
		
	protected static ITextComponent makeTooltip(String name)
		{
		return new TranslationTextComponent(makeTooltipKey(name));
		}
	}
