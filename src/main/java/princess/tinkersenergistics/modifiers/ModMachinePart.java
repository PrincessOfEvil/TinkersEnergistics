package princess.tinkersenergistics.modifiers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import princess.tinkersenergistics.ConfigHandler;
import princess.tinkersenergistics.TinkersEnergistics;
import princess.tinkersenergistics.library.MachineNBT;
import princess.tinkersenergistics.library.MachineTags;
import princess.tinkersenergistics.library.StatHelper;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerAPIException;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.tools.modifiers.ToolModifier;

public class ModMachinePart extends ToolModifier implements IMachineMod
	{
	public final Material		material;
	public final String			type;
	
	public ModMachinePart(Material material, String type, ToolPart item)
		{
		super(type + material.getIdentifier(), material.materialTextColor);
		
		if (!material.hasStats(MaterialTypes.HEAD)) { throw new TinkerAPIException(String.format("Trying to add a material modifier for a material without tool stats: %s", material.getIdentifier())); }
		
		this.material = material;
		this.type = type;
		
		addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this));
		
		ItemStack kit = item.getItemstackWithMaterial(material);
	    ItemStack flint = new ItemStack(Items.COAL);
	    addRecipeMatch(new RecipeMatch.ItemCombination(1, kit, flint));
		}
		
	@Override
	public String getLocalizedName()
		{
		return Util.translate(LOC_Name, type) + " (" + material.getLocalizedName() + ")";
		}
		
	@Override
	public String getLocalizedDesc()
		{
		return Util.translateFormatted(String.format(LOC_Desc, type), material.getLocalizedName());
		}
		
	@Override
	public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag)
		{
		NBTTagCompound tag = TagUtil.getToolTag(rootCompound);
		tag.setBoolean(MachineTags.POWERED, true);
		HeadMaterialStats stats = material.getStats(MaterialTypes.HEAD);
		//We're replacing.
		NBTTagList tagList = TagUtil.getModifiersTagList(rootCompound);
		NBTTagList tagList2 = TagUtil.getTagListSafe(TagUtil.getTagSafe(rootCompound, Tags.BASE_DATA), Tags.TOOL_MODIFIERS, TagUtil.TAG_TYPE_STRING);
		
		for (int i = 0; i < tagList.tagCount(); i++)
			{
			NBTTagCompound mod = tagList.getCompoundTagAt(i);
			ModifierNBT data = ModifierNBT.readTag(mod);
			
			// remove other occurences
			if (data.identifier.equals(this.identifier))
				{
				break;
				}
				
			if (data.identifier.startsWith(type))
				{
				tagList.removeTag(i);
				tagList2.removeTag(i);
				i--;
				
				if (type == "firebox")
					{
					MachineNBT data1 = new MachineNBT(tag);
					
					data1.speedMultiplier = StatHelper.speedMultiplier(data1.speedMultiplier, stats, true);
					data1.fuelMultiplier = StatHelper.fuelMultiplier(data1.fuelMultiplier, stats, true);
					
					TagUtil.setToolTag(rootCompound, data1.get());
					}
					
				tag.setInteger(MachineTags.TANK, 0);
				tag.setInteger(MachineTags.ENERGY_STORAGE, 0);
				}
			}
			
		switch (type)
			{
			case "firebox":
				{
				MachineNBT data = new MachineNBT(tag);
				data.speedMultiplier = StatHelper.speedMultiplier(data.speedMultiplier, stats, false);
				data.fuelMultiplier = StatHelper.fuelMultiplier(data.fuelMultiplier, stats, false);
				TagUtil.setToolTag(rootCompound, data.get());
				break;
				}
			case "exchanger":
				{
				tag.setInteger(MachineTags.TANK, Math.round(stats.attack * ConfigHandler.tankCapacityPerAttack));
				break;
				}
			case "coil":
				{
				tag.setInteger(MachineTags.ENERGY_STORAGE, Math.round(stats.attack * ConfigHandler.energyCapacityPerAttack));
				break;
				}
			}
			
		TagUtil.setModifiersTagList(rootCompound, tagList);
		}
		
	@Override
	protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException
		{
		return ((ToolCore) stack.getItem()).hasCategory(TinkersEnergistics.TIE_MACHINE);
		}
		
	@Override
	public boolean hasTexturePerMaterial()
		{
		return true;
		}
	}
