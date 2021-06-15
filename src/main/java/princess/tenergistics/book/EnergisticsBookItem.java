package princess.tenergistics.book;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import princess.tenergistics.TEnergistics;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.mantle.item.LecternBookItem;
import slimeknights.tconstruct.library.book.sectiontransformer.ModifierSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.ToolSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.materials.MaterialSectionTransformer;

public class EnergisticsBookItem extends LecternBookItem
	{
	//C O P Y P A S T I N G
	private final EnergisticsBookType bookType;
	
	public EnergisticsBookItem(Properties props, EnergisticsBookType bookType)
		{
		super(props);
		this.bookType = bookType;
		}
		
	@SuppressWarnings("resource")
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
		{
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote)
			{
			BookData book = EnergisticsBook.getBook(bookType);
			//Thankfully the call has its own check.
			book.load();
			if (Minecraft.getInstance().player != null)
				{
				String page = BookHelper.getCurrentSavedPage(stack);
				Minecraft.getInstance()
						.displayGuiScreen(new HeresyBookScreen(getDisplayName(stack), book, page, newPage -> BookLoader
								.updateSavedPage(Minecraft.getInstance().player, handIn, newPage)));
				}
			}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
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
			BookLoader.registerPageType(EnergisticsContentModifier.ID, EnergisticsContentModifier.class);
			addData(MIRACULOUS_MACHINERY, MIRACULOUS_MACHINERY_ID);
			}
			
		private static void addData(BookData book, ResourceLocation id)
			{
			book.addRepository(new FileRepository(id.getNamespace() + ":book/" + id.getPath()));
			book.addTransformer(new ToolSectionTransformer());
			book.addTransformer(new MaterialSectionTransformer());
			book.addTransformer(new ModifierSectionTransformer());
			book.addTransformer(new EnergisticsMaterialSectionTransformer());
			book.addTransformer(new EnergisticsModifierSectionTransformer());
			book.addTransformer(new EnergisticsModifierSectionTransformer.HeartSectionTransformer());
			book.addTransformer(new MachineMaterialSectionTransformer());
			book.addTransformer(BookTransformer.indexTranformer());
			}
			
		public static BookData getBook(EnergisticsBookType bookType)
			{
			return MIRACULOUS_MACHINERY;
			}
		}
		
	@Override
	public void openLecternScreenClient(BlockPos pos, ItemStack stack)
		{
		BookData book = EnergisticsBook.getBook(bookType);
		book.load();
		String page = BookHelper.getCurrentSavedPage(stack);
		
		Minecraft.getInstance()
				.displayGuiScreen(new HeresyBookScreen(stack
						.getDisplayName(), book, page, newPage -> BookLoader.updateSavedPage(pos, newPage)));
		}
	}
