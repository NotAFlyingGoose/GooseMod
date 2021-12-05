package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.AmongusModel;
import com.notaflyingoose.goosemod.entities.Crewmate;
import com.notaflyingoose.goosemod.entities.Imposter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ImposterRenderer extends MobRenderer<Imposter, AmongusModel<Imposter>> {

    public ImposterRenderer(EntityRendererProvider.Context renderProvider) {
        super(renderProvider, new AmongusModel<>(renderProvider.bakeLayer(ModModelLayers.AMONGUS)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(Imposter imposter) {
        return switch(imposter.getColor()) {
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
            case BROWN -> CrewmateRenderer.BROWN_LOCATION;
        };
    }
}
