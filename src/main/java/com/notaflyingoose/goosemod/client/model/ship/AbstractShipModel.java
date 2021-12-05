package com.notaflyingoose.goosemod.client.model.ship;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.notaflyingoose.goosemod.entities.vehicle.AbstractShip;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractShipModel extends ListModel<AbstractShip> {
    protected final ModelPart leftPaddle;
    protected final ModelPart rightPaddle;
    protected final ModelPart waterPatch;
    protected ImmutableList<ModelPart> parts;

    public AbstractShipModel(ModelPart root, ModelPart... extraParts) {
        this.leftPaddle = root.getChild("left_paddle");
        this.rightPaddle = root.getChild("right_paddle");
        this.waterPatch = root.getChild("water_patch");
        if (extraParts.length >= 1) {
            this.parts = new ImmutableList.Builder<ModelPart>().add(root.getChild("bottom"), root.getChild("back"), root.getChild("front"), root.getChild("right"), root.getChild("left"), this.leftPaddle, this.rightPaddle).add(extraParts).build();
        } else {
            this.parts = ImmutableList.of(root.getChild("bottom"), root.getChild("back"), root.getChild("front"), root.getChild("right"), root.getChild("left"), this.leftPaddle, this.rightPaddle);
        }
    }

    public ImmutableList<ModelPart> parts() {
        return this.parts;
    }

    public ModelPart waterPatch() {
        return this.waterPatch;
    }

    protected static void animatePaddle(AbstractShip p_170465_, int p_170466_, ModelPart p_170467_, float p_170468_) {
        float f = p_170465_.getRowingTime(p_170466_, p_170468_);
        p_170467_.xRot = Mth.clampedLerp((-(float)Math.PI / 3F), -0.2617994F, (Mth.sin(-f) + 1.0F) / 2.0F);
        p_170467_.yRot = Mth.clampedLerp((-(float)Math.PI / 4F), ((float)Math.PI / 4F), (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F);
        if (p_170466_ == 1) {
            p_170467_.yRot = (float)Math.PI - p_170467_.yRot;
        }
    }
}
