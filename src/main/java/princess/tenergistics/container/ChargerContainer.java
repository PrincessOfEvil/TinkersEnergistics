package princess.tenergistics.container;

import java.util.function.Consumer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.blocks.tileentity.ChargerTileEntity;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.inventory.ItemHandlerSlot;
import slimeknights.tconstruct.library.utils.IntArrayWrapper;
import slimeknights.tconstruct.library.utils.ValidZeroIntReference;

@SuppressWarnings("unused")
public class ChargerContainer extends BaseContainer<ChargerTileEntity>
	{
	
	public ChargerContainer(int id, PlayerInventory inv, ChargerTileEntity te)
		{
		super(TEnergistics.chargerContainer.get(), id, inv, te);
		if (te != null)
			{
			te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					.ifPresent(handler -> this.addSlot(new ItemHandlerSlot(handler, 0, 80, 20)));
			
			this.trackIntArray(te.energyArray);
			
			this.addInventorySlots();
			}
		}
		
	public ChargerContainer(int id, PlayerInventory inv, PacketBuffer buf)
		{
		this(id, inv, getTileEntityFromBuf(buf, ChargerTileEntity.class));
		}
		
	@Override
	protected int getInventoryYOffset()
		{
		return 51;
		}
	}
