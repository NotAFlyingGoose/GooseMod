package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.AmongusModel;
import com.notaflyingoose.goosemod.client.model.CopperGolemModel;
import com.notaflyingoose.goosemod.entities.CopperGolem;
import com.notaflyingoose.goosemod.entities.Imposter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CopperGolemRenderer extends MobRenderer<CopperGolem, CopperGolemModel<CopperGolem>> {
    public static final ResourceLocation LEVEL0_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/copper_golem/level0.png");

    public CopperGolemRenderer(EntityRendererProvider.Context renderProvider) {
        super(renderProvider, new CopperGolemModel<>(renderProvider.bakeLayer(ModModelLayers.COPPER_GOLEM)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(CopperGolem entity) {
        return LEVEL0_LOCATION;
    }
}
