package net.kaelos.aedynamics.client.render;

import appeng.client.render.crafting.AbstractCraftingUnitModelProvider;
import appeng.client.render.crafting.LightBakedModel;
import appeng.client.render.crafting.UnitBakedModel;
import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.block.crafting.AdvancedCraftingUnitType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class AdvancedCraftingUnitModelProvider extends AbstractCraftingUnitModelProvider<AdvancedCraftingUnitType> {
    private static final List<Material> MATERIALS = new ArrayList<>();
    private static final ChunkRenderTypeSet CUTOUT = ChunkRenderTypeSet.of(RenderType.cutout());

    protected static final Material RING_CORNER = texture("ring_corner");
    protected static final Material RING_SIDE_HOR = texture("ring_side_hor");
    protected static final Material RING_SIDE_VER = texture("ring_side_ver");
    protected static final Material UNIT_BASE = texture("unit_base");
    protected static final Material LIGHT_BASE = texture("light_base");
    protected static final Material STORAGE_1024K_LIGHT = texture("crafting_storage_1024k_light");
    protected static final Material STORAGE_4096K_LIGHT = texture("crafting_storage_4096k_light");
    protected static final Material STORAGE_16384K_LIGHT = texture("crafting_storage_16384k_light");
    protected static final Material STORAGE_65536K_LIGHT = texture("crafting_storage_65536k_light");
    protected static final Material STORAGE_262144K_LIGHT = texture("crafting_storage_262144k_light");

    public AdvancedCraftingUnitModelProvider(AdvancedCraftingUnitType type) {
        super(type);
    }

    @Override
    public List<Material> getMaterials() {
        return Collections.unmodifiableList(MATERIALS);
    }

    public TextureAtlasSprite getLightMaterial(Function<Material, TextureAtlasSprite> textureGetter) {
        return switch (this.type) {
            case STORAGE_1024K -> textureGetter.apply(STORAGE_1024K_LIGHT);
            case STORAGE_4096K -> textureGetter.apply(STORAGE_4096K_LIGHT);
            case STORAGE_16384K -> textureGetter.apply(STORAGE_16384K_LIGHT);
            case STORAGE_65536 -> textureGetter.apply(STORAGE_65536K_LIGHT);
            case STORAGE_262144K -> textureGetter.apply(STORAGE_262144K_LIGHT);
            default -> throw new IllegalArgumentException(
                    "Crafting unit type " + this.type + " does not use a light texture.");
        };
    }

    @Override
    public BakedModel getBakedModel(Function<Material, TextureAtlasSprite> spriteGetter) {
        TextureAtlasSprite ringCorner = spriteGetter.apply(RING_CORNER);
        TextureAtlasSprite ringSideHor = spriteGetter.apply(RING_SIDE_HOR);
        TextureAtlasSprite ringSideVer = spriteGetter.apply(RING_SIDE_VER);

        return switch (this.type) {
            case UNIT -> new UnitBakedModel(ringCorner, ringSideHor, ringSideVer, spriteGetter.apply(UNIT_BASE)) {
                @Override
                public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
                    return CUTOUT;
                }
            };
            case STORAGE_1024K, STORAGE_4096K, STORAGE_16384K, STORAGE_65536, STORAGE_262144K -> new LightBakedModel(ringCorner, ringSideHor, ringSideVer, spriteGetter.apply(LIGHT_BASE), this.getLightMaterial(spriteGetter)) {
                @Override
                public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
                    return CUTOUT;
                }
            };
        };
    }

    private static Material texture(String name) {
        Material material = new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath(AED.MOD_ID, "block/crafting/" + name));
        MATERIALS.add(material);
        return material;
    }
}
