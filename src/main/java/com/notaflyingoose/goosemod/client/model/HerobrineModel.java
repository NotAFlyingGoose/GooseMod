package com.notaflyingoose.goosemod.client.model;

import com.notaflyingoose.goosemod.entities.boss.herobrine.HerobrineBoss;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class HerobrineModel<T extends HerobrineBoss> extends HumanoidModel<T> {

    public HerobrineModel(ModelPart model) {
        super(model);
    }

    public void prepareMobModel(T mob, float limbSwing, float limbSwingAmount, float partialTicks) {
        int i = mob.getAttackAnimationTick();
        if (i > 0) {
            this.rightArm.xRot = -2.0F + 1.5F * Mth.triangleWave(i - partialTicks, 10.0F);
            this.leftArm.xRot = -2.0F + 1.5F * Mth.triangleWave(i - partialTicks, 10.0F);
        }

    }

}
