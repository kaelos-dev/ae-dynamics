package net.kaelos.aedynamics.entity.part;

import appeng.api.networking.IGridNodeListener;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.AECableType;
import appeng.items.parts.PartModels;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;
import appeng.util.SettingsFrom;
import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.helpers.patternprovider.AdvancedPatternProviderLogic;
import net.kaelos.aedynamics.helpers.patternprovider.AdvancedPatternProviderLogicHost;
import net.kaelos.aedynamics.item.AEDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class AdvancedPatternProviderPart extends AEBasePart implements AdvancedPatternProviderLogicHost {
    public static final ResourceLocation MODEL_BASE = ResourceLocation.fromNamespaceAndPath(AED.MOD_ID,
            "part/advanced_pattern_provider_base");

    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(MODEL_BASE,
            ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "part/advanced_pattern_provider_off"));

    @PartModels
    public static final PartModel MODELS_ON = new PartModel(MODEL_BASE,
            ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "part/advanced_pattern_provider_on"));

    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE,
            ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "part/advanced_pattern_provider_has_channel"));

    protected final AdvancedPatternProviderLogic logic = createLogic();

    public AdvancedPatternProviderPart(IPartItem<?> partItem) {
        super(partItem);
    }

    protected AdvancedPatternProviderLogic createLogic() {
        return new AdvancedPatternProviderLogic(this.getMainNode(), this);
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(5, 5, 12, 11, 11, 14);
    }

    @Override
    public AdvancedPatternProviderLogic getLogic() {
        return logic;
    }

    @Override
    public EnumSet<Direction> getTargets() {
        if (this.getSide() == null) {
            return null;
        }

        return EnumSet.of(this.getSide());
    }

    @Override
    public void saveChanges() {
        getHost().markForSave();
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(getPartItem());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AEDItems.ADVANCED_PATTERN_PROVIDER.get());
    }

    @Override
    protected void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        this.logic.onMainNodeStateChanged();
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.logic.readFromNBT(data);
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        this.logic.writeToNBT(data);
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.logic.updatePatterns();
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        this.logic.addDrops(drops);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        logic.clearContent();
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 4;
    }

    @Override
    public void exportSettings(SettingsFrom mode, CompoundTag output) {
        super.exportSettings(mode, output);

        if (mode == SettingsFrom.MEMORY_CARD) {
            logic.exportSettings(output);
        }
    }

    @Override
    public void importSettings(SettingsFrom mode, CompoundTag input, @Nullable Player player) {
        super.importSettings(mode, input, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            logic.importSettings(input, player);
        }
    }

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        logic.updateRedstoneState();
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!player.getCommandSenderWorld().isClientSide()) {
            openMenu(player, MenuLocators.forPart(this));
        }

        return true;
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capabilityClass) {
        return this.logic.getCapability(capabilityClass);
    }
}
