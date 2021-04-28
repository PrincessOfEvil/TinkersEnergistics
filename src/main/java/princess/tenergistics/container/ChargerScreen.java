package princess.tenergistics.container;

import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.library.client.GuiUtil;

public class ChargerScreen extends ContainerScreen<ChargerContainer>
	{
	private static final ResourceLocation	BACKGROUND	= new ResourceLocation(TEnergistics.modID, "textures/gui/charger.png");
	private static final float				SCALE		= 2.0f;
	
	private LazyOptional<IEnergyStorage>	energy;
	
	public ChargerScreen(ChargerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
		{
		super(screenContainer, inv, titleIn);
		energy = this.container.getTile().getCapability(CapabilityEnergy.ENERGY);
		this.ySize = 133;
		this.playerInventoryTitleY = this.ySize - 94;
		}
		
	@Override
	public void render(MatrixStack matrices, int x, int y, float partialTicks)
		{
		this.renderBackground(matrices);
		super.render(matrices, x, y, partialTicks);
		this.renderHoveredTooltip(matrices, x, y);
		}
		
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
		{
		GuiUtil.drawBackground(matrixStack, this, BACKGROUND);
		Optional<Integer> energyOut = energy.map(IEnergyStorage::getEnergyStored);
		if (energyOut.isPresent())
			{
			
			FontRenderer fontRenderer = this.minecraft.fontRenderer;
			
			matrixStack.push();
			
			matrixStack.scale(SCALE, SCALE, SCALE);
			fontRenderer.drawStringWithShadow(matrixStack, energyOut.get()
					.toString(), this.width / 6, this.height / 5 - 11, 0xee0e0e);
			
			matrixStack.pop();
			}
		}
	}
