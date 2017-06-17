package princess.tinkersenergistics.item;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import princess.tinkersenergistics.library.StatHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;

public class MachineModPart extends MachinePart
	{
	
	public MachineModPart(int cost, int type)
		{
		super(cost, type);
		}
		
	@Override
	public boolean hasUseForStat(String stat)
		{
		return stat.equals(MaterialTypes.HEAD);
		}
		
	@Override
	public boolean canUseMaterial(Material mat)
		{
		return mat.hasStats(MaterialTypes.HEAD);
		}
		
	@Override
	public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
		{
		for (Material mat : TinkerRegistry.getAllMaterialsWithStats(MaterialTypes.HEAD))
			{
			subItems.add(getItemstackWithMaterial(mat));
			if (!Config.listAllMaterials)
				{
				break;
				}
			}
		}
		
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
		{
		Material material = getMaterial(stack);
		
		// Material traits/info
		boolean shift = Util.isShiftKeyDown();
		
		// Stats
		if (Config.extraTooltips)
			{
			if (!shift)
				{
				// info tooltip for detailed and component info
				tooltip.add(Util.translate("tooltip.tool.holdShift"));
				}
			else
				{
				for (IMaterialStats stat : material.getAllStats())
					{
					if (hasUseForStat(stat.getIdentifier()))
						{
						List<String> text = StatHelper.getLocalizedInfoMod((HeadMaterialStats) stat, type);
						if (!text.isEmpty())
							{
							tooltip.add(TextFormatting.LIGHT_PURPLE.toString() + TextFormatting.UNDERLINE + StatHelper.getLocalizedName("ps"));
							tooltip.addAll(StatHelper.getLocalizedInfoMod((HeadMaterialStats) stat, type));
							}
						}
					}
				}
			}
			
		String materialInfo = I18n.translateToLocalFormatted("tooltip.part.material_added_by", TinkerRegistry.getTrace(material).getName());
		tooltip.add("");
		tooltip.add(materialInfo);
		}
	}