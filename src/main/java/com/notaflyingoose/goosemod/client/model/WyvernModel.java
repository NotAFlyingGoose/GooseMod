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
import net.minecraft.world.entity.Mob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class WyvernModel<T extends Mob> extends EntityModel<T> {
    protected final ModelPart neck;
    protected final ModelPart head;
    protected final ModelPart jaw;
    protected final ModelPart body;
    protected final ModelPart rightWing;
    protected final ModelPart leftWing;
    protected final ModelPart rightWingHand;
    protected final ModelPart leftWingHand;
    protected final ModelPart rightWingTip;
    protected final ModelPart leftWingTip;
    protected final ModelPart rightRearLeg;
    protected final ModelPart leftRearLeg;
    protected final ModelPart rightRearLegTip;
    protected final ModelPart leftRearLegTip;
    protected final ModelPart rightRearFoot;
    protected final ModelPart leftRearFoot;
    protected final ModelPart tail;
    @Nullable
    protected T entity;
    protected float a;

    public WyvernModel(ModelPart root) {
        this.neck = root.getChild("neck");
        this.head = neck.getChild("neck_2").getChild("neck_3").getChild("neck_4").getChild("neck_5").getChild("head");
        this.jaw = head.getChild("jaw");
        this.body = root.getChild("body");
        this.rightWing = root.getChild("right_wing");
        this.leftWing = root.getChild("left_wing");
        this.rightWingHand = rightWing.getChild("right_hand");
        this.leftWingHand = leftWing.getChild("left_hand");
        this.rightWingTip = rightWing.getChild("right_wing_tip");
        this.leftWingTip = leftWing.getChild("left_wing_tip");
        this.rightRearLeg = root.getChild("right_rear_leg");
        this.leftRearLeg = root.getChild("left_rear_leg");
        this.rightRearLegTip = rightRearLeg.getChild("right_rear_leg_tip");
        this.leftRearLegTip = leftRearLeg.getChild("left_rear_leg_tip");
        this.rightRearFoot = rightRearLegTip.getChild("right_rear_foot");
        this.leftRearFoot = leftRearLegTip.getChild("left_rear_foot");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition neckcenter = partdefinition.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 57).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 17.0F, -8.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition neck2 = neckcenter.addOrReplaceChild("neck_2", CubeListBuilder.create().texOffs(0, 57).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition neck3 = neck2.addOrReplaceChild("neck_3", CubeListBuilder.create().texOffs(0, 57).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition neck4 = neck3.addOrReplaceChild("neck_4", CubeListBuilder.create().texOffs(0, 57).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition neck5 = neck4.addOrReplaceChild("neck_5", CubeListBuilder.create().texOffs(0, 57).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition head = neck5.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 114).addBox(-6.0F, -7.0F, -30.0F, 12.0F, 11.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(169, 57).addBox(-8.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 45).addBox(-6.0F, 4.0F, -30.1F, 12.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(27, 64).addBox(6.1F, 4.0F, -30.0F, 0.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(27, 64).addBox(-6.1F, 4.0F, -30.0F, 0.0F, 3.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition head_r3 = head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(0, 78).addBox(12.0F, -45.8533F, -46.4969F, 4.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0F, 58.0F, 0.5303F, 0.151F, 0.0879F));

        PartDefinition head_r4 = head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(0, 78).mirror().addBox(-16.0F, -45.8533F, -47.4969F, 4.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 7.0F, 58.0F, 0.5303F, -0.151F, -0.0879F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 142).addBox(-6.0F, 0.0F, -17.0F, 12.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, -13.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 114).addBox(-12.0F, 0.0F, -16.0F, 24.0F, 24.0F, 64.0F, new CubeDeformation(0.0F))
                .texOffs(25, 85).addBox(-1.0F, -6.0F, -10.0F, 2.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(25, 85).addBox(-1.0F, -6.0F, 10.0F, 2.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(25, 85).addBox(-1.0F, -6.0F, 30.0F, 2.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 8.0F));

        PartDefinition wing = partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(113, 114).addBox(-56.0F, -4.0F, -4.0F, 56.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 57).addBox(-56.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-12.0F, 6.0F, 2.0F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition wingtip = wing.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(113, 131).addBox(-56.0F, -2.0F, 0.0F, 56.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-56.0F, 0.0F, 4.0F, 56.0F, 0.0F, 56.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-56.0F, 0.0F, -2.0F, 0.0F, 0.0F, -0.3491F));

        PartDefinition fronthand = wing.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(174, 140).addBox(-136.0F, 0.0F, -10.0F, 4.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(80.0F, 1.0F, -7.0F, 1.0472F, 0.0F, 0.0F));

        PartDefinition wing1 = partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(113, 114).mirror().addBox(0.0F, -4.0F, -4.0F, 56.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 57).mirror().addBox(0.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(12.0F, 5.0F, 2.0F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition wingtip1 = wing1.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(113, 131).mirror().addBox(0.0F, -2.0F, 0.0F, 56.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).mirror().addBox(0.0F, 0.0F, 4.0F, 56.0F, 0.0F, 56.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(56.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.3491F));

        PartDefinition fronthand1 = wing1.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(174, 140).addBox(-4.0F, 0.0F, -10.0F, 4.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(56.0F, 1.0F, -7.0F, 1.0472F, 0.0F, 0.0F));

        PartDefinition rearleg = partdefinition.addOrReplaceChild("right_rear_leg", CubeListBuilder.create().texOffs(169, 0).addBox(-8.0F, -4.0F, -8.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-16.0F, 16.0F, 42.0F, 1.0472F, 0.0F, 0.0F));

        PartDefinition rearlegtip = rearleg.addOrReplaceChild("right_rear_leg_tip", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 0.0F, -7.0F, 12.0F, 32.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 28.0F, 1.0F, 0.4363F, 0.0F, 0.0F));

        PartDefinition rearfoot = rearlegtip.addOrReplaceChild("right_rear_foot", CubeListBuilder.create().texOffs(113, 140).addBox(-9.0F, 0.0F, -20.0F, 18.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 32.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition rearleg1 = partdefinition.addOrReplaceChild("left_rear_leg", CubeListBuilder.create().texOffs(169, 0).mirror().addBox(-8.0F, -4.0F, -8.0F, 16.0F, 32.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(16.0F, 16.0F, 42.0F, 1.0472F, 0.0F, 0.0F));

        PartDefinition rearlegtip1 = rearleg1.addOrReplaceChild("left_rear_leg_tip", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-6.0F, 0.0F, -7.0F, 12.0F, 32.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 28.0F, 1.0F, 0.4363F, 0.0F, 0.0F));

        PartDefinition rearfoot1 = rearlegtip1.addOrReplaceChild("left_rear_foot", CubeListBuilder.create().texOffs(113, 140).mirror().addBox(-9.0F, 0.0F, -20.0F, 18.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 32.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 57).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 56.0F));

        PartDefinition tail2 = tail.addOrReplaceChild("tail_2", CubeListBuilder.create().texOffs(0, 57).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0175F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail_3", CubeListBuilder.create().texOffs(0, 57).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0175F, 0.0F, 0.0F));

        PartDefinition tail4 = tail3.addOrReplaceChild("tail_4", CubeListBuilder.create().texOffs(169, 90).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -8.0014F, 1.9477F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0175F, 0.0F, 0.0F));

        PartDefinition tail5 = tail4.addOrReplaceChild("tail_5", CubeListBuilder.create().texOffs(169, 90).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -8.0038F, 1.9128F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0349F, 0.0F, 0.0F));

        PartDefinition tail6 = tail5.addOrReplaceChild("tail_6", CubeListBuilder.create().texOffs(169, 90).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -8.0097F, 1.8608F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0524F, 0.0F, 0.0F));

        PartDefinition tail7 = tail6.addOrReplaceChild("tail_7", CubeListBuilder.create().texOffs(177, 171).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -7.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0524F, 0.0F, 0.0F));

        PartDefinition tail8 = tail7.addOrReplaceChild("tail_8", CubeListBuilder.create().texOffs(177, 171).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -7.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0175F, 0.0F, 0.0F));

        PartDefinition tail9 = tail8.addOrReplaceChild("tail_9", CubeListBuilder.create().texOffs(177, 171).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -7.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, -0.0175F, 0.0F, 0.0F));

        PartDefinition tail10 = tail9.addOrReplaceChild("tail_10", CubeListBuilder.create().texOffs(0, 97).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-1.0F, -6.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, -0.0349F, 0.0F, 0.0F));

        PartDefinition tail11 = tail10.addOrReplaceChild("tail_11", CubeListBuilder.create().texOffs(0, 97).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(177, 188).addBox(-1.0F, -8.0164F, -0.3136F, 2.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, -0.0524F, 0.0F, 0.0F));

        PartDefinition tail12 = tail11.addOrReplaceChild("tail_12", CubeListBuilder.create().texOffs(0, 97).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(177, 188).addBox(-1.0F, -8.0041F, -0.157F, 2.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, -0.0524F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        this.entity = entity;
        this.a = partialTicks;
        this.tail.xRot = Mth.cos(limbSwing * 0.6662F) * 0.1F * limbSwingAmount;
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        setupSleepingAnimation(this);
    }

    public static void setupSleepingAnimation(WyvernModel<?> model) {
        model.leftWing.xRot = 0.1f;
        model.leftWing.yRot = -0.3f;
        model.leftWing.zRot = 0.4f;
        model.leftWingTip.zRot = -0.4f;
        model.leftWingHand.xRot = -0.25f;
        model.rightWing.xRot = model.leftWing.xRot;
        model.rightWing.yRot = model.leftWing.yRot;
        model.rightWing.zRot = -model.leftWing.zRot;
        model.rightWingTip.zRot = -model.leftWingTip.zRot;
        model.rightWingHand.xRot = -model.leftWingHand.xRot;

        model.leftRearLeg.xRot = 1.4f;
        model.leftRearLegTip.xRot = 0.16f;
        model.leftRearFoot.xRot = 0.95f;
        model.rightRearLeg.xRot = model.leftRearLeg.xRot;
        model.rightRearLegTip.xRot = model.leftRearLegTip.xRot;
        model.rightRearFoot.xRot = model.leftRearFoot.xRot;

        model.tail.xRot = -0.3f;
        model.tail.yRot = 0;

        model.neck.xRot = -0.2f;
        model.neck.yRot = 0.05f;
        model.neck.zRot = -0.6f;

        model.jaw.xRot = 0;
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.translate(0.0D, -0.25, -3.0D);
        renderAllParts(poseStack, buffer, packedLight, packedOverlay);
    }

    public void renderAllParts(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
        neck.render(poseStack, buffer, packedLight, packedOverlay);
        body.render(poseStack, buffer, packedLight, packedOverlay);
        rightWing.render(poseStack, buffer, packedLight, packedOverlay);
        leftWing.render(poseStack, buffer, packedLight, packedOverlay);
        rightRearLeg.render(poseStack, buffer, packedLight, packedOverlay);
        leftRearLeg.render(poseStack, buffer, packedLight, packedOverlay);
        tail.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
