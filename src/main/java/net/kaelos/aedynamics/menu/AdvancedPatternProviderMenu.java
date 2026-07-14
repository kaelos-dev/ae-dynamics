package net.kaelos.aedynamics.menu;

import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.GenericStack;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.RestrictedInputSlot;
import net.kaelos.aedynamics.helpers.patternprovider.AdvancedPatternProviderLogic;
import net.kaelos.aedynamics.helpers.patternprovider.AdvancedPatternProviderLogicHost;
import net.kaelos.aedynamics.helpers.patternprovider.AdvancedPatternProviderReturnInventory;
import net.kaelos.aedynamics.item.AEDItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class AdvancedPatternProviderMenu extends AEBaseMenu {
    public static final MenuType<AdvancedPatternProviderMenu> TYPE = MenuTypeBuilder
            .create((id, inv, host) -> new AdvancedPatternProviderMenu(id, inv, host),
                    AdvancedPatternProviderLogicHost.class)
            .build("advanced_pattern_provider");

    protected final AdvancedPatternProviderLogic logic;

    @GuiSync(3)
    public YesNo blockingMode = YesNo.NO;
    @GuiSync(4)
    public YesNo showInAccessTerminal = YesNo.YES;
    @GuiSync(5)
    public LockCraftingMode lockCraftingMode = LockCraftingMode.NONE;
    @GuiSync(6)
    public LockCraftingMode craftingLockedReason = LockCraftingMode.NONE;
    @GuiSync(7)
    public GenericStack unlockStack = null;

    public AdvancedPatternProviderMenu(int id, Inventory inventory, AdvancedPatternProviderLogicHost host) {
        this(TYPE, id, inventory, host);
    }

    protected AdvancedPatternProviderMenu(MenuType<? extends AdvancedPatternProviderMenu> menuType, int id, Inventory inventory, AdvancedPatternProviderLogicHost host) {
        super(menuType, id, inventory, host);
        this.createPlayerInventorySlots(inventory);

        this.logic = host.getLogic();

        var upgrades = logic.getUpgrades();
        for (int i = 0; i < upgrades.size(); i++) {
            var upgradeSlot = new AppEngSlot(upgrades, i);
            setSlotPosition(upgradeSlot, 180, 8 + (i * 18));
            this.addSlot(upgradeSlot, SlotSemantics.UPGRADE);
        }

        var patternInv = logic.getPatternInv();
        for (int i = 0; i < patternInv.size(); i++) {
            var slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, patternInv, i) {
                @Override
                public boolean isActive() {
                    int capacityCards = logic.getUpgrades().getInstalledUpgrades(AEDItems.EXPANSION_CARD.get());
                    int maxActiveSlots = 18 + (capacityCards * 9);
                    return this.getSlotIndex() < maxActiveSlots;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    if (!this.isActive()) {
                        return false;
                    }
                    return super.mayPlace(stack);
                }
            };

            int col = i % 9;
            int row = i / 9;
            setSlotPosition(slot, 8 + col * 18, 29 + row * 18);

            this.addSlot(slot, SlotSemantics.ENCODED_PATTERN);
        }

        var returnInv = logic.getReturnInv().createMenuWrapper();
        for (int i = 0; i < 9; i++) {
            if (i < returnInv.size())  {
                this.addSlot(new AppEngSlot(returnInv, i), SlotSemantics.STORAGE);
            }
        }
    }

    private void setSlotPosition(Slot slot, int x, int y) {
        try {
            Field xField = ObfuscationReflectionHelper.findField(Slot.class, "f_40220_");
            Field yField = ObfuscationReflectionHelper.findField(Slot.class, "f_40221_");

            xField.setAccessible(true);
            yField.setAccessible(true);

            xField.setInt(slot, x);
            yField.setInt(slot, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            blockingMode = logic.getConfigManager().getSetting(Settings.BLOCKING_MODE);
            showInAccessTerminal = logic.getConfigManager().getSetting(Settings.PATTERN_ACCESS_TERMINAL);
            lockCraftingMode = logic.getConfigManager().getSetting(Settings.LOCK_CRAFTING_MODE);
            craftingLockedReason = logic.getCraftingLockedReason();
            unlockStack = logic.getUnlockStack();
        }

        super.broadcastChanges();
    }

    public GenericStackInv getReturnInv() {
        return logic.getReturnInv();
    }

    public YesNo getBlockingMode() {
        return blockingMode;
    }

    public LockCraftingMode getLockCraftingMode() {
        return lockCraftingMode;
    }

    public LockCraftingMode getCraftingLockedReason() {
        return craftingLockedReason;
    }

    public GenericStack getUnlockStack() {
        return unlockStack;
    }

    public YesNo getShowInAccessTerminal() {
        return showInAccessTerminal;
    }
}
