package princess.tinkersenergistics.library;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import princess.tinkersenergistics.TEnergistics;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;

public class MachineCore extends ToolCore
	{
	
	public MachineCore(PartMaterialType... requiredComponents)
		{
		super(requiredComponents);
	    addCategory(TEnergistics.TIE_MACHINE);
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
		return 0.05f;
		}
	@Override
	public double attackSpeed()
		{
		return 4d;
		}
	}
