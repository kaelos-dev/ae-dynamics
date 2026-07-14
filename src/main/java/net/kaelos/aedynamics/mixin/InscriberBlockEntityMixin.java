package net.kaelos.aedynamics.mixin;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.grid.AENetworkPowerBlockEntity;
import appeng.blockentity.misc.InscriberBlockEntity;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipe;
import appeng.util.inv.AppEngInternalInventory;
import net.kaelos.aedynamics.item.AEDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {InscriberBlockEntity.class}, remap = false)
public abstract class InscriberBlockEntityMixin extends AENetworkPowerBlockEntity {
    @Shadow @Final private AppEngInternalInventory topItemHandler;
    @Shadow @Final private AppEngInternalInventory bottomItemHandler;
    @Shadow @Final private AppEngInternalInventory sideItemHandler;
    @Shadow private int finalStep;
    @Shadow private int processingTime;
    @Shadow private boolean smash;

    @Shadow public abstract boolean isSmash();
    @Shadow public abstract void setSmash(boolean smash);
    @Shadow public abstract InscriberRecipe getTask();
    @Shadow public abstract int getMaxProcessingTime();
    @Shadow public abstract IUpgradeInventory getUpgrades();
    @Shadow protected abstract boolean hasCraftWork();
    @Shadow protected abstract boolean pushOutResult();
    @Shadow protected abstract boolean hasAutoExportWork();

    public InscriberBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Inject(
            method = "tickingRequest",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onTickingRequest(IGridNode node, int ticksSinceLastCall, CallbackInfoReturnable<TickRateModulation> cir) {
        int stackUpgradesCount = this.getUpgrades().getInstalledUpgrades(AEDItems.MOLECULAR_STACK_CARD.get());

        if (stackUpgradesCount == 0) {
            return;
        }

        int maxBatchSize = 64;

        if (this.isSmash()) {
            this.finalStep++;
            if (this.finalStep == 8) {
                final InscriberRecipe out = this.getTask();
                if (out != null) {
                    int batchSize = calculateBatchSize(out, maxBatchSize);

                    if (batchSize > 0) {
                        ItemStack outputStack = out.getResultItem().copy();
                        outputStack.setCount(batchSize);

                        if (this.sideItemHandler.insertItem(1, outputStack, false).isEmpty()) {
                            this.processingTime = 0;

                            if (out.getProcessType() == InscriberProcessType.PRESS) {
                                this.topItemHandler.extractItem(0, batchSize, false);
                                this.bottomItemHandler.extractItem(0, batchSize, false);
                            }

                            this.sideItemHandler.extractItem(0, batchSize, false);
                        }
                    }
                }

                this.saveChanges();
            } else if (this.finalStep == 16) {
                this.finalStep = 0;
                this.setSmash(false);
                this.markForUpdate();
            }
        } else if (this.hasCraftWork()) {
            getMainNode().ifPresent(grid -> {
                IEnergyService eg = grid.getEnergyService();
                IEnergySource src = this;

                IUpgradeableObject upgradeable = (IUpgradeableObject) this;
                int speedCards = upgradeable.getUpgrades().getInstalledUpgrades(AEDItems.ADVANCED_SPEED_CARD.get());
                final int speedFactor = (speedCards > 0) ? Integer.MAX_VALUE : 2;

                final int powerConsumption = 10 * speedFactor * 10;
                final double powerThreshold = powerConsumption - 0.01;

                double powerReq = this.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                if (powerReq <= powerThreshold) {
                    src = eg;
                    powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                }

                if (powerReq > powerThreshold) {
                    src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    this.processingTime += speedFactor;
                }
            });

            if (this.processingTime > this.getMaxProcessingTime()) {
                this.processingTime = this.getMaxProcessingTime();

                final InscriberRecipe out = this.getTask();
                if (out != null) {
                    int batchSize = calculateBatchSize(out, maxBatchSize);
                    if (batchSize > 0) {
                        ItemStack testOutput = out.getResultItem().copy();
                        testOutput.setCount(batchSize);

                        if (this.sideItemHandler.insertItem(1, testOutput, true).isEmpty()) {
                            this.setSmash(true);
                            this.finalStep = 0;
                            this.markForUpdate();
                        }
                    }
                }
            }
        }

        TickRateModulation modulation;
        if (this.pushOutResult()) {
            modulation = TickRateModulation.URGENT;
        } else {
            modulation = this.hasCraftWork() ? TickRateModulation.URGENT
                    : this.hasAutoExportWork() ? TickRateModulation.SLOWER : TickRateModulation.SLEEP;
        }

        cir.setReturnValue(modulation);
        cir.cancel();
    }

    private int calculateBatchSize(InscriberRecipe recipe, int maxBatchSize) {
        int batch = maxBatchSize;

        ItemStack middleStack = this.sideItemHandler.getStackInSlot(0);
        if (middleStack.isEmpty()) return 0;
        batch = Math.min(batch, middleStack.getCount());

        if (recipe.getProcessType() == InscriberProcessType.PRESS) {
            ItemStack topStack = this.topItemHandler.getStackInSlot(0);
            ItemStack bottomStack = this.bottomItemHandler.getStackInSlot(0);

            if (topStack.isEmpty() || bottomStack.isEmpty()) return 0;

            batch = Math.min(batch, topStack.getCount());
            batch = Math.min(batch, bottomStack.getCount());
        }

        ItemStack outputSlot = this.sideItemHandler.getStackInSlot(1);
        if (!outputSlot.isEmpty()) {
            int spaceLeft = outputSlot.getMaxStackSize() - outputSlot.getCount();
            batch = Math.min(batch, spaceLeft / recipe.getResultItem().getCount());
        }

        return Math.max(batch, 0);
    }
}
