package net.kaelos.aedynamics.datagen;

import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.block.AEDBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class AEDBlockTagProvider extends BlockTagsProvider {
    public AEDBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AED.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(AEDBlocks.ADVANCED_CRAFTING_UNIT.get())
                .add(AEDBlocks.CRAFTING_STORAGE_1024K.get())
                .add(AEDBlocks.CRAFTING_STORAGE_4096K.get())
                .add(AEDBlocks.CRAFTING_STORAGE_16384K.get())
                .add(AEDBlocks.CRAFTING_STORAGE_65536K.get())
                .add(AEDBlocks.CRAFTING_STORAGE_262144K.get())
                .add(AEDBlocks.ADVANCED_PATTERN_PROVIDER.get());
    }
}
