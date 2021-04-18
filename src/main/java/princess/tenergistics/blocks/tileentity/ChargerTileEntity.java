package princess.tenergistics.blocks.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.container.ChargerContainer;
import slimeknights.mantle.tileentity.InventoryTileEntity;

public class ChargerTileEntity extends InventoryTileEntity implements ITickableTileEntity
	{
	private static final int				CAPACITY	= 10000;
	
	private static final String				TAG_ENERGY	= "energy";
	
	private static final IEnergyStorage		EMPTY		= new EnergyStorage(0);
	
	protected IEnergyStorage				energy;
	protected LazyOptional<IEnergyStorage>	energyCap;
	
	public IIntArray						energyArray	= new IIntArray()
															{
															public int get(int index)
																{
																return index == 0 ? energy.getEnergyStored() : 0;
																}
																
															public void set(int index, int value)
																{
																if (index == 0)
																	{
																	setEnergy(value);
																	}
																}
																
															public int size()
																{
																return 1;
																}
															};
	
	public ChargerTileEntity()
		{
		super(TEnergistics.chargerTile.get(), new TranslationTextComponent("gui.tenergistics.charger"), 1, 1);
		
		energy = new EnergyStorage(CAPACITY);
		energyCap = LazyOptional.of(() -> energy);
		}
		
	@Override
	public void tick()
		{
		if (world == null || world.isRemote || world.getGameTime() % 5 != 0)
			{ return; }
			
		ItemStack item = itemHandler.getStackInSlot(0);
		
		// insanity check
		if (!item.isEmpty() && item.getCapability(CapabilityEnergy.ENERGY).isPresent())
			{
			IEnergyStorage cap = item.getCapability(CapabilityEnergy.ENERGY).orElse(EMPTY);
			if (cap.canReceive() && cap.getMaxEnergyStored() > 0)
				{
				item = itemHandler.extractItem(0, 1, false);
				cap = item.getCapability(CapabilityEnergy.ENERGY).orElse(EMPTY);
				energy.extractEnergy(cap.receiveEnergy(energy.getEnergyStored(), false), false);
				itemHandler.insertItem(0, item, false);
				}
			}
		}
		
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
		{
		return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
		}
		
	@Override
	public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity)
		{
		return new ChargerContainer(menuId, playerInventory, this);
		}
		
	@Override
	public <C> LazyOptional<C> getCapability(Capability<C> capability, @Nullable Direction facing)
		{
		if (capability == CapabilityEnergy.ENERGY)
			{ return energyCap.cast(); }
		return super.getCapability(capability, facing);
		}
		
	protected void invalidateCaps()
		{
		super.invalidateCaps();
		this.energyCap.invalidate();
		}
		
	@Override
	public void read(BlockState blockState, CompoundNBT tags)
		{
		super.read(blockState, tags);
		setEnergy(tags.getInt(TAG_ENERGY));
		}
		
	@Override
	public void writeSynced(CompoundNBT tags)
		{
		super.writeSynced(tags);
		tags.putInt(TAG_ENERGY, energy.getEnergyStored());
		}
		
	@Override
	public CompoundNBT write(CompoundNBT tags)
		{
		super.write(tags);
		tags.putInt(TAG_ENERGY, energy.getEnergyStored());
		return tags;
		}
		
	private void setEnergy(int energySet)
		{
		int energyIn = energySet - energy.getEnergyStored();
		if (energyIn >= 0)
			{
			energy.receiveEnergy(energyIn, false);
			}
		else
			{
			energy.extractEnergy(-energyIn, false);
			}
		}
	}
