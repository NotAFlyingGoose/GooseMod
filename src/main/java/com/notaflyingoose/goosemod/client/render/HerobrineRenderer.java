package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.HerobrineModel;
import com.notaflyingoose.goosemod.entities.boss.herobrine.HerobrineBoss;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class HerobrineRenderer<T extends HerobrineBoss, M extends HerobrineModel<T>> extends HumanoidMobRenderer<T, M> {
    private static final ResourceLocation NORMAL_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/herobrine/scary.png");
    private static final ResourceLocation SPECIAL_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/herobrine/dream.png");

    public HerobrineRenderer(EntityRendererProvider.Context renderProvider) {
        this(renderProvider, ModModelLayers.HEROBRINE, ModModelLayers.HEROBRINE_INNER_ARMOR, ModModelLayers.HEROBRINE_OUTER_ARMOR);
    }

    public HerobrineRenderer(EntityRendererProvider.Context renderProvider, ModelLayerLocation baseLayer, ModelLayerLocation innerArmor, ModelLayerLocation outerArmor) {
        this(renderProvider,
                (M) new HerobrineModel<T>(renderProvider.bakeLayer(baseLayer)),
                (M) new HerobrineModel<T>(renderProvider.bakeLayer(innerArmor)),
                (M) new HerobrineModel<T>(renderProvider.bakeLayer(outerArmor)));
    }

    public HerobrineRenderer(EntityRendererProvider.Context renderProvider, M baseLayer, M innerArmor, M outerArmor) {
        super(renderProvider, baseLayer, 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, innerArmor, outerArmor));
    }

    @Override
    public ResourceLocation getTextureLocation(HerobrineBoss entity) {
        if (entity.hasCustomName() && entity.getName().getContents().equals("Dream"))
            return SPECIAL_LOCATION;
        return NORMAL_LOCATION;
    }
}
