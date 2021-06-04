package princess.tenergistics.book;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.book.sectiontransformer.ContentListingSectionTransformer;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;

@OnlyIn(Dist.CLIENT)
public class EnergisticsModifierSectionTransformer extends ContentListingSectionTransformer
	{
	
	public EnergisticsModifierSectionTransformer()
		{
		super("energistics_modifiers");
		}
		
	@Override
	protected void processPage(BookData book, ContentListing listing, PageData page)
		{
		if (page.content instanceof EnergisticsContentModifier)
			{
			ModifierId modifierId = new ModifierId(((EnergisticsContentModifier) page.content).modifierID);
			if (TinkerRegistries.MODIFIERS.containsKey(modifierId))
				{
				Modifier modifier = TinkerRegistries.MODIFIERS.getValue(modifierId);
				assert modifier != null; // contains key was true
				listing.addEntry(modifier.getDisplayName().getString(), page);
				}
			}
		else
			if (page.content instanceof ContentModifier)
				{
				ModifierId modifierId = new ModifierId(((ContentModifier) page.content).modifierID);
				if (TinkerRegistries.MODIFIERS.containsKey(modifierId))
					{
					Modifier modifier = TinkerRegistries.MODIFIERS.getValue(modifierId);
					assert modifier != null; // contains key was true
					listing.addEntry(modifier.getDisplayName().getString(), page);
					}
				}
			else
				{
				super.processPage(book, listing, page);
				}
		}
		
	public static class HeartSectionTransformer extends ContentListingSectionTransformer
		{
		public HeartSectionTransformer()
			{
			super("energistics_modifiers_hearts");
			}
			
		@Override
		protected void processPage(BookData book, ContentListing listing, PageData page)
			{
			if (page.content instanceof EnergisticsContentModifier)
				{
				ModifierId modifierId = new ModifierId(((EnergisticsContentModifier) page.content).modifierID);
				if (TinkerRegistries.MODIFIERS.containsKey(modifierId))
					{
					Modifier modifier = TinkerRegistries.MODIFIERS.getValue(modifierId);
					assert modifier != null; // contains key was true
					listing.addEntry(modifier.getDisplayName().getString(), page);
					}
				}
			else
				if (page.content instanceof ContentModifier)
					{
					ModifierId modifierId = new ModifierId(((ContentModifier) page.content).modifierID);
					if (TinkerRegistries.MODIFIERS.containsKey(modifierId))
						{
						Modifier modifier = TinkerRegistries.MODIFIERS.getValue(modifierId);
						assert modifier != null; // contains key was true
						listing.addEntry(modifier.getDisplayName().getString(), page);
						}
					}
				else
					{
					super.processPage(book, listing, page);
					}
			}
		}
	}
