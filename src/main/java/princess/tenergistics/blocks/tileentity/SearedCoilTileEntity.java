package princess.tenergistics.blocks.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.capabilities.SearedCoilFuelCapability;
import slimeknights.tconstruct.library.fluid.IFluidTankUpdater;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;

public class SearedCoilTileEntity extends SmelteryComponentTileEntity implements FluidUpdatePacket.IFluidPacketReceiver
	{
	private static final int				CAPACITY	= 16000;
	
	public static final String				TAG_ENERGY	= "energy";
	
	public static final IEnergyStorage		EMPTY		= new EnergyStorage(0);
	
	protected IEnergyStorage				energy;
	protected LazyOptional<IEnergyStorage>	energyCap;
	
	protected IFluidHandler					tank;
	protected LazyOptional<IFluidHandler>	tankCap;
	
	public SearedCoilTileEntity()
		{
		super(TEnergistics.searedCoilTile.get());
		
		energy = new EnergyStorage(CAPACITY);
		energyCap = LazyOptional.of(() -> energy);
		
		tank = new SearedCoilFuelCapability<SearedCoilTileEntity>(CAPACITY, this);
		tankCap = LazyOptional.of(() -> tank);
		}
		
	@Override
	protected boolean shouldSyncOnUpdate()
		{
		return true;
		}
		
	@Override
	public <C> LazyOptional<C> getCapability(Capability<C> capability, @Nullable Direction facing)
		{
		if (capability == CapabilityEnergy.ENERGY)
			{ return energyCap.cast(); }
		if (facing == null && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			{ return tankCap.cast(); }
		return super.getCapability(capability, facing);
		}
		
	protected void invalidateCaps()
		{
		super.invalidateCaps();
		energyCap.invalidate();
		tankCap.invalidate();
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
		
	@Override
	public void updateFluidTo(FluidStack fluid)
		{
		setEnergy(fluid.getAmount());
		}
		
	public void setEnergy(int energySet)
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
		
	public void onContentsChanged()
		{
		if (this instanceof IFluidTankUpdater)
			{
			((IFluidTankUpdater) this).onTankContentsChanged();
			}
			
		markDirty();
		World world = getWorld();
		if (!world.isRemote)
			{
			TinkerNetwork.getInstance()
					.sendToClientsAround(new FluidUpdatePacket(getPos(), tank.getFluidInTank(0)), (ServerWorld) world, getPos());
			}
		}
	}
