package net.kaelos.aedynamics.menu;

import net.kaelos.aedynamics.AED;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

public final class AEDMenuTypes {
    public static void init(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.MENU_TYPES, helper -> {
            helper.register(
                    ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "advanced_pattern_provider"),
                    AdvancedPatternProviderMenu.TYPE
            );
        });
    }

    private static void registryAll(IForgeRegistry<MenuType<?>> registry, MenuType<?>... types) {
        for (var type : types) {
            if (registry.getResourceKey(type).isEmpty()) {
                throw new IllegalStateException("Menu Type " + type + " is not registered");
            }
        }
    }
}
