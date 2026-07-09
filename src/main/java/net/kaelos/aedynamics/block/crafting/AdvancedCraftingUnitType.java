package net.kaelos.aedynamics.block.crafting;

import appeng.block.crafting.ICraftingUnitType;
import appeng.core.definitions.BlockDefinition;
import net.kaelos.aedynamics.block.AEDBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public enum AdvancedCraftingUnitType implements ICraftingUnitType {
    UNIT(0, "unit"),
    STORAGE_1024K(1024, "crafting_storage_1024k"),
    STORAGE_4096K(4096, "crafting_storage_4096k"),
    STORAGE_16384K(16384, "crafting_storage_16384k"),
    STORAGE_65536(65536, "crafting_storage_65536k"),
    STORAGE_262144K(262144, "crafting_storage_262144k");

    private final int storageKb;
    private final String id;

    AdvancedCraftingUnitType(int storageKb, String id) {
        this.storageKb = storageKb;
        this.id = id;
    }

    @Override
    public long getStorageBytes() {
        return 1024L * this.storageKb;
    }

    @Override
    public int getAcceleratorThreads() {
        return 0;
    }

    public String getId() {
        return this.id;
    }

    public Block getBlock() {
        var definition = switch (this) {
            case UNIT -> AEDBlocks.ADVANCED_CRAFTING_UNIT;
            case STORAGE_1024K -> AEDBlocks.CRAFTING_STORAGE_1024K;
            case STORAGE_4096K -> AEDBlocks.CRAFTING_STORAGE_4096K;
            case STORAGE_16384K -> AEDBlocks.CRAFTING_STORAGE_16384K;
            case STORAGE_65536 -> AEDBlocks.CRAFTING_STORAGE_65536K;
            case STORAGE_262144K -> AEDBlocks.CRAFTING_STORAGE_262144K;
        };

        return definition.get();
    }

    @Override
    public Item getItemFromType() {
        return this.getBlock().asItem();
    }
}
