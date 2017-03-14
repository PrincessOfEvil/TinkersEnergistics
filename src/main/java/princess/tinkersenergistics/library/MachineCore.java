package princess.tinkersenergistics.library;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import princess.tinkersenergistics.TinkersEnergistics;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;

public class MachineCore extends ToolCore
	{
	
	public MachineCore(PartMaterialType... requiredComponents)
		{
		super(requiredComponents);
		addCategory(TinkersEnergistics.TIE_MACHINE);
		}
		
	@Override
	public NBTTagCompound buildTag(List<Material> materials)
		{
		return buildDefaultTag(materials).get();
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
	}
