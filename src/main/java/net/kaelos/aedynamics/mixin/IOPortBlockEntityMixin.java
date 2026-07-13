package net.kaelos.aedynamics.mixin;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.blockentity.storage.IOPortBlockEntity;
import net.kaelos.aedynamics.item.AEDItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = {IOPortBlockEntity.class}, remap = false)
public abstract class IOPortBlockEntityMixin {
    @Shadow public abstract IUpgradeInventory getUpgrades();

    @ModifyVariable(
            method = "tickingRequest",
            at = @At(value = "STORE",
                    ordinal = 0
            )
    )
    private long modifyItemsToMove(long originalItemsToMove) {
        int speedCards = this.getUpgrades().getInstalledUpgrades(AEDItems.ADVANCED_SPEED_CARD.get());
        return (speedCards > 0) ? Integer.MAX_VALUE : originalItemsToMove;
    }
}
