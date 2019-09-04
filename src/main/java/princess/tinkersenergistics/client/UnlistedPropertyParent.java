package princess.tinkersenergistics.client;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IUnlistedProperty;
import princess.tinkersenergistics.item.MachineCore;

public class UnlistedPropertyParent implements IUnlistedProperty<ItemStack>
	{
	private final String name;
	
	public UnlistedPropertyParent(String name)
		{
		this.name = name;
		}
		
	@Override
	public String getName()
		{
		return name;
		}
		
	@Override
	public boolean isValid(ItemStack value)
		{
		return value.getItem() instanceof MachineCore;
		}
		
	@Override
	public Class<ItemStack> getType()
		{
		return ItemStack.class;
		}
		
	@Override
	public String valueToString(ItemStack value)
		{
		return value.toString();
		}
	}