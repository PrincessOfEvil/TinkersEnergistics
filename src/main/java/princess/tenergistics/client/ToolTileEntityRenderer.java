package princess.tenergistics.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.vector.Vector3f;
import princess.tenergistics.blocks.tileentity.PlacedToolTileEntity;

public class ToolTileEntityRenderer<T extends PlacedToolTileEntity> extends TileEntityRenderer<T>
	{
	private static Class<?>			largeModel	= null;
	
	public ToolTileEntityRenderer(TileEntityRendererDispatcher dispatcher)
		{
		super(dispatcher);
		
		try
			{
			largeModel = Class
					.forName("slimeknights.tconstruct.library.client.model.tools.ToolModel$BakedLargeToolModel");
			}
		catch (ClassNotFoundException ex)
			{}
			
		}
		
	@Override
	public void render(PlacedToolTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
		{
		BlockState state = tile.getBlockState();
		ItemStack tool = tile.getTool();
		
		matrix.push();
		
		matrix.translate(0.5, 0, 0.5);
		matrix.rotate(Vector3f.YP.rotationDegrees(-90f * (state.get(BlockStateProperties.HORIZONTAL_FACING)
				.rotateYCCW()
				.getHorizontalIndex())));
		matrix.translate(-0.5, 0, -0.5);
		
		//center the item
		matrix.translate(0.5, 0.5, 0.5);
		
		matrix.rotate(Vector3f.ZP.rotationDegrees(90f * (2 - state.get(BlockStateProperties.FACE).ordinal())));
		
		if (tile.bitesIn())
			{
			boolean large = false;
			
			if (largeModel != null && largeModel.isInstance(Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(tool, null, null)))
				{
				large = true;
				}
			
			//shift it slightly to have it bite into the block
			matrix.translate(1d / 128, 9d / 128, 0);
			
			if (large)
				{
				matrix.translate(-3d / 16, -3d / 16, 0);
				}
				
			if (state.get(BlockStateProperties.FACE) == AttachFace.FLOOR)
				{
				if (large)
					{
					matrix.translate(3d / 16, 0, 0);
					}
					
				matrix.rotate(Vector3f.ZP.rotationDegrees(45f));
				}
			}
		
		//Fix the sprite: FIXED transform is weird.
		matrix.rotate(Vector3f.ZP.rotationDegrees(90f));
		
		matrix.rotate(Vector3f.ZP.rotationDegrees(45f));
		matrix.rotate(Vector3f.XP.rotationDegrees(180f));
		matrix.rotate(Vector3f.ZP.rotationDegrees(-45f));
		
	    Minecraft.getInstance().getItemRenderer().renderItem(tool, TransformType.FIXED, combinedLight, OverlayTexture.NO_OVERLAY, matrix, buffer);
		
		matrix.pop();
		}
		
	public boolean isGlobalRenderer(T te)
		{
		return true;
		}
	}
