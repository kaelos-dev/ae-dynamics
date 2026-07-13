package net.kaelos.aedynamics.mixin;

import appeng.api.storage.cells.IBasicCellItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.me.cells.BasicCellInventory;
import net.kaelos.aedynamics.item.storage.UnlimitedStorageCell;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {BasicCellInventory.class}, remap = false)
public abstract class BasicCellInventoryMixin {
    @Shadow private int maxItemTypes;
    @Shadow @Final IBasicCellItem cellType;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onInitComplete(IBasicCellItem cellType, ItemStack o, ISaveProvider container, CallbackInfo ci) {
        if (this.cellType instanceof UnlimitedStorageCell) {
            this.maxItemTypes = Integer.MAX_VALUE;
        }
    }
}
