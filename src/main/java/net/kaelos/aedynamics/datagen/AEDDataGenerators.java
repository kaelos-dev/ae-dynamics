package net.kaelos.aedynamics.datagen;

import net.kaelos.aedynamics.AED;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = AED.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AEDDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new AEDItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new AEDBlockStateProvider(output, existingFileHelper));
    }
}
