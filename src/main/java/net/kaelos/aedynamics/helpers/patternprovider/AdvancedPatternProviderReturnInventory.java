package net.kaelos.aedynamics.helpers.patternprovider;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.capabilities.Capabilities;
import appeng.helpers.patternprovider.PatternProviderReturnInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AdvancedPatternProviderReturnInventory extends PatternProviderReturnInventory {
    public static int NUMBER_OF_SLOTS = 36;

    private boolean injectingIntoNetwork = false;
    private final LazyOptional<GenericInternalInventory> genericInternalInventory = LazyOptional.of(() -> this);

    private final GenericStack[] advancedStacks = new GenericStack[NUMBER_OF_SLOTS];

    private final Runnable listener;

    public AdvancedPatternProviderReturnInventory(@Nullable Runnable listener) {
        super(listener);
        this.listener = listener;
        useRegisteredCapacities();
    }

    @Override
    public int size() {
        return NUMBER_OF_SLOTS;
    }

    @Override
    public GenericStack getStack(int slot) {
        return advancedStacks[slot];
    }

    @Override
    public void setStack(int slot, @Nullable GenericStack stack) {
        advancedStacks[slot] = stack;
        if (listener != null) {
            listener.run();
        }
    }

    @Override
    public void clear() {
        Arrays.fill(advancedStacks, null);
        if (listener != null) {
            listener.run();
        }
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canInsert() {
        return !injectingIntoNetwork;
    }

    public boolean injectIntoNetwork(MEStorage storage, IActionSource source, Consumer<GenericStack> insertionCallback) {
        var didSomething = false;
        injectingIntoNetwork = true;

        try {
            for (int i = 0; i < advancedStacks.length; i++) {
                GenericStack stack = advancedStacks[i];
                if (stack != null) {
                    long sizeBefore = stack.amount();
                    var inserted = storage.insert(stack.what(), stack.amount(), Actionable.MODULATE, source);

                    if (inserted >= stack.amount()) {
                        advancedStacks[i] = null;
                    } else {
                        advancedStacks[i] = new GenericStack(stack.what(), stack.amount() - inserted);
                    }

                    inserted = Math.max(0, sizeBefore - GenericStack.getStackSizeOrZero(advancedStacks[i]));
                    if (inserted > 0) {
                        didSomething = true;
                        insertionCallback.accept(new GenericStack(stack.what(), inserted));
                    }
                }
            }
        } finally {
            injectingIntoNetwork = false;
            if (didSomething && listener != null) {
                listener.run();
            }
        }

        return didSomething;
    }

    public void addDrops(List<ItemStack> drops, Level level, BlockPos pos) {
        for (var stack : advancedStacks) {
            if (stack != null) {
                stack.what().addDrops(stack.amount(), drops, level, pos);
            }
        }
    }

    public <T> LazyOptional<T> getCapability(Capability<T> capability) {
        if (capability == Capabilities.GENERIC_INTERNAL_INV) {
            return genericInternalInventory.cast();
        } else {
            return LazyOptional.empty();
        }
    }

    @Override
    public void writeToChildTag(CompoundTag tag, String name) {
        ListTag list = new ListTag();
        for (int i = 0; i < advancedStacks.length; i++) {
            if (advancedStacks[i] != null) {
                CompoundTag itemTag = GenericStack.writeTag(advancedStacks[i]);
                itemTag.putInt("Slot", i);
                list.add(itemTag);
            }
        }

        tag.put(name, list);
    }

    @Override
    public void readFromChildTag(CompoundTag tag, String name) {
        Arrays.fill(advancedStacks, null);
        if (tag.contains(name, Tag.TAG_LIST)) {
            ListTag list = tag.getList(name, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag itemTag = list.getCompound(i);
                int slot = itemTag.getInt("Slot");
                if (slot >= 0 && slot < advancedStacks.length) {
                    advancedStacks[slot] = GenericStack.readTag(itemTag);
                }
            }
        }
    }
}
