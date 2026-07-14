package net.kaelos.aedynamics.datagen;

import appeng.block.crafting.CraftingUnitBlock;
import appeng.block.crafting.PushDirection;
import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.block.AEDBlocks;
import net.kaelos.aedynamics.block.crafting.AdvancedPatternProviderBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
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

        patternProviderBlock();
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

    private void patternProviderBlock() {
        Block block = AEDBlocks.ADVANCED_PATTERN_PROVIDER.get();
        String nameBlock = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();

        ResourceLocation baseTex = ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "block/provider/" + nameBlock);
        ResourceLocation altTex = ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "block/provider/" + nameBlock + "_alternate");
        ResourceLocation frontTex = ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "block/provider/" + nameBlock + "_alternate_front");
        ResourceLocation arrowTex = ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "block/provider/" + nameBlock + "_alternate_arrow");

        BlockModelBuilder advancedPatternProviderModel = models().cubeAll(nameBlock, baseTex);

        BlockModelBuilder advancedPatternProviderOrientedModel = models().withExistingParent(nameBlock + "_oriented", mcLoc("block/cube"))
                .texture("particle", baseTex)
                .texture("down", altTex)
                .texture("up", frontTex)
                .texture("north", arrowTex)
                .texture("south", arrowTex)
                .texture("east", arrowTex)
                .texture("west", arrowTex);

        getVariantBuilder(block).forAllStates(state -> {
            PushDirection pushDirection = state.getValue(AdvancedPatternProviderBlock.PUSH_DIRECTION);

            return switch (pushDirection) {
                case ALL -> ConfiguredModel.builder()
                        .modelFile(advancedPatternProviderModel)
                        .build();
                case DOWN -> ConfiguredModel.builder()
                        .modelFile(advancedPatternProviderOrientedModel)
                        .rotationX(180)
                        .build();
                case EAST -> ConfiguredModel.builder()
                        .modelFile(advancedPatternProviderOrientedModel)
                        .rotationX(90)
                        .rotationY(90)
                        .build();
                case NORTH -> ConfiguredModel.builder()
                        .modelFile(advancedPatternProviderOrientedModel)
                        .rotationX(90)
                        .build();
                case SOUTH -> ConfiguredModel.builder()
                        .modelFile(advancedPatternProviderOrientedModel)
                        .rotationX(90)
                        .rotationY(180)
                        .build();
                case UP -> ConfiguredModel.builder()
                        .modelFile(advancedPatternProviderOrientedModel)
                        .build();
                case WEST -> ConfiguredModel.builder()
                        .modelFile(advancedPatternProviderOrientedModel)
                        .rotationX(90)
                        .rotationY(270)
                        .build();
            };
        });

        simpleBlockItem(block, advancedPatternProviderModel);
    }

    private void pathBlock(Block block, String path) {
        String nameBlock = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();
        var model = models().cubeAll(nameBlock, modLoc("block/" + path + "/" + nameBlock));
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }
}
