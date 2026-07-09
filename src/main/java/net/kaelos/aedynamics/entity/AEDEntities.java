package net.kaelos.aedynamics.entity;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.CraftingBlockEntity;
import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.block.AEDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

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

    @SafeVarargs
    public static <T extends AEBaseBlockEntity> RegistryObject<BlockEntityType<T>> create(String id, Class<T> entityClass, BlockEntityFactory<T> factory, RegistryObject<? extends AEBaseEntityBlock<?>>... blockObject) {
        return BLOCK_ENTITIES.register(id, () -> {
            AEBaseEntityBlock<?>[] blocks = Arrays.stream(blockObject)
                    .map(RegistryObject::get)
                    .toArray(AEBaseEntityBlock[]::new);

            AtomicReference<BlockEntityType<T>> typeHolder = new AtomicReference<>();
            BlockEntityType<T> type = BlockEntityType.Builder.of(
                    (blockPos, blockState) -> factory.create(typeHolder.get(), blockPos, blockState),
                    blocks
            ).build(null);

            typeHolder.set(type);

            AEBaseBlockEntity.registerBlockEntityItem(type, blocks[0].asItem());

            for (AEBaseEntityBlock<?> block : blocks) {
                @SuppressWarnings("unchecked")
                AEBaseEntityBlock<T> typedBlock = (AEBaseEntityBlock<T>) block;
                typedBlock.setBlockEntity(entityClass, type, null, null);
            }

            return type;
        });
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }
}
