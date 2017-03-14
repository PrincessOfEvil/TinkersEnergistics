package princess.tinkersenergistics;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.config.ForgeCFG;
import slimeknights.mantle.pulsar.control.PulseManager;

@Mod(name = ModInfo.NAME, modid = ModInfo.MODID, version = ModInfo.VERSION, dependencies = "required-after:tconstruct")
public class ModRegistryHelper {

    @Mod.Instance(value = ModInfo.MODID)
    public static ModRegistryHelper INSTANCE;

    public static ForgeCFG pulseConfig = new ForgeCFG(ModInfo.NAME, ModInfo.DESCRIPTION);
    public static PulseManager pulsar = new PulseManager(pulseConfig);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        pulsar.registerPulse(new TinkersEnergistics());
    }
}