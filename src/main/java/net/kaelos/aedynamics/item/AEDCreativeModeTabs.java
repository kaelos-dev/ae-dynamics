package net.kaelos.aedynamics.item;

import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.block.AEDBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AEDCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AED.MOD_ID);

    @SuppressWarnings("unused")
    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + AED.MOD_ID + ".main"))
                    .icon(() -> new ItemStack(AEDItems.ADVANCED_ITEM_CELL_HOUSING.get()))
                    .displayItems((displayParameters, output) -> {
                        output.accept(AEDItems.ELECTRITE_DUST.get());
                        output.accept(AEDItems.ELECTRITE_INGOT.get());

                        output.accept(AEDItems.EMPIRITIC_CRYSTAL.get());
                        output.accept(AEDItems.EMPIRITIC_DUST.get());
                        output.accept(AEDItems.PRINTED_EMPIRITIC_PROCESSOR.get());
                        output.accept(AEDItems.EMPIRITIC_PROCESSOR.get());

                        output.accept(AEDItems.MOLECULAR_STACK_CARD.get());
                        output.accept(AEDItems.ADVANCED_SPEED_CARD.get());
                        output.accept(AEDItems.EXPANSION_CARD.get());

                        output.accept(AEDItems.CELL_COMPONENT_1024K.get());
                        output.accept(AEDItems.CELL_COMPONENT_4096K.get());
                        output.accept(AEDItems.CELL_COMPONENT_16384K.get());
                        output.accept(AEDItems.CELL_COMPONENT_65536K.get());
                        output.accept(AEDItems.CELL_COMPONENT_262144K.get());
                        output.accept(AEDItems.ADVANCED_ITEM_CELL_HOUSING.get());

                        output.accept(AEDItems.ITEM_STORAGE_CELL_1024K.get());
                        output.accept(AEDItems.ITEM_STORAGE_CELL_4096K.get());
                        output.accept(AEDItems.ITEM_STORAGE_CELL_16384K.get());
                        output.accept(AEDItems.ITEM_STORAGE_CELL_65536K.get());
                        output.accept(AEDItems.ITEM_STORAGE_CELL_262144K.get());

                        output.accept(AEDBlocks.ADVANCED_CRAFTING_UNIT.get());
                        output.accept(AEDBlocks.CRAFTING_STORAGE_1024K.get());
                        output.accept(AEDBlocks.CRAFTING_STORAGE_4096K.get());
                        output.accept(AEDBlocks.CRAFTING_STORAGE_16384K.get());
                        output.accept(AEDBlocks.CRAFTING_STORAGE_65536K.get());
                        output.accept(AEDBlocks.CRAFTING_STORAGE_262144K.get());

                        output.accept(AEDBlocks.ADVANCED_PATTERN_PROVIDER.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
