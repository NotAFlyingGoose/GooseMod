package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.LemurModel;
import com.notaflyingoose.goosemod.entities.Lemur;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LemurRenderer extends MobRenderer<Lemur, LemurModel<Lemur>> {

    private static final ResourceLocation NORMAL_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/lemur/ring_tailed.png");
    private static final ResourceLocation SPECIAL_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/lemur/julien.png");

    public LemurRenderer(EntityRendererProvider.Context renderProvider) {
        super(renderProvider, new LemurModel<>(renderProvider.bakeLayer(ModModelLayers.LEMUR)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(Lemur lemur) {
        if (lemur.hasCustomName() && lemur.getName().getContents().equalsIgnoreCase("julien"))
            return SPECIAL_LOCATION;
        return NORMAL_LOCATION;
    }
}
