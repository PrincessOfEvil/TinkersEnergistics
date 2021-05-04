package princess.tenergistics.modifiers;

import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.tools.PoweredTool;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class WideFunnelModifier extends SingleUseModifier
	{
	public WideFunnelModifier()
		{
		super(0xc8bfe7);
		}
		
	@Override
	public ActionResultType onToolUse(IModifierToolStack tool, int level, World world, PlayerEntity player, Hand hand)
		{
		FluidStack fluidStack = PoweredTool.getFluidStack(tool);
		if (PoweredTool.getFluidCapacity(tool) - fluidStack.getAmount() < FluidAttributes.BUCKET_VOLUME)
			{ return ActionResultType.PASS; }
			
		BlockRayTraceResult trace = ToolCore.blockRayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
		if (trace.getType() != Type.BLOCK)
			{ return ActionResultType.PASS; }
		Direction face = trace.getFace();
		BlockPos target = trace.getPos();
		BlockPos offset = target.offset(face);
		if (!world.isBlockModifiable(player, target) || !player.canPlayerEdit(offset, face, player.getHeldItem(hand)))
			{ return ActionResultType.PASS; }
			
		FluidState fluidState = world.getFluidState(target);
		Fluid currentFluid = fluidStack.getFluid();
		if (fluidState.isEmpty()
				|| !TEnergistics.exchangerModifier.get().tank
						.isFluidValid(tool, 0, 0, new FluidStack(fluidState.getFluid(), 1))
				|| (!fluidStack.isEmpty() && !currentFluid.isEquivalentTo(fluidState.getFluid())))
			{ return ActionResultType.PASS; }
			
		BlockState state = world.getBlockState(target);
		if (state.getBlock() instanceof IBucketPickupHandler)
			{
			Fluid pickedUpFluid = ((IBucketPickupHandler) state.getBlock()).pickupFluid(world, target, state);
			if (pickedUpFluid != Fluids.EMPTY)
				{
				player.playSound(pickedUpFluid.getAttributes().getFillSound(fluidStack), 1.0F, 1.0F);
				if (!world.isRemote)
					{
					if (fluidStack.isEmpty())
						{
						PoweredTool.setFluidStack(tool, new FluidStack(pickedUpFluid, FluidAttributes.BUCKET_VOLUME));
						}
					else
						if (pickedUpFluid == currentFluid)
							{
							fluidStack.grow(FluidAttributes.BUCKET_VOLUME);
							PoweredTool.setFluidStack(tool, fluidStack);
							}
						else
							{
							TEnergistics.log
									.error("Picked up a fluid {} that does not match the current fluid state {}, this should not happen", pickedUpFluid, fluidState
											.getFluid());
							}
					}
				return ActionResultType.SUCCESS;
				}
			}
		return ActionResultType.PASS;
		}
		
	}
