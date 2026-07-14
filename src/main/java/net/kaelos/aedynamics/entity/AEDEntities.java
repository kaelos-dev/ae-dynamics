package net.kaelos.aedynamics.entity;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.crafting.CraftingBlockEntity;
import com.google.common.base.Preconditions;
import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.block.AEDBlocks;
import net.kaelos.aedynamics.entity.crafting.AdvancedPatternProviderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public class AEDEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AED.MOD_ID);

    public static final RegistryObject<BlockEntityType<CraftingBlockEntity>> ADVANCED_CRAFTING_UNITS = create(
            "advanced_crafting_units",
            CraftingBlockEntity.class,
            CraftingBlockEntity::new,
            AEDBlocks.ADVANCED_CRAFTING_UNIT,
            AEDBlocks.CRAFTING_STORAGE_1024K,
            AEDBlocks.CRAFTING_STORAGE_4096K,
            AEDBlocks.CRAFTING_STORAGE_16384K,
            AEDBlocks.CRAFTING_STORAGE_65536K,
            AEDBlocks.CRAFTING_STORAGE_262144K
    );

    public static final RegistryObject<BlockEntityType<AdvancedPatternProviderBlockEntity>> ADVANCED_PATTERN_PROVIDER_BE = create(
            "advanced_pattern_provider_be",
            AdvancedPatternProviderBlockEntity.class,
            AdvancedPatternProviderBlockEntity::new,
            AEDBlocks.ADVANCED_PATTERN_PROVIDER
    );

    @SafeVarargs
    public static <T extends AEBaseBlockEntity> RegistryObject<BlockEntityType<T>> create(String id, Class<T> entityClass, BlockEntityFactory<T> factory, RegistryObject<? extends AEBaseEntityBlock<?>>... blockObjects) {
        Preconditions.checkArgument(blockObjects.length > 0, "Must provide at least one block!");

        return BLOCK_ENTITIES.register(id, () -> {
            AEBaseEntityBlock<?>[] blocks = Arrays.stream(blockObjects)
                    .map(RegistryObject::get)
                    .toArray(AEBaseEntityBlock[]::new);

            AtomicReference<BlockEntityType<T>> typeHolder = new AtomicReference<>();
            BlockEntityType.BlockEntitySupplier<T> supplier = (blockPos, blockState) ->
                    factory.create(typeHolder.get(), blockPos, blockState);

            BlockEntityType<T> type = BlockEntityType.Builder.of(supplier, blocks).build(null);
            typeHolder.set(type);

            AEBaseBlockEntity.registerBlockEntityItem(type, blocks[0].asItem());

            BlockEntityTicker<T> serverTicker = null;
            if (ServerTickingBlockEntity.class.isAssignableFrom(entityClass)) {
                serverTicker = (level, pos, state, entity) -> {
                    ((ServerTickingBlockEntity) entity).serverTick();;
                };
            }

            BlockEntityTicker<T> clientTicker = null;
            if (ClientTickingBlockEntity.class.isAssignableFrom(entityClass)) {
                clientTicker = (level, pos, state, entity) -> {
                    ((ClientTickingBlockEntity) entity).clientTick();
                };
            }

            for (var block : blocks) {
                AEBaseEntityBlock<T> baseBlock = (AEBaseEntityBlock<T>) block;
                baseBlock.setBlockEntity(entityClass, type, clientTicker, serverTicker);
            }

            return type;
        });
    }

    public interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
