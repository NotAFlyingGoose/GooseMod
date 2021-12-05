package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.GooseModel;
import com.notaflyingoose.goosemod.entities.Goose;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GooseRenderer extends MobRenderer<Goose, GooseModel<Goose>> {
    private static final ResourceLocation NORMAL_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/goose/normal.png");
    private static final ResourceLocation SPECIAL_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/goose/no_wings.png");

    public GooseRenderer(EntityRendererProvider.Context p_173952_) {
        super(p_173952_, new GooseModel<>(p_173952_.bakeLayer(ModModelLayers.GOOSE)), 0.3F);
        this.addLayer(new GooseHeldItemLayer(this));
    }

    public ResourceLocation getTextureLocation(Goose goose) {
        if (goose.hasCustomName() && goose.getName().getContents().equals("notaflyinggoose"))
            return SPECIAL_LOCATION;
        return NORMAL_LOCATION;
    }

    protected float getBob(Goose p_114000_, float p_114001_) {
        float f = Mth.lerp(p_114001_, p_114000_.oFlap, p_114000_.flap);
        float f1 = Mth.lerp(p_114001_, p_114000_.oFlapSpeed, p_114000_.flapSpeed);
        return (Mth.sin(f) + 1.0F) * f1;
    }
}
