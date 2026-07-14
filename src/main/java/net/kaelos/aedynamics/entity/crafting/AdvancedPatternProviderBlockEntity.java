package net.kaelos.aedynamics.entity.crafting;

import appeng.api.networking.IGridNodeListener;
import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.AEItemKey;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.block.crafting.PushDirection;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.util.SettingsFrom;
import net.kaelos.aedynamics.block.AEDBlocks;
import net.kaelos.aedynamics.block.crafting.AdvancedPatternProviderBlock;
import net.kaelos.aedynamics.helpers.patternprovider.AdvancedPatternProviderLogic;
import net.kaelos.aedynamics.helpers.patternprovider.AdvancedPatternProviderLogicHost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AdvancedPatternProviderBlockEntity extends AENetworkBlockEntity implements AdvancedPatternProviderLogicHost, IUpgradeableObject {
    protected final AdvancedPatternProviderLogic logic = createLogic();

    @Nullable
    private PushDirection pendingPushDirectionChange;

    public AdvancedPatternProviderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    protected AdvancedPatternProviderLogic createLogic() {
        return new AdvancedPatternProviderLogic(this.getMainNode(), this);
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        this.logic.onMainNodeStateChanged();
    }

    private PushDirection getPushDirection() {
        return getBlockState().getValue(AdvancedPatternProviderBlock.PUSH_DIRECTION);
    }


    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        var pushDirection = getPushDirection().getDirection();
        if (pushDirection == null) {
            return EnumSet.allOf(Direction.class);
        }

        return EnumSet.complementOf(EnumSet.of(pushDirection));
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        this.logic.addDrops(drops);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.logic.clearContent();
    }

    @Override
    public void onReady() {
        if (pendingPushDirectionChange != null) {
            if (level == null) {
                return;
            }

            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(AdvancedPatternProviderBlock.PUSH_DIRECTION, pendingPushDirectionChange));
            pendingPushDirectionChange = null;
            onGridConnectableSidesChanged();
        }

        super.onReady();
        this.logic.updatePatterns();
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.logic.writeToNBT(data);
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);

        if (data.getBoolean("omniDirectional")) {
            pendingPushDirectionChange = PushDirection.ALL;
        } else if (data.contains("forward", CompoundTag.TAG_STRING)) {
            try {
                var forward = Direction.valueOf(data.getString("forward").toUpperCase(Locale.ROOT));
                pendingPushDirectionChange = PushDirection.fromDirection(forward);
            } catch (IllegalArgumentException ignored) {}
        }

        this.logic.readFromNBT(data);
    }

    @Override
    public AdvancedPatternProviderLogic getLogic() {
        return logic;
    }

    @Override
    public EnumSet<Direction> getTargets() {
        var pushDirection = getPushDirection();
        if (pushDirection.getDirection() == null) {
            return EnumSet.allOf(Direction.class);
        } else {
            return EnumSet.of(pushDirection.getDirection());
        }
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(AEDBlocks.ADVANCED_PATTERN_PROVIDER.get());
    }

    @Override
    public void exportSettings(SettingsFrom mode, CompoundTag output, @org.jetbrains.annotations.Nullable Player player) {
        super.exportSettings(mode, output, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            logic.exportSettings(output);

            var pushDirection = getPushDirection();
            output.putByte("push_direction", (byte) pushDirection.ordinal());
        }
    }

    @Override
    public void importSettings(SettingsFrom mode, CompoundTag input, @org.jetbrains.annotations.Nullable Player player) {
        super.importSettings(mode, input, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            logic.importSettings(input, player);

            if (input.contains(AdvancedPatternProviderBlock.PUSH_DIRECTION.getName(), CompoundTag.TAG_BYTE)) {
                var pushDirection = input.getByte(AdvancedPatternProviderBlock.PUSH_DIRECTION.getName());
                if (pushDirection >= 0 && pushDirection < PushDirection.values().length) {
                    var level = getLevel();
                    if (level != null) {
                        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(AdvancedPatternProviderBlock.PUSH_DIRECTION, PushDirection.values()[pushDirection]));
                    }
                }
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        var lo = logic.getCapability(cap);
        if (lo.isPresent()) {
            return lo;
        }

        return super.getCapability(cap, side);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AEDBlocks.ADVANCED_PATTERN_PROVIDER.get());
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        onGridConnectableSidesChanged();
    }
}
