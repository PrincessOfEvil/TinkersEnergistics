package princess.tenergistics.book;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import princess.tenergistics.tools.stats.GearboxMaterialStats;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.book.sectiontransformer.materials.AbstractMaterialSectionTransformer;
import slimeknights.tconstruct.library.materials.IMaterial;

@OnlyIn(Dist.CLIENT)
public class MachineMaterialSectionTransformer extends AbstractMaterialSectionTransformer
	{
	
	public MachineMaterialSectionTransformer()
		{
		super("energistics_machine_materials");
		}
		
	@Override
	protected boolean isValidMaterial(IMaterial material)
		{
		return MaterialRegistry.getInstance()
				.getMaterialStats(material.getIdentifier(), GearboxMaterialStats.ID).isPresent();
		}
		
	@Override
	protected PageContent getPageContent(IMaterial material, List<ItemStack> displayStacks)
		{
		return new ContentMachineMaterial(material, displayStacks);
		}
	}
