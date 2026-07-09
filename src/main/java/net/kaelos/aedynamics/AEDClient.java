package net.kaelos.aedynamics;

import appeng.client.render.crafting.CraftingCubeModel;
import appeng.hooks.BuiltInModelHooks;
import net.kaelos.aedynamics.block.crafting.AdvancedCraftingUnitType;
import net.kaelos.aedynamics.client.render.AdvancedCraftingUnitModelProvider;
import net.minecraft.resources.ResourceLocation;

public class AEDClient {
    public static void init() {
        for (AdvancedCraftingUnitType type : AdvancedCraftingUnitType.values()) {
            BuiltInModelHooks.addBuiltInModel(
                    ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "block/crafting/" + type.getId() + "_formed"),
                    new CraftingCubeModel(new AdvancedCraftingUnitModelProvider(type))
            );
        }
    }
}
