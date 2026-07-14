package net.kaelos.aedynamics.helpers.patternprovider;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.util.inv.AppEngInternalInventory;
import net.kaelos.aedynamics.block.AEDBlocks;
import net.kaelos.aedynamics.item.AEDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Field;
import java.util.List;

public class AdvancedPatternProviderLogic extends PatternProviderLogic {
    private final AppEngInternalInventory advancedPatternInv;
    private final AdvancedPatternProviderReturnInventory advancedReturnInv;

    private final IUpgradeInventory upgrades;
    private final AdvancedPatternProviderLogicHost host;

    private final IManagedGridNode mainNode;

    public AdvancedPatternProviderLogic(IManagedGridNode mainNode, AdvancedPatternProviderLogicHost host) {
        super(mainNode, host);
        this.host = host;
        this.mainNode = mainNode;

        this.advancedPatternInv = new AppEngInternalInventory(this, 36);
        this.advancedReturnInv = new AdvancedPatternProviderReturnInventory(host::saveChanges);

        this.upgrades = UpgradeInventories.forMachine(AEDBlocks.ADVANCED_PATTERN_PROVIDER.get(), 2, this::onUpgradesChanged);
    }

    @Override
    public void updatePatterns() {
        super.updatePatterns();

        try {
            Field patternsField = PatternProviderLogic.class.getDeclaredField("patterns");
            patternsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<IPatternDetails> patternsList = (List<IPatternDetails>) patternsField.get(this);
            patternsList.clear();

            Level level = this.host.getBlockEntity().getLevel();
            if (level == null) return;

            for (int i = 0; i < this.advancedPatternInv.size(); i++) {
                ItemStack stack = this.advancedPatternInv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    IPatternDetails details = PatternDetailsHelper.decodePattern(stack, level);
                    if (details != null) {
                        patternsList.add(details);
                    }
                }
            }

            if (this.mainNode != null && this.mainNode.getNode() != null) {
                IGridNode node = this.mainNode.getNode();
                if (node.getGrid() != null) {
                    ICraftingService craftingService = node.getGrid().getService(ICraftingService.class);
                    if (craftingService != null) {
                        craftingService.refreshNodeCraftingProvider(node);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onUpgradesChanged() {
        this.host.saveChanges();

        Level level = this.host.getBlockEntity().getLevel();
        BlockPos pos = this.host.getBlockEntity().getBlockPos();

        if (level == null || level.isClientSide()) {
            return;
        }

        int capacityCards = this.upgrades.getInstalledUpgrades(AEDItems.EXPANSION_CARD.get());
        int maxActiveSlots = 18 + (capacityCards * 9);

        boolean inventoryChanged = false;
        for (int i = maxActiveSlots; i < this.advancedPatternInv.size(); i++) {
            ItemStack stack = this.advancedPatternInv.getStackInSlot(i);

            if (!stack.isEmpty()) {
                Block.popResource(level, pos, stack);
                this.advancedPatternInv.setItemDirect(i, ItemStack.EMPTY);
                inventoryChanged = true;
            }
        }

        if (inventoryChanged) {
            this.updatePatterns();
        }
    }

    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public InternalInventory getPatternInv() {
        return this.advancedPatternInv;
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);

        ListTag patternList = new ListTag();
        for (int i = 0; i < advancedPatternInv.size(); i++) {
            ItemStack stack = advancedPatternInv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stack.save(itemTag);
                patternList.add(itemTag);
            }
        }

        data.put("AdvancedPatterns", patternList);
        this.advancedReturnInv.writeToChildTag(data, "AdvancedReturn");
        this.upgrades.writeToNBT(data, "AdvancedUpgrades");
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);

        advancedPatternInv.clear();
        if (data.contains("AdvancedPatterns", Tag.TAG_LIST)) {
            ListTag patternList = data.getList("AdvancedPatterns", Tag.TAG_COMPOUND);
            for (int i = 0; i < patternList.size(); i++) {
                CompoundTag itemTag = patternList.getCompound(i);
                int slot = itemTag.getInt("Slot");
                if (slot >= 0 && slot < advancedPatternInv.size()) {
                    advancedPatternInv.setItemDirect(slot, ItemStack.of(itemTag));
                }
            }
        }

        if (data.contains("AdvancedReturn")) {
            this.advancedReturnInv.readFromChildTag(data, "AdvancedReturn");
        }

        if (data.contains("AdvancedUpgrades")) {
            this.upgrades.readFromNBT(data, "AdvancedUpgrades");
        }
    }
}
