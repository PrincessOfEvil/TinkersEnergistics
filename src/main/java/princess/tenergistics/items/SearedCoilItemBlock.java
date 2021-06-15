package princess.tenergistics.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.blocks.tileentity.SearedCoilTileEntity;
import princess.tenergistics.capabilities.CoilItemEnergyCapability;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.tconstruct.library.Util;

public class SearedCoilItemBlock extends BlockTooltipItem
	{
	private static final String	KEY_ENERGY	= Util
			.makeTranslationKey("block", new ResourceLocation(TEnergistics.modID, "energy"));
	
	private final boolean		limitStackSize;
	
	public SearedCoilItemBlock(Block blockIn, Properties builder, boolean limitStackSize)
		{
		super(blockIn, builder);
		this.limitStackSize = limitStackSize;
		}
		
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
		{
		return new CoilItemEnergyCapability(stack);
		}
		
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
		{
		if (stack.hasTag() && getEnergy(stack) > 0)
			{
			tooltip.add(new TranslationTextComponent(KEY_ENERGY, getEnergy(stack)).mergeStyle(TextFormatting.GRAY));
			}
		else
			{
			super.addInformation(stack, worldIn, tooltip, flagIn);
			}
		}
		
	@Override
	public int getItemStackLimit(ItemStack stack)
		{
		if (!limitStackSize)
			{ return super.getItemStackLimit(stack); }
		return getEnergy(stack) == 0 ? 64 : 16;
		}
		
	public static int getEnergy(ItemStack stack)
		{
		if (stack.hasTag())
			{
			assert stack.getTag() != null;
			return stack.getTag().getInt(SearedCoilTileEntity.TAG_ENERGY);
			}
		return 0;
		}
		
	public static void setEnergy(ItemStack stack, int energy)
		{
		if (stack.hasTag())
			{
			assert stack.getTag() != null;
			stack.getTag().putInt(SearedCoilTileEntity.TAG_ENERGY, energy);
			}
		else
			{
			CompoundNBT tag = new CompoundNBT();
			tag.putInt(SearedCoilTileEntity.TAG_ENERGY, energy);
			stack.setTag(tag);
			}
		}
	}
