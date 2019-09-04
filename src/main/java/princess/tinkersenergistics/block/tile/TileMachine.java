package princess.tinkersenergistics.block.tile;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import princess.tinkersenergistics.ConfigHandler;
import princess.tinkersenergistics.TinkersEnergistics;
import princess.tinkersenergistics.capability.MachineEnergyStorage;
import princess.tinkersenergistics.capability.MachineFluidTank;
import princess.tinkersenergistics.capability.MachineItemHandler;
import princess.tinkersenergistics.library.MachineRecipeHandler;
import princess.tinkersenergistics.library.MachineTags;
import princess.tinkersenergistics.library.OreDicHelper;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

//i_am_a_god.mp3
//holy duck this class needs some splitting up
//like i'm not even kidding if i make another machine i'm making a machine registry
public class TileMachine extends TileEntity implements ITickable
	{
	private ItemStack				parentItem			= ItemStack.EMPTY;
	
	/**How much time it takes to cook*/
	public int						cookTime			= 1000;
	/**Current progress, counts up*/
	public int						fireTicks			= 0;
	/**Consumed fuel, in fireticks*/
	public int						fuelTicks			= 0;
	/**Consumed fuel, in fireticks, for rendering*/
	public int						fuelTicksMax		= 1;
	/**fireTicks generated per tick*/
	public int						speedMultiplier		= 1;
	/**fuelTicks eaten per tick*/
	public int						fuelMultiplier		= 5;
	/**fuelTicks generated per fuel fueltick*/
	public int						fuelMultInternal	= 25;
	
	public int						craftMultiplier		= 0;
	
	public boolean					fluidPowered		= false;
	public boolean					energyPowered		= false;
	
	private MachineItemHandler		inventory;
	private MachineFluidTank		fuelTank;
	private MachineEnergyStorage	energyStorage;
	
	/**Type 0 is furnace, 1 is crusher and 2 is converter, right now.*/
	public int						type				= 0;
	
	private Random					random				= new Random(TinkersEnergistics.One.hashCode() * System.nanoTime());
	
	public TileMachine()
		{
		inventory = new MachineItemHandler(this);
		fuelTank = new MachineFluidTank(fluidPowered ? 4 : 0);
		energyStorage = new MachineEnergyStorage(energyPowered ? 4000 : 0);
		}
		
	@Override
	public void update()
		{
		if (!this.world.isRemote)
			{
			if (canStartCrafting())
				{
				if (fuelTicks <= 0)
					{
					consumeFuel();
					}
				if (fuelTicks > 0)
					{
					fireTicks += speedMultiplier;
					fuelTicks -= fuelMultiplier;
					}
				markDirty();
				}
			if (canCraft()) craft();
			}
		}
		
	/** THE method to replace.*/
	public ItemStack craftingResult(ItemStack stack)
		{
		switch (type)
			{
			case 1:
				return MachineRecipeHandler.getCrushingResult(stack);
			case 2:
				return ItemStack.EMPTY;
			default:
				return FurnaceRecipes.instance().getSmeltingResult(stack);
			}
		}
		
	// Method to balance
	private boolean consumeFuel()
		{
		if (fluidPowered)
			{
			FluidStack liquid = fuelTank.getFluid();
			if (liquid != null)
				{
				FluidStack in = liquid.copy();
				int amount = liquid.amount - in.amount;
				FluidStack drained = fuelTank.drain(amount, false);
				if (drained != null && drained.amount == amount)
					{
					fuelTank.drain(amount, true);
					int addition = TinkerRegistry.consumeSmelteryFuel(in) * ConfigHandler.fluidFuelMultiplier;
					fuelTicks += addition * fuelMultInternal;
					fuelTicksMax = addition * fuelMultInternal;
					return true;
					}
				}
			return false;
			}
		else
			if (energyPowered)
				{
				if (energyStorage.getEnergyStored() >= ConfigHandler.energyPerCraft)
					{
					energyStorage.extractEnergyProcessing(ConfigHandler.energyPerCraft, false);
					fuelTicks += ConfigHandler.fireTicksInTwoSticks * fuelMultInternal;
					fuelTicksMax = ConfigHandler.fireTicksInTwoSticks * fuelMultInternal;
					return true;
					}
				else
					if (energyStorage.getEnergyStored() >= ConfigHandler.energyPerNanoCraft)
						{
						energyStorage.extractEnergyProcessing(ConfigHandler.energyPerNanoCraft, false);
						fuelTicks += fuelMultInternal;
						fuelTicksMax = fuelMultInternal;
						return true;
						}
				return false;
				}
			else
				{
				ItemStack stack = inventory.extractItemFuel(1, false);
				if (stack.isEmpty()) return false;
				int addition = TileEntityFurnace.getItemBurnTime(stack);
				fuelTicks += addition * fuelMultInternal;
				fuelTicksMax = addition * fuelMultInternal;
				return true;
				}
		}
		
	private boolean canStartCrafting()
		{
		ItemStack item = inventory.extractItemProcessing(1, true);
		
		if (item.isEmpty()) return false;
		
		ItemStack stack = craftingResult(item);
		
		if (stack.isEmpty()) return false;
		if (inventory.insertItemProcessing(stack, true).isEmpty()) return true;
		
		return false;
		}
		
	private boolean canCraft()
		{
		if (fireTicks < cookTime) return false;
		return canStartCrafting();
		}
		
	private void craft()
		{
		fireTicks = 0;
		ItemStack item = inventory.extractItemProcessing(1, false);
		inventory.insertItemProcessing(craftingResult(item), false);
		
		if (random.nextInt(100) < craftMultiplier && OreDicHelper.isOre("ore", item)) inventory.insertItemProcessing(craftingResult(item), false);
		
		markDirty();
		}
		
	/* here come dat boi!! */
	
	public void readFromNBT(NBTTagCompound compound)
		{
		super.readFromNBT(compound);
		
		cookTime = compound.getInteger("CookTime");
		fireTicks = compound.getInteger("FireTicks");
		fuelTicks = compound.getInteger("FuelTicks");
		fuelTicksMax = compound.getInteger("FuelTicksMax");
		
		speedMultiplier = compound.getInteger("SpeedMultiplier");
		fuelMultiplier = compound.getInteger("FuelMultiplier");
		
		inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
		
		type = compound.getInteger("Type");
		
		setParentItem(new ItemStack(compound.getCompoundTag("ParentItem")));
		
		if (fluidPowered) fuelTank.readFromNBT((NBTTagCompound) compound.getTag("Tank"));
		if (energyPowered) energyStorage.receiveEnergy(compound.getInteger("Energy"), false);
		}
		
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
		{
		super.writeToNBT(compound);
		
		compound.setInteger("CookTime", cookTime);
		compound.setInteger("FireTicks", fireTicks);
		compound.setInteger("FuelTicks", fuelTicks);
		compound.setInteger("FuelTicksMax", fuelTicksMax);
		
		compound.setInteger("SpeedMultiplier", speedMultiplier);
		compound.setInteger("FuelMultiplier", fuelMultiplier);
		
		compound.setTag("Inventory", inventory.serializeNBT());
		if (fluidPowered) compound.setTag("Tank", fuelTank.writeToNBT(new NBTTagCompound()));
		if (energyPowered) compound.setInteger("Energy", energyStorage.getEnergyStored());
		
		compound.setInteger("Type", type);
		
		if (!parentItem.isEmpty()) compound.setTag("ParentItem", parentItem.writeToNBT(new NBTTagCompound()));
		
		return compound;
		}
		
	public NBTTagCompound getUpdateTag()
		{
		return writeToNBT(new NBTTagCompound());
		}
		
	public String getName()
		{
		return !parentItem.isEmpty() ? parentItem.getDisplayName() : "container.furnace";
		}
		
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fluidPowered) return true;
		if (capability == CapabilityEnergy.ENERGY && energyPowered) return true;
		return super.hasCapability(capability, facing);
		}
		
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) inventory;
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fluidPowered) return (T) fuelTank;
		if (capability == CapabilityEnergy.ENERGY && energyPowered) return (T) energyStorage;
		return super.getCapability(capability, facing);
		}
		
	public ItemStack getParentItem()
		{
		if (!parentItem.isEmpty()) return parentItem.copy();
		return ItemStack.EMPTY;
		}
		
	public void setParentItem(ItemStack parentItem)
		{
		if (parentItem.isEmpty()) return;
		this.parentItem = parentItem;
		
		NBTTagCompound tag = TagUtil.getTagSafe(parentItem).getCompoundTag(slimeknights.tconstruct.library.utils.Tags.TOOL_DATA);
		
		inventory.setInputSlotLimit(tag.getInteger(MachineTags.INPUT_SLOTS));
		inventory.setOutputSlotLimit(tag.getInteger(MachineTags.OUTPUT_SLOTS));
		cookTime = tag.getInteger(MachineTags.COOK_TIME);
		type = tag.getInteger(MachineTags.TYPE);
		speedMultiplier = (int) Math.round(Math.ceil(tag.getFloat(MachineTags.SPEED_MULTIPLIER)));
		fuelMultiplier = (int) Math.round(Math.ceil(tag.getFloat(MachineTags.FUEL_MULTIPLIER)));
		
		if (tag.getInteger(MachineTags.TANK) > 0)
			{
			fuelTank = new MachineFluidTank(tag.getInteger(MachineTags.TANK));
			fluidPowered = true;
			}
		if (tag.getInteger(MachineTags.ENERGY_STORAGE) > 0)
			{
			energyStorage = new MachineEnergyStorage(tag.getInteger(MachineTags.ENERGY_STORAGE));
			energyPowered = true;
			}
			
		NBTTagList tags = TagUtil.getModifiersTagList(parentItem);
		
		for (int i = 0; i < tags.tagCount(); i++)
			{
			NBTTagCompound tagged = tags.getCompoundTagAt(i);
			ModifierNBT data = ModifierNBT.readTag(tagged);
			
			if (data.identifier.equals("double"))
				{
				ModifierNBT.IntegerNBT modData = ModifierNBT.readInteger(tagged);
				craftMultiplier = modData.current * 2;
				break;
				}
			}
		markDirty();
		}
		
	public boolean canInteractWith(EntityPlayer playerIn)
		{
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
		}
		
	public MachineItemHandler getInventory()
		{
		return inventory;
		}
		
	//Guess where i got THAT from.
	public void setFacing(EnumFacing face)
		{
		getTileData().setInteger("Facing", face.getIndex());
		}
		
	public EnumFacing getFacing()
		{
		return EnumFacing.getFront(getTileData().getInteger("Facing"));
		}

    @SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
		{
		return INFINITE_EXTENT_AABB;
		}
	}
