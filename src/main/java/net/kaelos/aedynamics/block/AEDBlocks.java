package net.kaelos.aedynamics.block;

import appeng.block.crafting.CraftingUnitBlock;
import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.block.crafting.AdvancedCraftingUnitType;
import net.kaelos.aedynamics.item.AEDItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class AEDBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AED.MOD_ID);

    public static final RegistryObject<CraftingUnitBlock> ADVANCED_CRAFTING_UNIT = registerBlock("advanced_crafting_unit",
            () -> new CraftingUnitBlock(AdvancedCraftingUnitType.UNIT));

    public static final RegistryObject<CraftingUnitBlock> CRAFTING_STORAGE_1024K = registerBlock("crafting_storage_1024k",
            () -> new CraftingUnitBlock(AdvancedCraftingUnitType.STORAGE_1024K));
    public static final RegistryObject<CraftingUnitBlock> CRAFTING_STORAGE_4096K = registerBlock("crafting_storage_4096k",
            () -> new CraftingUnitBlock(AdvancedCraftingUnitType.STORAGE_4096K));
    public static final RegistryObject<CraftingUnitBlock> CRAFTING_STORAGE_16384K = registerBlock("crafting_storage_16384k",
            () -> new CraftingUnitBlock(AdvancedCraftingUnitType.STORAGE_16384K));
    public static final RegistryObject<CraftingUnitBlock> CRAFTING_STORAGE_65536K = registerBlock("crafting_storage_65536k",
            () -> new CraftingUnitBlock(AdvancedCraftingUnitType.STORAGE_65536));
    public static final RegistryObject<CraftingUnitBlock> CRAFTING_STORAGE_262144K = registerBlock("crafting_storage_262144k",
            () -> new CraftingUnitBlock(AdvancedCraftingUnitType.STORAGE_262144K));

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        AEDItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
