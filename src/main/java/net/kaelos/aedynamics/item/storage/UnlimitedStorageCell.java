package net.kaelos.aedynamics.item.storage;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.localization.GuiText;
import appeng.core.localization.PlayerMessages;
import appeng.core.localization.Tooltips;
import appeng.items.contents.CellConfig;
import appeng.util.ConfigInventory;
import appeng.util.InteractionUtil;
import net.kaelos.aedynamics.AED;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnlimitedStorageCell extends Item implements IBasicCellItem {
    protected final ItemLike coreItem;
    protected final ItemLike housingItem;
    protected final double idleDrain;
    protected final int totalBytes;
    protected final int bytesPerType;
    private final AEKeyType keyType;

    public UnlimitedStorageCell(Properties properties, ItemLike coreItem, ItemLike housingItem, double idleDrain, int totalBytes, int bytesPerType, AEKeyType keyType) {
        super(properties.stacksTo(1));
        this.coreItem = coreItem;
        this.housingItem = housingItem;
        this.idleDrain = idleDrain;
        this.totalBytes = totalBytes * 1024;
        this.bytesPerType = bytesPerType;
        this.keyType = keyType;
    }

    @Override
    public AEKeyType getKeyType() {
        return this.keyType;
    }

    @Override
    public int getBytes(ItemStack cellItem) {
        return this.totalBytes;
    }

    @Override
    public int getBytesPerType(ItemStack cellItem) {
        return this.bytesPerType;
    }

    @Override
    public int getTotalTypes(ItemStack cellItem) {
        return Integer.MAX_VALUE;
    }

    @Override
    public double getIdleDrain() {
        return this.idleDrain;
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        final String fz = is.getOrCreateTag().getString("FuzzyMode");
        if (fz.isEmpty()) {
            return FuzzyMode.IGNORE_ALL;
        }

        try {
            return FuzzyMode.valueOf(fz);
        } catch (Throwable t) {
            return FuzzyMode.IGNORE_ALL;
        }
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
        is.getOrCreateTag().putString("FuzzyMode", fzMode.name());
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack stack) {
        return UpgradeInventories.forItem(stack, keyType == AEKeyType.items() ? 4 : 3);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(keyType.filter(), is);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        this.disassembleDrive(player.getItemInHand(hand), level, player);
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()), player.getItemInHand(hand));
    }

    private boolean disassembleDrive(ItemStack stack, Level level, Player player) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            if (level.isClientSide()) {
                return false;
            }

            final Inventory playerInv = player.getInventory();
            var inv = StorageCells.getCellInventory(stack, null);

            if (inv != null && playerInv.getSelected() == stack) {
                var list = inv.getAvailableStacks();
                if (list.isEmpty()) {
                    playerInv.setItem(playerInv.selected, ItemStack.EMPTY);

                    // drop core
                    playerInv.placeItemBackInInventory(new ItemStack(coreItem));

                    // drop upgrades
                    for (var upgrade : this.getUpgrades(stack)) {
                        playerInv.placeItemBackInInventory(upgrade);
                    }

                    // drop empty storage cell case
                    playerInv.placeItemBackInInventory(new ItemStack(housingItem));

                    return true;
                } else {
                    player.displayClientMessage(PlayerMessages.OnlyEmptyCellsCanBeDisassembled.text(), true);
                }
            }
        }

        return false;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return this.disassembleDrive(stack, context.getLevel(), context.getPlayer())
                ? InteractionResult.sidedSuccess(context.getLevel().isClientSide())
                : InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag tag = stack.getTag();
        long storedItemCount = 0;
        int storedTypes = 0;

        if (tag != null) {
            storedItemCount = tag.getLong("ic");

            if (tag.contains("amts")) {
                storedTypes = tag.getLongArray("amts").length;
            }
        }

        long bytesForItems = (storedItemCount + 7) / 8;
        long bytesForTypes = (long) storedTypes * this.getBytesPerType(stack);
        long totalUsedBytes = bytesForItems + bytesForTypes;
        tooltip.add(Tooltips.bytesUsed(totalUsedBytes, this.totalBytes));

        Component typesTooltip = Tooltips.of(
                Tooltips.ofUnformattedNumberWithRatioColor(storedTypes, 0.0, false),
                Tooltips.of(" "),
                Tooltips.of(GuiText.Of),
                Tooltips.of(" "),
                Component.translatable("gui." + AED.MOD_ID + ".types_infinity").withStyle(Tooltips.NUMBER_TEXT),
                Tooltips.of(" "),
                Tooltips.of(GuiText.Types)
        );
        tooltip.add(typesTooltip);
    }
}
