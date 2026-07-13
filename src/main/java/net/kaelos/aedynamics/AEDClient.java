package net.kaelos.aedynamics;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.hooks.BuiltInModelHooks;
import net.kaelos.aedynamics.block.crafting.AdvancedCraftingUnitType;
import net.kaelos.aedynamics.client.render.AdvancedCraftingUnitModelProvider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = AED.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AEDClient {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        registerModels();
    }

    public static void registerModels() {
        for (AdvancedCraftingUnitType type : AdvancedCraftingUnitType.values()) {
            BuiltInModelHooks.addBuiltInModel(
                    ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "block/crafting/" + type.getId() + "_formed"),
                    new CraftingCubeModel(new AdvancedCraftingUnitModelProvider(type))
            );
        }
    }

    @FunctionalInterface
    public interface StyledScreenFactory<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T t, Inventory pi, Component title, ScreenStyle style);
    }
}
