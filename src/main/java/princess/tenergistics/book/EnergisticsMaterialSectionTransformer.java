package princess.tenergistics.book;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import princess.tenergistics.TEnergistics;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.sectiontransformer.materials.AbstractMaterialSectionTransformer;
import slimeknights.tconstruct.library.materials.IMaterial;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EnergisticsMaterialSectionTransformer extends AbstractMaterialSectionTransformer {

  public EnergisticsMaterialSectionTransformer() {
    super("energistics_materials");
  }

  @Override
  protected boolean isValidMaterial(IMaterial material) {
    return material.getIdentifier().getNamespace().equals(TEnergistics.modID);
  }

  @Override
  protected PageContent getPageContent(IMaterial material, List<ItemStack> displayStacks) {
    return new ContentMaterial(material, displayStacks);
  }
}
