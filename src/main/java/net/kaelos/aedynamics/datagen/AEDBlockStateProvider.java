package net.kaelos.aedynamics.datagen;

import net.kaelos.aedynamics.AED;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class AEDBlockStateProvider extends BlockStateProvider {
    public AEDBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, AED.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {}
}
