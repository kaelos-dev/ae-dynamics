package net.kaelos.aedynamics.item;

import appeng.api.parts.PartModels;
import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.Upgrades;
import appeng.items.materials.StorageComponentItem;
import appeng.items.materials.UpgradeCardItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.entity.part.AdvancedPatternProviderPart;
import net.kaelos.aedynamics.item.storage.UnlimitedStorageCell;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AEDItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AED.MOD_ID);

    public static final RegistryObject<Item> ADVANCED_ITEM_CELL_HOUSING = ITEMS.register("advanced_item_cell_housing",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<StorageComponentItem> CELL_COMPONENT_1024K = ITEMS.register("cell_component_1024k",
            () -> new StorageComponentItem(new Item.Properties(), 1024));
    public static final RegistryObject<StorageComponentItem> CELL_COMPONENT_4096K = ITEMS.register("cell_component_4096k",
            () -> new StorageComponentItem(new Item.Properties(), 4096));
    public static final RegistryObject<StorageComponentItem> CELL_COMPONENT_16384K = ITEMS.register("cell_component_16384k",
            () -> new StorageComponentItem(new Item.Properties(), 16384));
    public static final RegistryObject<StorageComponentItem> CELL_COMPONENT_65536K = ITEMS.register("cell_component_65536k",
            () -> new StorageComponentItem(new Item.Properties(), 65536));
    public static final RegistryObject<StorageComponentItem> CELL_COMPONENT_262144K = ITEMS.register("cell_component_262144k",
            () -> new StorageComponentItem(new Item.Properties(), 262144));

    public static final RegistryObject<UnlimitedStorageCell> ITEM_STORAGE_CELL_1024K = ITEMS.register("item_storage_cell_1024k",
            () -> new UnlimitedStorageCell(new Item.Properties(), CELL_COMPONENT_1024K.get(), ADVANCED_ITEM_CELL_HOUSING.get(), 3.0, 1024, 8192, AEKeyType.items()));
    public static final RegistryObject<UnlimitedStorageCell> ITEM_STORAGE_CELL_4096K = ITEMS.register("item_storage_cell_4096k",
            () -> new UnlimitedStorageCell(new Item.Properties(), CELL_COMPONENT_4096K.get(), ADVANCED_ITEM_CELL_HOUSING.get(), 3.5, 4096, 32768, AEKeyType.items()));
    public static final RegistryObject<UnlimitedStorageCell> ITEM_STORAGE_CELL_16384K = ITEMS.register("item_storage_cell_16384k",
            () -> new UnlimitedStorageCell(new Item.Properties(), CELL_COMPONENT_16384K.get(), ADVANCED_ITEM_CELL_HOUSING.get(), 4.0, 16384, 131072, AEKeyType.items()));
    public static final RegistryObject<UnlimitedStorageCell> ITEM_STORAGE_CELL_65536K = ITEMS.register("item_storage_cell_65536k",
            () -> new UnlimitedStorageCell(new Item.Properties(), CELL_COMPONENT_65536K.get(), ADVANCED_ITEM_CELL_HOUSING.get(), 4.5, 65536, 524288, AEKeyType.items()));
    public static final RegistryObject<UnlimitedStorageCell> ITEM_STORAGE_CELL_262144K = ITEMS.register("item_storage_cell_262144k",
            () -> new UnlimitedStorageCell(new Item.Properties(), CELL_COMPONENT_262144K.get(), ADVANCED_ITEM_CELL_HOUSING.get(), 5.0, 262144, 2097152, AEKeyType.items()));

    public static final RegistryObject<Item> EMPIRITIC_CRYSTAL = ITEMS.register("empiritic_crystal",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EMPIRITIC_DUST = ITEMS.register("empiritic_dust",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PRINTED_EMPIRITIC_PROCESSOR = ITEMS.register("printed_empiritic_processor",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EMPIRITIC_PROCESSOR = ITEMS.register("empiritic_processor",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ELECTRITE_DUST = ITEMS.register("electrite_dust",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ELECTRITE_INGOT = ITEMS.register("electrite_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MOLECULAR_STACK_CARD = ITEMS.register("molecular_stack_card",
            () -> new UpgradeCardItem(new Item.Properties().stacksTo(16)) {
                    @OnlyIn(Dist.CLIENT)
                    @Override
                    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag advancedTooltips) {
                        super.appendHoverText(stack, level, lines, advancedTooltips);
                        lines.add(Component.empty());
                        lines.add(Component.translatable("gui." + AED.MOD_ID + ".molecular_stack").withStyle(ChatFormatting.GRAY));
                    }
                });
    public static final RegistryObject<Item> ADVANCED_SPEED_CARD = ITEMS.register("advanced_speed_card",
            () -> Upgrades.createUpgradeCardItem(new Item.Properties()));
    public static final RegistryObject<Item> EXPANSION_CARD = ITEMS.register("expansion_card",
            () -> Upgrades.createUpgradeCardItem(new Item.Properties()));

    public static final RegistryObject<PartItem<AdvancedPatternProviderPart>> ADVANCED_PATTERN_PROVIDER = ITEMS.register("advanced_pattern_provider_part",
            () -> {
                PartModels.registerModels(PartModelsHelper.createModels(AdvancedPatternProviderPart.class));

                return new PartItem<>(
                        new Item.Properties(),
                        AdvancedPatternProviderPart.class,
                        AdvancedPatternProviderPart::new
                );
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
