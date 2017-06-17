package princess.tinkersenergistics.modifiers;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.IModifierDisplay;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolPart;

public class ModMachinePartDisplay extends Modifier implements IModifierDisplay
	{
	private ToolPart	item;
	private String		type;
	
	public ModMachinePartDisplay(String type, ToolPart item)
		{
		super(type);
		
		this.type = type;
		this.item = item;
		}
		
	@Override
	public boolean hasTexturePerMaterial()
		{
		return true;
		}
		
	@Override
	public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag)
		{
		// dummy
		}
		
	@Override
	public int getColor()
		{
		return 0xCCCCCC;
		}
		
	@Override
	public List<List<ItemStack>> getItems()
		{
		ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();
		for (IModifier modifier : TinkerRegistry.getAllModifiers())
			{
			if (!(modifier instanceof ModMachinePart))
				{
				continue;
				}
				
			if (!((ModMachinePart) modifier).type.equals(type))
				{
				continue;
				}
			ItemStack kit = item.getItemstackWithMaterial(((ModMachinePart) modifier).material);
			ItemStack flint = new ItemStack(Items.COAL);
			
			builder.add(ImmutableList.<ItemStack> of(kit, flint));
			}
			
		return builder.build();
		}
	}