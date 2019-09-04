package princess.tinkersenergistics.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import princess.tinkersenergistics.block.tile.TileMachine;
import princess.tinkersenergistics.capability.MachineItemHandler;
import princess.tinkersenergistics.library.ModInfo;
import slimeknights.tconstruct.library.Util;

public class GuiMachine extends GuiContainer
	{
	private static final ResourceLocation	background	= new ResourceLocation(ModInfo.MODID, "textures/gui/machine.png");
	TileMachine								tile;
	
	public GuiMachine(TileMachine tile, Container inventory)
		{
		super(inventory);
		this.tile = tile;
		xSize = 176;
		ySize = 174;
		}
		
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
		{
		this.fontRenderer.drawString(this.tile.getName(), 8, 6, 4210752);
		this.fontRenderer.drawString(Util.translate(Minecraft.getMinecraft().player.inventory.getName()), 8, this.ySize - 96 + 2, 4210752);
		}
		
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
		{
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
		}
		
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
		{
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawSlots();
		drawProgressBars();
		}
		
	private void drawProgressBars()
		{
		int cookTime = tile.cookTime == 0 ? 1 : tile.cookTime;
		int fireTicks = tile.fireTicks;
		int fuelTicks = tile.fuelTicks;
		int fuelTicksMax = tile.fuelTicksMax == 0 ? 1 : tile.fuelTicksMax;
		
		int fuelType = tile.fluidPowered ? 1 : tile.energyPowered ? 2 : 0;
		
		int type = tile.type;
		
		int x = 21 + guiLeft - 1 + 18 * 3 + 7;
		int y = 21 + guiTop - 1 + 18 + 2;
		
		//First off, a machine fuel background. A simple quad.
		drawTexturedModalRect(x, y, 18 * fuelType + 2, 212, 14, 14);
		
		//Then we render remaining fuel over it. As a fraction of the maximum fuel value
		int fraction = (fuelTicks * 14 / fuelTicksMax);
		drawTexturedModalRect(x, y + 14 - fraction, 18 * fuelType + 2, 194 + 14 - fraction, 14, fraction);
		
		//Now, to the hard part.
		x -= 2;
		y -= 19;
		int completeness = (fireTicks * 18 / cookTime);
		drawTexturedModalRect(x, y, 18 * type, 176, completeness, 14);
		}
		
	private void drawSlots()
		{
		MachineItemHandler itemHandler = (MachineItemHandler) this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		int x = 21 + guiLeft - 1;
		int y = 21 + guiTop - 1;
		
		// inputs
		switch (itemHandler.inputSlotLimit)
			{
			case 1:
				drawTexturedModalRect(x, y, 176, 0, 18, 18);
				break;
			case 2:
				drawTexturedModalRect(x, y, 176, 18, 36, 18);
				break;
			case 3:
				drawTexturedModalRect(x, y, 176, 36, 54, 18);
				break;
			case 4:
			case 5:
			case 6:
				drawTexturedModalRect(x, y, 176, 54, 54, 36);
				break;
			default:
				drawTexturedModalRect(x, y, 176, 90, 54, 54);
				break;
			}
			
		x += 18 * 3 + 28;
		// outputs
		switch (itemHandler.outputSlotLimit)
			{
			case 1:
				drawTexturedModalRect(x, y, 176, 0, 18, 18);
				break;
			case 2:
				drawTexturedModalRect(x, y, 176, 18, 36, 18);
				break;
			case 3:
				drawTexturedModalRect(x, y, 176, 36, 54, 18);
				break;
			case 4:
			case 5:
			case 6:
				drawTexturedModalRect(x, y, 176, 54, 54, 36);
				break;
			default:
				drawTexturedModalRect(x, y, 176, 90, 54, 54);
				break;
			}
		}
	}
