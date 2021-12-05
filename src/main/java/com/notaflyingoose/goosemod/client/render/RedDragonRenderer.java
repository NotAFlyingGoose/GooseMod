package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.RedDragonModel;
import com.notaflyingoose.goosemod.entities.RedDragon;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RedDragonRenderer<T extends RedDragon, M extends RedDragonModel<T>> extends MobRenderer<T, M> {
    private static final ResourceLocation NORMAL_TEXTURE_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/red_dragon/normal.png");
    private static final ResourceLocation SLEEPING_TEXTURE_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/red_dragon/sleeping.png");

    public RedDragonRenderer(EntityRendererProvider.Context renderProvider) {
        this(renderProvider, ModModelLayers.WYVERN);
    }

    public RedDragonRenderer(EntityRendererProvider.Context renderProvider, ModelLayerLocation layer) {
        this(renderProvider, (M) new RedDragonModel(renderProvider.bakeLayer(layer)));
    }

    public RedDragonRenderer(EntityRendererProvider.Context renderProvider, M baseLayer) {
        super(renderProvider, baseLayer, 5F);
    }

    @Override
    public ResourceLocation getTextureLocation(RedDragon entity) {
        return entity.isDragonSleeping() ? SLEEPING_TEXTURE_LOCATION : NORMAL_TEXTURE_LOCATION;
    }
}
