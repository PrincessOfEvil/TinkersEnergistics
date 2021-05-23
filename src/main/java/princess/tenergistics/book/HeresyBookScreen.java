package princess.tenergistics.book;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import princess.tenergistics.TEnergistics;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.TextElement;

public class HeresyBookScreen extends BookScreen
	{
	private static final int				FFAAAAAA_TEXT			= 0xFF5AC5FA;
	
	private static final int				AE8000_TEXT				= 0x5AC5FA;
	
	public static final ResourceLocation	TEX_BOOK_HERESY			= new ResourceLocation(TEnergistics.modID, "textures/gui/book/book.png");
	public static final ResourceLocation	TEX_BOOKFRONT_HERESY	= new ResourceLocation(TEnergistics.modID, "textures/gui/book/bookfront.png");
	
	public HeresyBookScreen(ITextComponent title, BookData book, ItemStack item)
		{
		super(title, book, item);
		}
		
	@Override
	@SuppressWarnings({ "deprecation" })
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
		initWidthsAndHeights();
		FontRenderer fontRenderer = this.book.fontRenderer;
		if (fontRenderer == null)
			{
			fontRenderer = this.minecraft.fontRenderer;
			}
			
		if (debug)
			{
			fill(matrixStack, 0, 0, fontRenderer.getStringWidth("DEBUG") + 4, fontRenderer.FONT_HEIGHT + 4, 0x55000000);
			fontRenderer.drawString(matrixStack, "DEBUG", 2, 2, 0xFFFFFFFF);
			}
			
		RenderSystem.enableAlphaTest();
		RenderSystem.enableBlend();
		
		// The books are unreadable at Gui Scale set to small, so we'll double the scale
		RenderSystem.pushMatrix();
		RenderSystem.color3f(1F, 1F, 1F);
		
		float coverR = ((this.book.appearance.coverColor >> 16) & 0xff) / 255.F;
		float coverG = ((this.book.appearance.coverColor >> 8) & 0xff) / 255.F;
		float coverB = (this.book.appearance.coverColor & 0xff) / 255.F;
		
		TextureManager render = this.minecraft.textureManager;
		
		if (this.getPage_() == -1)
			{
			render.bindTexture(TEX_BOOKFRONT_HERESY);
			RenderHelper.disableStandardItemLighting();
			
			RenderSystem.color3f(coverR, coverG, coverB);
			blit(matrixStack, this.width / 2 - PAGE_WIDTH_UNSCALED / 2, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, 0, 0, PAGE_WIDTH_UNSCALED, PAGE_HEIGHT_UNSCALED, TEX_SIZE, TEX_SIZE);
			RenderSystem.color3f(1F, 1F, 1F);
			
			if (!this.book.appearance.title.isEmpty())
				{
				blit(matrixStack, this.width / 2 - PAGE_WIDTH_UNSCALED / 2, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, 0, PAGE_HEIGHT_UNSCALED, PAGE_WIDTH_UNSCALED, PAGE_HEIGHT_UNSCALED, TEX_SIZE, TEX_SIZE);
				
				RenderSystem.pushMatrix();
				
				float scale = fontRenderer.getStringWidth(this.book.appearance.title) <= 67 ? 2.5F : 2F;
				
				RenderSystem.scalef(scale, scale, 1F);
				float xx = (this.width / 2) / scale + 3 - fontRenderer.getStringWidth(this.book.appearance.title) / 2;
				float yy = (this.height / 5 - fontRenderer.FONT_HEIGHT / 2) / scale - 4;
				fontRenderer.drawStringWithShadow(matrixStack, this.book.appearance.title, xx, yy, AE8000_TEXT);
				RenderSystem.popMatrix();
				}
				
			if (!this.book.appearance.subtitle.isEmpty())
				{
				RenderSystem.pushMatrix();
				RenderSystem.scalef(1.5F, 1.5F, 1F);
				fontRenderer
						.drawStringWithShadow(matrixStack, this.book.appearance.subtitle, (this.width / 2) / 1.5F + 7 - fontRenderer
								.getStringWidth(this.book.appearance.subtitle) / 2, (this.height / 2 + 100 - fontRenderer.FONT_HEIGHT * 2) / 1.5F, AE8000_TEXT);
				RenderSystem.popMatrix();
				}
			}
		else
			{
			render.bindTexture(TEX_BOOK_HERESY);
			RenderHelper.disableStandardItemLighting();
			
			RenderSystem.color3f(coverR, coverG, coverB);
			blit(matrixStack, this.width / 2 - PAGE_WIDTH_UNSCALED, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, 0, 0, PAGE_WIDTH_UNSCALED * 2, PAGE_HEIGHT_UNSCALED, TEX_SIZE, TEX_SIZE);
			
			RenderSystem.color3f(1F, 1F, 1F);
			
			if (this.getPage_() != 0)
				{
				blit(matrixStack, this.width / 2 - PAGE_WIDTH_UNSCALED, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, 0, PAGE_HEIGHT_UNSCALED, PAGE_WIDTH_UNSCALED, PAGE_HEIGHT_UNSCALED, TEX_SIZE, TEX_SIZE);
				
				RenderSystem.pushMatrix();
				this.drawerTransform(false);
				
				RenderSystem.scalef(PAGE_SCALE, PAGE_SCALE, 1F);
				
				if (this.book.appearance.drawPageNumbers)
					{
					String pNum = (this.getPage_() - 1) * 2 + 2 + "";
					fontRenderer.drawString(matrixStack, pNum, PAGE_WIDTH / 2 - fontRenderer
							.getStringWidth(pNum) / 2, PAGE_HEIGHT - 10, FFAAAAAA_TEXT);
					}
					
				int mX = this.getMouseX(false);
				int mY = this.getMouseY();
				
				// Not foreach to prevent conmodification crashes
				for (int i = 0; i < this.getElements(0).size(); i++)
					{
					BookElement element = this.getElements(0).get(i);
					RenderSystem.color4f(1F, 1F, 1F, 1F);
					
					if (element instanceof TextElement)
						{
						TextElement text = (TextElement) element;
						for (int j = 0; j < text.text.length; j++)
							{
							if (text.text[j].useOldColor && text.text[j].color.equals("black"))
								{
								text.text[j].useOldColor = false;
								text.text[j].rgbColor = 9236223;
								}
							}
						}
					element.draw(matrixStack, mX, mY, partialTicks, fontRenderer);
					}
					
				// Not foreach to prevent conmodification crashes
				for (int i = 0; i < this.getElements(0).size(); i++)
					{
					BookElement element = this.getElements(0).get(i);
					
					RenderSystem.color4f(0.5F, 1F, 1F, 1F);
					element.drawOverlay(matrixStack, mX, mY, partialTicks, fontRenderer);
					}
					
				RenderSystem.popMatrix();
				}
				
			// Rebind texture as the font renderer binds its own texture
			render.bindTexture(TEX_BOOK_HERESY);
			// Set color back to white
			RenderSystem.color4f(1F, 1F, 1F, 1F);
			RenderHelper.disableStandardItemLighting();
			
			int fullPageCount = this.book.getFullPageCount(this.advancementCache);
			if (this.getPage_() < fullPageCount - 1 || this.book.getPageCount(this.advancementCache) % 2 != 0)
				{
				blit(matrixStack, this.width / 2, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, PAGE_WIDTH_UNSCALED, PAGE_HEIGHT_UNSCALED, PAGE_WIDTH_UNSCALED, PAGE_HEIGHT_UNSCALED, TEX_SIZE, TEX_SIZE);
				
				RenderSystem.pushMatrix();
				this.drawerTransform(true);
				
				RenderSystem.scalef(PAGE_SCALE, PAGE_SCALE, 1F);
				
				if (this.book.appearance.drawPageNumbers)
					{
					String pNum = (this.getPage_() - 1) * 2 + 3 + "";
					fontRenderer.drawString(matrixStack, pNum, PAGE_WIDTH / 2 - fontRenderer
							.getStringWidth(pNum) / 2, PAGE_HEIGHT - 10, FFAAAAAA_TEXT);
					}
					
				int mX = this.getMouseX(true);
				int mY = this.getMouseY();
				
				// Not foreach to prevent conmodification crashes
				for (int i = 0; i < this.getElements(1).size(); i++)
					{
					BookElement element = this.getElements(1).get(i);
					
					if (element instanceof TextElement)
						{
						TextElement text = (TextElement) element;
						for (int j = 0; j < text.text.length; j++)
							{
							if (text.text[j].useOldColor && text.text[j].color.equals("black"))
								{
								text.text[j].useOldColor = false;
								text.text[j].rgbColor = 9236223;
								}
							}
						}
					RenderSystem.color4f(1F, 1F, 1F, 1F);
					element.draw(matrixStack, mX, mY, partialTicks, fontRenderer);
					}
					
				// Not foreach to prevent conmodification crashes
				for (int i = 0; i < this.getElements(1).size(); i++)
					{
					BookElement element = this.getElements(1).get(i);
					
					RenderSystem.color4f(1F, 1F, 1F, 1F);
					element.drawOverlay(matrixStack, mX, mY, partialTicks, fontRenderer);
					}
					
				RenderSystem.popMatrix();
				}
			}
			
		for (int i = 0; i < this.buttons.size(); ++i)
			{
			this.buttons.get(i).render(matrixStack, mouseX, mouseY, partialTicks);
			}
			
		RenderSystem.popMatrix();
		}
		
	}
