package princess.tenergistics.hax;

import static slimeknights.tconstruct.smeltery.block.FaucetBlock.FACING;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.capability.IFluidHandler;
import princess.tenergistics.blocks.tileentity.ScrewPumpTileEntity;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

public class EnergisticsHacksTiCBecauseKMIsDumm
	{
	//last code in this class: 12.06.21
	
	@Mixin(FaucetTileEntity.class)
	public static abstract class TileFaucetMixin extends MantleTileEntity
		{
		public TileFaucetMixin(TileEntityType<?> tileEntityTypeIn)
			{
			super(tileEntityTypeIn);
			}
			
		@Shadow(remap = false)
		private LazyOptional<IFluidHandler>							inputHandler;
		@Shadow(remap = false)
		private LazyOptional<IFluidHandler>							outputHandler;
		@Shadow(remap = false)
		private final NonNullConsumer<LazyOptional<IFluidHandler>>	inputListener	= null;
		@Shadow(remap = false)
		private final NonNullConsumer<LazyOptional<IFluidHandler>>	outputListener	= null;
		
		@Shadow(remap = false)
		private LazyOptional<IFluidHandler> findFluidHandler(Direction side)
			{
			return null;
			}
			
		@Inject(at = @At("HEAD"), method = "getInputHandler()Lnet/minecraftforge/common/util/LazyOptional;", remap = false)
		private void getInputHandler(CallbackInfoReturnable<LazyOptional<IFluidHandler>> ci)
			{
			if (isMixinValid() && inputHandler == null)
				{
				inputHandler = findFluidHandler(Direction.DOWN);
				if (inputHandler.isPresent())
					{
					inputHandler.addListener(inputListener);
					}
				}
			}
			
		@Inject(at = @At("HEAD"), method = "getOutputHandler()Lnet/minecraftforge/common/util/LazyOptional;", remap = false)
		private void getOutputHandler(CallbackInfoReturnable<LazyOptional<IFluidHandler>> ci)
			{
			if (isMixinValid() && outputHandler == null)
				{
				outputHandler = findFluidHandler(getBlockState().get(FACING).getOpposite());
				if (outputHandler.isPresent())
					{
					outputHandler.addListener(outputListener);
					}
				}
			}
			
		public void neighborChanged(BlockPos neighbor)
			{
			if (isMixinValid())
				{
				if (pos.equals(neighbor.up()))
					{
					inputHandler = null;
					}
				else
					if (pos.equals(neighbor.offset(getBlockState().get(FACING))))
						{
						outputHandler = null;
						}
				}
			else
				{
				if (pos.equals(neighbor.up()))
					{
					outputHandler = null;
					}
				else
					if (pos.equals(neighbor.offset(getBlockState().get(FACING))))
						{
						inputHandler = null;
						}
				}
			}
			
		private boolean isMixinValid()
			{
			return world != null && world.getTileEntity(pos) instanceof ScrewPumpTileEntity;
			}
		}
	}
