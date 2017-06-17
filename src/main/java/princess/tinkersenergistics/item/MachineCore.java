package princess.tinkersenergistics.item;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import princess.tinkersenergistics.TinkersEnergistics;
import princess.tinkersenergistics.block.tile.TileMachine;
import princess.tinkersenergistics.library.MachineNBT;
import princess.tinkersenergistics.library.MachineTags;
import princess.tinkersenergistics.library.StatHelper;
import princess.tinkersenergistics.modifiers.MachineTrait;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;

public abstract class MachineCore extends ToolCore
	{
	Block	block	= TinkersEnergistics.machineBlock;
	int		type;
	
	public MachineCore(int type, PartMaterialType... requiredComponents)
		{
		super(requiredComponents);
		this.type = type;
		addCategory(TinkersEnergistics.TIE_MACHINE);
		}
		
	protected MachineNBT buildDefaultTag(List<Material> materials, boolean isMachine)
		{
		MachineNBT data = new MachineNBT();
		
		data.type(type);
		
		if (materials.size() >= 2)
			{
			HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(MaterialTypes.HANDLE);
			HeadMaterialStats head = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
			// start with head
			data.head(head);
			
			// add in accessoires if present
			if (materials.size() >= 3)
				{
				ExtraMaterialStats binding = materials.get(2).getStatsOrUnknown(MaterialTypes.EXTRA);
				data.extra(binding);
				}
				
			// calculate handle impact
			data.handle(handle);
			}
			
		// 3 free modifiers
		data.modifiers = DEFAULT_MODIFIERS;
		
		return data;
		}
		
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
		{
		boolean shift = Util.isShiftKeyDown();
		boolean ctrl = Util.isCtrlKeyDown();
		if (!shift && !ctrl)
			{
			if (!TagUtil.getTagSafe(stack).getCompoundTag(slimeknights.tconstruct.library.utils.Tags.TOOL_DATA).getBoolean(MachineTags.POWERED)) tooltip.add("" + TextFormatting.DARK_RED + TextFormatting.BOLD + Util.translate("tooltip.machine.unpowered"));
			
			TooltipBuilder.addModifierTooltips(stack, tooltip);
			
			tooltip.add("");
			tooltip.add(Util.translate("tooltip.tool.holdShift"));
			tooltip.add(Util.translate("tooltip.tool.holdCtrl"));
			}
		else
			if (Config.extraTooltips && shift)
				{
				getTooltipDetailed(stack, tooltip);
				}
			else
				if (Config.extraTooltips && ctrl)
					{
					getTooltipComponents(stack, tooltip);
					}
		}
		
	@Override
	public List<String> getInformation(ItemStack stack, boolean detailed)
		{
		LinkedList<String> info = Lists.newLinkedList();
		
		NBTTagCompound tag = TagUtil.getTagSafe(stack).getCompoundTag(slimeknights.tconstruct.library.utils.Tags.TOOL_DATA);
		
		int cookTime = 1000;
		int speedMultiplier = 1;
		int fuelMultiplier = 5;
		
		cookTime = tag.getInteger(MachineTags.COOK_TIME);
		speedMultiplier = (int) Math.round(Math.ceil(tag.getFloat(MachineTags.SPEED_MULTIPLIER)));
		fuelMultiplier = (int) Math.round(Math.ceil(tag.getFloat(MachineTags.FUEL_MULTIPLIER)));
		
		info.add(StatHelper.formatCookTime(cookTime, type));
		info.add(StatHelper.formatSpeedMultiplier(speedMultiplier, type));
		info.add(StatHelper.formatFuelMultiplier(fuelMultiplier, type));
		
		if (ToolHelper.getFreeModifiers(stack) > 0)
			{
			info.add(String.format("%s: %d", I18n.translateToLocal(TooltipBuilder.LOC_FreeModifiers), ToolHelper.getFreeModifiers(stack)));
			}
			
		info.addAll(addModifierInfo(stack));
		
		return info;
		}
		
	@Override
	public void getTooltipComponents(ItemStack stack, List<String> tooltips)
		{
		List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
		List<PartMaterialType> component = getRequiredComponents();
		
		if (materials.size() < component.size()) { return; }
		
		for (int i = 0; i < component.size(); i++)
			{
			PartMaterialType pmt = component.get(i);
			Material material = materials.get(i);
			
			Iterator<IToolPart> partIter = pmt.getPossibleParts().iterator();
			if (!partIter.hasNext())
				{
				continue;
				}
				
			IToolPart part = partIter.next();
			ItemStack partStack = part.getItemstackWithMaterial(material);
			if (partStack != null)
				{
				tooltips.add(material.getTextColor() + TextFormatting.UNDERLINE + partStack.getDisplayName());
				
				for (IMaterialStats stat : material.getAllStats())
					{
					if (pmt.usesStat(stat.getIdentifier()))
						{
						List<String> text = StatHelper.getLocalizedInfo(stat, type);
						if (!text.isEmpty())
							{
							tooltips.addAll(StatHelper.getLocalizedInfo(stat, type));
							}
						}
					}
				tooltips.add("");
				}
			}
		}
		
	/** We're not a tool, so no default traits allowed. */
	@Override
	public void addMaterialTraits(NBTTagCompound root, List<Material> materials)
		{
		MachineTrait.INSTANCE.apply(root);
		}
		
	@Override
	public NBTTagCompound buildTag(List<Material> materials)
		{
		NBTTagCompound tag = buildDefaultTag(materials, true).get();
		return tag;
		}
		
	@Nonnull
	@Override
	public EnumRarity getRarity(ItemStack stack)
		{
		return TinkersEnergistics.SKAIAN;
		}
		
	// Not a weapon
	@Override
	public float damagePotential()
		{
		return 0f;
		}
		
	public float damageCutoff()
		{
		return 0.0f;
		}
		
	@Override
	public double attackSpeed()
		{
		return 4;
		}
		
	public void reduceDurabilityOnHit(ItemStack stack, EntityPlayer player, float damage)
		{}
		
	private LinkedList<String> addModifierInfo(ItemStack stack)
		{
		NBTTagList tagList = TagUtil.getModifiersTagList(stack);
		LinkedList<String> tips = Lists.newLinkedList();
		for (int i = 0; i < tagList.tagCount(); i++)
			{
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			ModifierNBT data = ModifierNBT.readTag(tag);
			
			// get matching modifier
			IModifier modifier = TinkerRegistry.getModifier(data.identifier);
			if (modifier == null || modifier.isHidden())
				{
				continue;
				}
				
			for (String string : modifier.getExtraInfo(stack, tag))
				{
				if (!string.isEmpty())
					{
					tips.add(data.getColorString() + string);
					}
				}
			}
			
		return tips;
		}
		
	//ok, time to copy ItemBlock, guys.
	
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
		{
		NBTTagCompound tag = TagUtil.getTagSafe(stack).getCompoundTag(slimeknights.tconstruct.library.utils.Tags.TOOL_DATA);
		
		if (!tag.getBoolean("Powered")) return EnumActionResult.FAIL;
		
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		
		if (!block.isReplaceable(worldIn, pos))
			{
			pos = pos.offset(facing);
			}
			
		if (stack.stackSize != 0 && playerIn.canPlayerEdit(pos, facing, stack) && worldIn.canBlockBePlaced(this.block, pos, false, facing, (Entity) null, stack))
			{
			int i = getMetadata(stack.getMetadata());
			IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, playerIn, stack);
			
			if (placeBlockAt(stack, playerIn, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
				{
				SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
				worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				--stack.stackSize;
				}
				
			return EnumActionResult.SUCCESS;
			}
		else
			{
			return EnumActionResult.FAIL;
			}
		}
		
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
		{
		if (!world.setBlockState(pos, newState, 3)) return false;
		
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == block)
			{
			setTileEntityNBT(world, player, pos, stack.copy());
			block.onBlockPlacedBy(world, pos, state, player, stack);
			}
			
		return true;
		}
		
	private void setTileEntityNBT(World world, EntityPlayer player, BlockPos pos, ItemStack stack)
		{
		TileMachine tile = (TileMachine) world.getTileEntity(pos);
		
		tile.setParentItem(stack);
		}
	}
