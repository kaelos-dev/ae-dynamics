package net.kaelos.aedynamics.datagen;

import net.kaelos.aedynamics.block.AEDBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class AEDBlockLootTableProvider extends BlockLootSubProvider {
    protected AEDBlockLootTableProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(AEDBlocks.ADVANCED_CRAFTING_UNIT.get());
        dropSelf(AEDBlocks.CRAFTING_STORAGE_1024K.get());
        dropSelf(AEDBlocks.CRAFTING_STORAGE_4096K.get());
        dropSelf(AEDBlocks.CRAFTING_STORAGE_16384K.get());
        dropSelf(AEDBlocks.CRAFTING_STORAGE_65536K.get());
        dropSelf(AEDBlocks.CRAFTING_STORAGE_262144K.get());

        dropSelf(AEDBlocks.ADVANCED_PATTERN_PROVIDER.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return AEDBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
