package net.kaelos.aedynamics;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEParts;
import net.kaelos.aedynamics.item.AEDItems;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = AED.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AEDServer {
    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Upgrades.add(AEDItems.MOLECULAR_STACK.get(), AEBlocks.INSCRIBER, 1);
            Upgrades.add(AEDItems.MOLECULAR_STACK.get(), AEParts.EXPORT_BUS, 1);
            Upgrades.add(AEDItems.MOLECULAR_STACK.get(), AEParts.IMPORT_BUS, 1);

            Upgrades.add(AEDItems.ADVANCED_SPEED_CARD.get(), AEBlocks.IO_PORT, 1);
            Upgrades.add(AEDItems.ADVANCED_SPEED_CARD.get(), AEParts.IMPORT_BUS, 1);
            Upgrades.add(AEDItems.ADVANCED_SPEED_CARD.get(), AEParts.EXPORT_BUS, 1);
            Upgrades.add(AEDItems.ADVANCED_SPEED_CARD.get(), AEBlocks.INSCRIBER, 1);
            Upgrades.add(AEDItems.ADVANCED_SPEED_CARD.get(), AEBlocks.MOLECULAR_ASSEMBLER, 1);
        });
    }
}
