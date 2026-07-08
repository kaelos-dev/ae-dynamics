package net.kaelos.aedynamics.datagen;

import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.item.AEDItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class AEDItemModelProvider extends ItemModelProvider {
    public AEDItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AED.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        pathItem(AEDItems.ADVANCED_ITEM_CELL_HOUSING.get(), "cell");

        pathItem(AEDItems.CELL_COMPONENT_1024K.get(), "component");
        pathItem(AEDItems.CELL_COMPONENT_4096K.get(), "component");
        pathItem(AEDItems.CELL_COMPONENT_16384K.get(), "component");
        pathItem(AEDItems.CELL_COMPONENT_65536K.get(), "component");
        pathItem(AEDItems.CELL_COMPONENT_262144K.get(), "component");

        pathItem(AEDItems.ITEM_STORAGE_CELL_1024K.get(), "cell");
        pathItem(AEDItems.ITEM_STORAGE_CELL_4096K.get(), "cell");
        pathItem(AEDItems.ITEM_STORAGE_CELL_16384K.get(), "cell");
        pathItem(AEDItems.ITEM_STORAGE_CELL_65536K.get(), "cell");
        pathItem(AEDItems.ITEM_STORAGE_CELL_262144K.get(), "cell");
    }

    private void pathItem(Item item, String path) {
        String name = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).getPath();
        withExistingParent(name, "item/generated")
                .texture("layer0", modLoc("item/" + path + "/" + name));
    }
}
