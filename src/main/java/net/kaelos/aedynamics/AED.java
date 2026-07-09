package net.kaelos.aedynamics;

import net.kaelos.aedynamics.block.AEDBlocks;
import net.kaelos.aedynamics.entity.AEDEntities;
import net.kaelos.aedynamics.item.AEDCreativeModeTabs;
import net.kaelos.aedynamics.item.AEDItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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

        eventBus.addListener(this::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> AEDClient::init);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(AEDServer::init);
    }
}
