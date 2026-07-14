package net.kaelos.aedynamics.datagen;

import appeng.block.crafting.CraftingUnitBlock;
import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.block.AEDBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class AEDBlockStateProvider extends BlockStateProvider {
    public AEDBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, AED.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        craftingStorage(AEDBlocks.CRAFTING_STORAGE_1024K.get());
        craftingStorage(AEDBlocks.CRAFTING_STORAGE_4096K.get());
        craftingStorage(AEDBlocks.CRAFTING_STORAGE_16384K.get());
        craftingStorage(AEDBlocks.CRAFTING_STORAGE_65536K.get());
        craftingStorage(AEDBlocks.CRAFTING_STORAGE_262144K.get());

        pathBlock(AEDBlocks.ADVANCED_CRAFTING_UNIT.get(), "crafting");
        pathBlock(AEDBlocks.ADVANCED_PATTERN_PROVIDER.get(), "provider");
    }

    private void craftingStorage(Block block) {
        String nameBlock = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();

        ModelFile unformedModel = models().cubeAll(
                "block/crafting/" + nameBlock,
                modLoc("block/crafting/" + nameBlock)
        );

        ModelFile formedModel = models().getBuilder("block/crafting/" + nameBlock + "_formed")
                .renderType("cutout");

        getVariantBuilder(block)
                .partialState().with(CraftingUnitBlock.FORMED, false)
                .modelForState().modelFile(unformedModel).addModel()
                .partialState().with(CraftingUnitBlock.FORMED, true)
                .modelForState().modelFile(formedModel).addModel();

        simpleBlockItem(block, unformedModel);
    }

    private void pathBlock(Block block, String path) {
        String nameBlock = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();
        var model = models().cubeAll(nameBlock, modLoc("block/" + path + "/" + nameBlock));
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }
}
