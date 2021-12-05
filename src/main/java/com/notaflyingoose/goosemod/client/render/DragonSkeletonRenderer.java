package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.WyvernModel;
import com.notaflyingoose.goosemod.entities.DragonSkeleton;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DragonSkeletonRenderer<T extends DragonSkeleton, M extends WyvernModel<T>> extends MobRenderer<T, M> {
    private static final ResourceLocation DRAGON_SKELETON_TEXTURE = new ResourceLocation(GooseMod.MODID, "textures/entity/red_dragon/skeleton.png");

    public DragonSkeletonRenderer(EntityRendererProvider.Context renderProvider) {
        this(renderProvider, ModModelLayers.WYVERN);
    }

    public DragonSkeletonRenderer(EntityRendererProvider.Context renderProvider, ModelLayerLocation layer) {
        this(renderProvider, (M) new WyvernModel<T>(renderProvider.bakeLayer(layer)));
    }

    public DragonSkeletonRenderer(EntityRendererProvider.Context renderProvider, M baseLayer) {
        super(renderProvider, baseLayer, 5F);
    }

    @Override
    public ResourceLocation getTextureLocation(DragonSkeleton entity) {
        return DRAGON_SKELETON_TEXTURE;
    }
}
