package net.kaelos.aedynamics;

import net.kaelos.aedynamics.block.AEDBlocks;
import net.kaelos.aedynamics.entity.AEDEntities;
import net.kaelos.aedynamics.item.AEDCreativeModeTabs;
import net.kaelos.aedynamics.item.AEDItems;
import net.kaelos.aedynamics.menu.AEDMenuTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod(AED.MOD_ID)
public class AED {
    public static final String MOD_ID = "aedynamics";

    public AED(final FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        AEDItems.register(eventBus);
        AEDCreativeModeTabs.register(eventBus);
        AEDBlocks.register(eventBus);
        AEDEntities.register(eventBus);

        eventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(ForgeRegistries.Keys.MENU_TYPES)) {
                AEDMenuTypes.init(event);
            }
        });
    }
}
