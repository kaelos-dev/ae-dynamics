package net.kaelos.aedynamics.mixin;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.parts.automation.IOBusPart;
import net.kaelos.aedynamics.item.AEDItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {IOBusPart.class}, remap = false)
public abstract class IOBusPartMixin {
    @Inject(
            method = "getOperationsPerTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetOperationsPerTick(CallbackInfoReturnable<Integer> cir) {
        IUpgradeableObject upgradeable = (IUpgradeableObject) this;

        int stackUpgradesCount = upgradeable.getUpgrades().getInstalledUpgrades(AEDItems.MOLECULAR_STACK_CARD.get());
        if (stackUpgradesCount > 0) {
            cir.setReturnValue(50000);
            cir.cancel();
            return;
        }

        int speedCards = upgradeable.getUpgrades().getInstalledUpgrades(AEDItems.ADVANCED_SPEED_CARD.get());
        int speed = (speedCards > 0) ? 1024 : 1;

        cir.setReturnValue(speed);
        cir.cancel();
    }
}
