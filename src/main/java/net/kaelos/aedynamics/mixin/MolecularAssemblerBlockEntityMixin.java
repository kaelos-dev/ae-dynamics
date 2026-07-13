package net.kaelos.aedynamics.mixin;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.blockentity.crafting.MolecularAssemblerBlockEntity;
import net.kaelos.aedynamics.item.AEDItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {MolecularAssemblerBlockEntity.class}, remap = false)
public abstract class MolecularAssemblerBlockEntityMixin {
    @Shadow private IUpgradeInventory upgrades;
    @Shadow private double progress;

    @Inject(
            method = "tickingRequest",
            at = @At("HEAD")
    )
    private void applyMolecularStackSpeed(IGridNode node, int ticksSinceLastCall, CallbackInfoReturnable<TickRateModulation> cir) {
        if (this.upgrades != null && this.upgrades.getInstalledUpgrades(AEDItems.ADVANCED_SPEED_CARD.get()) > 0) {
            this.progress = 100.0;
        }
    }
}
