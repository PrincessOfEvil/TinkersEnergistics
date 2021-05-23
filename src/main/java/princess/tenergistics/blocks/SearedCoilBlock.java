package princess.tenergistics.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemHandlerHelper;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.blocks.tileentity.SearedCoilTileEntity;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

public class SearedCoilBlock extends SearedBlock
	{
	public SearedCoilBlock(Properties properties)
		{
		super(properties);
		}
		
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
		{
		return new SearedCoilTileEntity();
		}
		
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
		{
		if (interactWithBattery(world, pos, player, hand, hit))
			{
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof SearedCoilTileEntity)
				{
				((SearedCoilTileEntity) tile).onContentsChanged();
				}
				
			return ActionResultType.SUCCESS;
			}
		return super.onBlockActivated(state, world, pos, player, hand, hit);
		}
		
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
		{
		CompoundNBT nbt = stack.getTag();
		if (nbt != null)
			{
			TileEntityHelper.getTile(SearedCoilTileEntity.class, worldIn, pos)
					.ifPresent(te -> te.setEnergy(nbt.getInt(SearedCoilTileEntity.TAG_ENERGY)));
			}
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		}
		
	private static final String	KEY_DRAINED	= "block.tenergistics.energy.drained";
	private static final String	KEY_FILLED	= "block.tenergistics.energy.filled";
	
	public static boolean interactWithBattery(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
		{
		ItemStack stack = player.getHeldItem(hand);
		Direction face = hit.getFace();
		if (stack.getCapability(CapabilityEnergy.ENERGY).isPresent())
			{
			// only server needs to transfer stuff
			if (!world.isRemote)
				{
				TileEntity te = world.getTileEntity(pos);
				if (te != null)
					{
					LazyOptional<IEnergyStorage> teCapability = te.getCapability(CapabilityEnergy.ENERGY, face);
					if (teCapability.isPresent())
						{
						IEnergyStorage teHandler = teCapability.orElse(SearedCoilTileEntity.EMPTY);
						
						ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
						copy.getCapability(CapabilityEnergy.ENERGY).ifPresent(itemHandler -> {
						
						int transferred = tryTransfer(itemHandler, teHandler, Integer.MAX_VALUE);
						if (transferred > 0)
							{
							world.playSound(null, pos, TEnergistics.moltenEnergy.get()
									.getAttributes()
									.getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
							
							player.sendStatusMessage(new TranslationTextComponent(KEY_FILLED, transferred), true);
							}
						else
							{
							transferred = tryTransfer(teHandler, itemHandler, Integer.MAX_VALUE);
							if (transferred > 0)
								{
								world.playSound(null, pos, TEnergistics.moltenEnergy.get()
										.getAttributes()
										.getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
								player.sendStatusMessage(new TranslationTextComponent(KEY_DRAINED, transferred), true);
								}
							}
							
						if (transferred > 0)
							{
							player.setHeldItem(hand, DrinkHelper.fill(stack, player, copy));
							}
						});
						}
					}
				}
		      return true;
			}
		return false;
		}
		
	public static int tryTransfer(IEnergyStorage input, IEnergyStorage output, int maxFill)
		{
		int simulated = input.extractEnergy(maxFill, true);
		if (simulated > 0)
			{
			int simulatedFill = output.receiveEnergy(simulated, true);
			if (simulatedFill > 0)
				{
				int drainedEnergy = input.extractEnergy(simulatedFill, false);
				if (drainedEnergy > 0)
					{
					int actualFill = output.receiveEnergy(drainedEnergy, false);
					if (actualFill != drainedEnergy)
						{
						TEnergistics.log.error("Lost {} energy during transfer", drainedEnergy - actualFill);
						}
					}
				return drainedEnergy;
				}
			}
		return 0;
		}
	}