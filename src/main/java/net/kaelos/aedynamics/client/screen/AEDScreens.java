package net.kaelos.aedynamics.client.screen;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.menu.AEBaseMenu;
import com.google.common.annotations.VisibleForTesting;
import net.kaelos.aedynamics.menu.AdvancedPatternProviderMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.IdentityHashMap;
import java.util.Map;

public class AEDScreens {
    @VisibleForTesting
    static final Map<MenuType<?>, String> MENU_STYLES = new IdentityHashMap<>();

    public static void init() {
        register(AdvancedPatternProviderMenu.TYPE, AdvancedPatternProviderScreen<AdvancedPatternProviderMenu>::new, "/screens/advanced_pattern_provider.json");
    }

    public static <M extends AEBaseMenu, U extends AEBaseScreen<M>> void register(MenuType<M> type, StyledScreenFactory<M, U> factory, String stylePath) {
        MENU_STYLES.put(type, stylePath);
        MenuScreens.<M, U>register(type, (menu, playerInv, title) -> {
            var style = StyleManager.loadStyleDoc(stylePath);
            return factory.create(menu, playerInv, title, style);
        });
    }

    @FunctionalInterface
    public interface StyledScreenFactory<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T t, Inventory pi, Component title, ScreenStyle style);
    }
}
