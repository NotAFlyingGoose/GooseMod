package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.LemurModel;
import com.notaflyingoose.goosemod.client.model.MimicModel;
import com.notaflyingoose.goosemod.entities.Lemur;
import com.notaflyingoose.goosemod.entities.Mimic;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MimicRenderer extends MobRenderer<Mimic, MimicModel> {

    protected static final ResourceLocation MIMIC_LOCATION = new ResourceLocation("minecraft", "textures/entity/chest/normal.png");

    public MimicRenderer(EntityRendererProvider.Context renderProvider) {
        super(renderProvider, new MimicModel(renderProvider.bakeLayer(ModModelLayers.MIMIC)), 0.45f);
    }

    @Override
    public ResourceLocation getTextureLocation(Mimic mimic) {
        return MIMIC_LOCATION;
    }
}
