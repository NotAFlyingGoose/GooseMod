package com.notaflyingoose.goosemod.client.render;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.entities.vehicle.AbstractShip;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class ModModelLayers {
    public static final ModelLayerLocation AMONGUS = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "amongus"), "main");
    public static final ModelLayerLocation WYVERN = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "wyvern"), "main");
    public static final ModelLayerLocation HEROBRINE = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "herobrine"), "main");
    public static final ModelLayerLocation HEROBRINE_INNER_ARMOR = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "herobrine"), "inner_armor");
    public static final ModelLayerLocation HEROBRINE_OUTER_ARMOR = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "herobrine"), "outer_armor");
    public static final ModelLayerLocation HEISENBERG = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "heisenberg"), "main");
    public static final ModelLayerLocation HEISENBERG_INNER_ARMOR = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "heisenberg"), "inner_armor");
    public static final ModelLayerLocation HEISENBERG_OUTER_ARMOR = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "heisenberg"), "outer_armor");
    public static final ModelLayerLocation LEMUR = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "lemur"), "main");
    public static final ModelLayerLocation GOOSE = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "goose"), "main");
    public static final ModelLayerLocation MIMIC = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "mimic"), "main");
    public static final ModelLayerLocation COPPER_GOLEM = new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, "copper_golem"), "main");

    public static ModelLayerLocation createShipModelName(AbstractShip.Variant variant, Boat.Type boatType) {
        return new ModelLayerLocation(new ResourceLocation(GooseMod.MODID, variant.getName() + "/" + boatType.getName()), "main");
    }
}
