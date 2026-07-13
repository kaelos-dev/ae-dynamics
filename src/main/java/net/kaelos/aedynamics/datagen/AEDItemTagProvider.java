package net.kaelos.aedynamics.datagen;

import net.kaelos.aedynamics.AED;
import net.kaelos.aedynamics.item.AEDItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class AEDItemTagProvider extends ItemTagsProvider {
    public AEDItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> lookupCompletableFuture, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, completableFuture, lookupCompletableFuture, AED.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(Tags.Items.GEMS)
                .add(AEDItems.EMPIRITIC_CRYSTAL.get());

        TagKey<Item> empiriticCrystalTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "gems/empiritic_crystal"));
        tag(empiriticCrystalTag)
                .add(AEDItems.EMPIRITIC_CRYSTAL.get());
    }
}
