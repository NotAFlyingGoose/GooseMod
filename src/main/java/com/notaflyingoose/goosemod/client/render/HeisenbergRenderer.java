package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.HeisenbergModel;
import com.notaflyingoose.goosemod.entities.Heisenberg;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class HeisenbergRenderer<T extends Heisenberg, M extends HeisenbergModel<T>> extends HumanoidMobRenderer<T, M> {
    private static final ResourceLocation HEISENBERG_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/heisenberg.png");

    public HeisenbergRenderer(EntityRendererProvider.Context renderProvider) {
        this(renderProvider, ModModelLayers.HEISENBERG, ModModelLayers.HEISENBERG_INNER_ARMOR, ModModelLayers.HEISENBERG_OUTER_ARMOR);
    }

    public HeisenbergRenderer(EntityRendererProvider.Context renderProvider, ModelLayerLocation baseLayer, ModelLayerLocation innerArmor, ModelLayerLocation outerArmor) {
        this(renderProvider,
                (M) new HeisenbergModel<T>(renderProvider.bakeLayer(baseLayer)),
                (M) new HeisenbergModel<T>(renderProvider.bakeLayer(innerArmor)),
                (M) new HeisenbergModel<T>(renderProvider.bakeLayer(outerArmor)));
    }

    public HeisenbergRenderer(EntityRendererProvider.Context renderProvider, M baseLayer, M innerArmor, M outerArmor) {
        super(renderProvider, baseLayer, 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, innerArmor, outerArmor));
    }

    @Override
    public ResourceLocation getTextureLocation(Heisenberg entity) {
        return HEISENBERG_LOCATION;
    }
}
