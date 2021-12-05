package com.notaflyingoose.goosemod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.notaflyingoose.goosemod.entities.RedDragon;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class RedDragonModel<T extends RedDragon> extends WyvernModel<T> {

    public RedDragonModel(ModelPart root) {
        super(root);
    }

    public void setupAnim(RedDragon entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.head.yRot = headYaw * ((float)Math.PI / 180F);

        this.tail.yRot = Mth.cos(ageInTicks/8) * 0.1F;
        this.tail.xRot += -Mth.sin(ageInTicks/8) * 0.1F;

        this.neck.zRot = 0;
        this.neck.yRot = 0;
        this.neck.xRot = (headPitch - 45) * ((float)Math.PI / 180F);
        this.neck.xRot -= Mth.cos(ageInTicks/4 + 3.14f) * 0.01F;

        if (!entity.isDragonSleeping()) {
            if (entity.isFlying()) {
                float flap = Mth.lerp(this.a, entity.oFlapTime, entity.flapTime) * ((float) Math.PI * 2F);
                this.leftWing.xRot = 0.125F - (float) Math.cos(flap) * 0.2F;
                this.leftWing.yRot = -0.25F;
                this.leftWing.zRot = -((float) (Math.sin(flap) + 0.125D)) * 0.8F;
                this.leftWingTip.zRot = (float) (Math.sin((flap + 2.0F)) + 0.5D) * 0.75F;
                this.leftWingHand.xRot = -0.25f;
                this.rightWing.xRot = this.leftWing.xRot;
                this.rightWing.yRot = -this.leftWing.yRot;
                this.rightWing.zRot = -this.leftWing.zRot;
                this.rightWingTip.zRot = -this.leftWingTip.zRot;
                this.rightWingHand.xRot = -this.leftWingHand.xRot;

                this.leftRearLeg.xRot = 1.04f;
                this.leftRearLegTip.xRot = 0.44f;
                this.leftRearFoot.xRot = 0.79f;
                this.rightRearLeg.xRot = this.leftRearLeg.xRot;
                this.rightRearLegTip.xRot = this.leftRearLegTip.xRot;
                this.rightRearFoot.xRot = this.leftRearFoot.xRot;
            } else {
                this.leftWing.xRot = -0.5f;
                this.leftWing.yRot = 0.1f;
                this.leftWing.zRot = 0.5f;
                this.leftWingTip.zRot = -2.3f;
                this.leftWingHand.xRot = 0.75f;
                this.rightWing.xRot = this.leftWing.xRot;
                this.rightWing.yRot = -this.leftWing.yRot;
                this.rightWing.zRot = -this.leftWing.zRot;
                this.rightWingTip.zRot = -this.leftWingTip.zRot;
                this.rightWingHand.xRot = this.leftWingHand.xRot;

                this.leftRearLeg.xRot = -0.6f + (Mth.sin(limbSwing * 0.375f * 0.25F) * 2.0F * limbSwingAmount);
                this.leftRearLegTip.xRot = 1.6f;
                this.leftRearFoot.xRot = -0.95f;
                this.rightRearLeg.xRot = -0.6f + (Mth.sin(limbSwing * 0.375f * 0.25F + (float) Math.PI) * 2.0F * limbSwingAmount);
                this.rightRearLegTip.xRot = 1.6f;
                this.rightRearFoot.xRot = -0.95f;
            }
        } else {
            setupSleepingAnimation(this);
        }
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!this.entity.isDragonSleeping()) {
            if (this.entity.isFlying()) {
                float f = Mth.lerp(this.a, this.entity.oFlapTime, this.entity.flapTime);
                float f1 = (float) (Math.sin((f * ((float) Math.PI * 2F) - 1.0F)) + 1.0D);
                f1 = (f1 * f1 + f1 * 2.0F) * 0.05F;
                poseStack.translate(0.0D, (f1 - 2F), -3.0D);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(f1 * 2.0F));
            } else {
                poseStack.translate(0.0D, -2.5, -3.0D);
            }
        } else {
            poseStack.translate(0.0D, -0.25, -3.0D);
        }
        renderAllParts(poseStack, buffer, packedLight, packedOverlay);
    }
}
