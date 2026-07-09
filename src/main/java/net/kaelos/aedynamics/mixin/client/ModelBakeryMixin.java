package net.kaelos.aedynamics.mixin.client;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelBakery.class})
public abstract class ModelBakeryMixin {
    @Inject(method = {"loadModel"}, at = {@At("HEAD")}, cancellable = true)
    private void loadModelHook(ResourceLocation id, CallbackInfo ci) {
        UnbakedModel model = this.aedynamics$getUnbakedModel(id);
        if (model != null) {
            this.cacheAndQueueDependencies(id, model);
            ci.cancel();
        }
    }

    @Unique
    private UnbakedModel aedynamics$getUnbakedModel(ResourceLocation variantId) {
        if (!variantId.getNamespace().equals("aedynamics")) {
            return null;
        } else if (variantId instanceof ModelResourceLocation) {
            ModelResourceLocation modelId = (ModelResourceLocation) variantId;
            if ("inventory".equals(modelId.getVariant())) {
                ResourceLocation itemModelId = ResourceLocation.fromNamespaceAndPath(modelId.getNamespace(), "item/" + modelId.getPath());
                return (UnbakedModel) BuiltInModelHooksAccessor.getBuiltInModels().get(itemModelId);
            } else {
                return null;
            }
        } else {
            return (UnbakedModel) BuiltInModelHooksAccessor.getBuiltInModels().get(variantId);
        }
    }

    @Shadow
    protected abstract void cacheAndQueueDependencies(ResourceLocation var1, UnbakedModel var2);
}
