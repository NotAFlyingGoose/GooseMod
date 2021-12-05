package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.AmongusModel;
import com.notaflyingoose.goosemod.entities.Crewmate;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class CrewmateRenderer extends MobRenderer<Crewmate, AmongusModel<Crewmate>> {
    public static final ResourceLocation RED_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/red.png");
    public static final ResourceLocation BLACK_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/black.png");
    public static final ResourceLocation WHITE_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/white.png");
    public static final ResourceLocation BLUE_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/blue.png");
    public static final ResourceLocation CYAN_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/cyan.png");
    public static final ResourceLocation YELLOW_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/yellow.png");
    public static final ResourceLocation PINK_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/pink.png");
    public static final ResourceLocation PURPLE_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/purple.png");
    public static final ResourceLocation ORANGE_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/orange.png");
    public static final ResourceLocation LIME_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/lime.png");
    public static final ResourceLocation GREEN_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/green.png");
    public static final ResourceLocation GRAY_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/gray.png");
    public static final ResourceLocation BROWN_LOCATION = new ResourceLocation(GooseMod.MODID, "textures/entity/amongus/brown.png");

    public CrewmateRenderer(EntityRendererProvider.Context renderProvider) {
        super(renderProvider, new AmongusModel<>(renderProvider.bakeLayer(ModModelLayers.AMONGUS)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(Crewmate crewmate) {
        return switch(crewmate.getColor()) {
            default -> CrewmateRenderer.RED_LOCATION;
            case BLACK -> CrewmateRenderer.BLACK_LOCATION;
            case WHITE -> CrewmateRenderer.WHITE_LOCATION;
            case BLUE -> CrewmateRenderer.BLUE_LOCATION;
            case CYAN -> CrewmateRenderer.CYAN_LOCATION;
            case YELLOW -> CrewmateRenderer.YELLOW_LOCATION;
            case PINK -> CrewmateRenderer.PINK_LOCATION;
            case PURPLE -> CrewmateRenderer.PURPLE_LOCATION;
            case ORANGE -> CrewmateRenderer.ORANGE_LOCATION;
            case LIME -> CrewmateRenderer.LIME_LOCATION;
            case GREEN -> CrewmateRenderer.GREEN_LOCATION;
            case GRAY -> CrewmateRenderer.GRAY_LOCATION;
            case BROWN -> CrewmateRenderer.BROWN_LOCATION;
        };
    }
}
