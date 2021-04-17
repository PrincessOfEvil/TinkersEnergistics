package princess.tenergistics.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import princess.tenergistics.TEnergistics;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.library.book.sectiontransformer.ModifierSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.ToolSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.materials.MaterialSectionTransformer;

public class EnergisticsBookItem extends TooltipItem
	{
	//C O P Y P A S T I N G
	private final EnergisticsBookType bookType;
	
	public EnergisticsBookItem(Properties props, EnergisticsBookType bookType)
		{
		super(props);
		this.bookType = bookType;
		}
		
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
		{
		ItemStack itemStack = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote)
			{
			EnergisticsBook.getBook(bookType).openGui(getDisplayName(itemStack), itemStack);
			}
		return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
		}
		
	public enum EnergisticsBookType
		{
	MIRACULOUS_MACHINERY
		}
		
	public static class EnergisticsBook extends BookData
		{
		
		private static final ResourceLocation	MIRACULOUS_MACHINERY_ID	= new ResourceLocation(TEnergistics.modID, "miraculous_machinery");
		
		public final static BookData			MIRACULOUS_MACHINERY	= BookLoader
				.registerBook(MIRACULOUS_MACHINERY_ID.toString(), false, false);
		
		public static void initBook()
			{
			addData(MIRACULOUS_MACHINERY, MIRACULOUS_MACHINERY_ID);
			}
			
		private static void addData(BookData book, ResourceLocation id)
			{
			book.addRepository(new FileRepository(id.getNamespace() + ":book/" + id.getPath()));
			book.addTransformer(new MaterialSectionTransformer());
			book.addTransformer(new ToolSectionTransformer());
			book.addTransformer(new ModifierSectionTransformer());
			book.addTransformer(BookTransformer.indexTranformer());
			}
			
		public static BookData getBook(EnergisticsBookType bookType)
			{
			return MIRACULOUS_MACHINERY;
			}
		}
	}
