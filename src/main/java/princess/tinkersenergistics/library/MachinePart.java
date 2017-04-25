package princess.tinkersenergistics.library;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import princess.tinkersenergistics.TinkersEnergistics;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolPart;

public class MachinePart extends ToolPart
	{
	public MachinePart(int cost)
		{
		super(cost);
		}
		
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
		{
		Material material = getMaterial(stack);
		
		// Material traits/info
		boolean shift = Util.isShiftKeyDown();
		
		if (!checkMissingMaterialTooltip(stack, tooltip))
			{
			tooltip.addAll(getTooltipTraitInfo(material));
			}
			
		// Stats
		if (Config.extraTooltips)
			{
			if (!shift)
				{
				// info tooltip for detailed and component info
				tooltip.add("");
				tooltip.add(Util.translate("tooltip.tool.holdShift"));
				}
			else
				{
				for (IMaterialStats stat : material.getAllStats())
					{
					if (hasUseForStat(stat.getIdentifier()))
						{
						List<String> text = stat.getLocalizedInfo();
						if (!text.isEmpty())
							{
							tooltip.add("");
							tooltip.add(TextFormatting.WHITE.toString() + TextFormatting.UNDERLINE + stat.getLocalizedName());
							tooltip.addAll(stat.getLocalizedInfo());
							}
						}
					}
				}
			}
			
		String materialInfo = I18n.translateToLocalFormatted("tooltip.part.material_added_by", TinkerRegistry.getTrace(material));
		tooltip.add("");
		tooltip.add(materialInfo);
		}
	
	@Nonnull
	@Override
	public EnumRarity getRarity(ItemStack stack)
		{
		return TinkersEnergistics.SKAIAN;
		}
	}
