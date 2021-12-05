package com.notaflyingoose.goosemod.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.notaflyingoose.goosemod.entities.Goose;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class GooseModel<T extends Mob> extends AgeableListModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public final ModelPart body;
    public final ModelPart right_leg;
    public final ModelPart left_leg;
    public final ModelPart right_wing;
    public final ModelPart left_wing;
    public final ModelPart neck;
    public final ModelPart head;
    public final ModelPart mouth;

    public GooseModel(ModelPart root) {
        this.body = root.getChild("body");
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
        this.right_wing = root.getChild("right_wing");
        this.left_wing = root.getChild("left_wing");
        this.neck = root.getChild("neck");
        this.head = this.neck.getChild("head");
        this.mouth = this.head.getChild("beak").getChild("mouth");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -3.0F, 8.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(31, 0).addBox(-3.0F, 6.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(34, 43).addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 19.0F, 2.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(37, 34).addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 19.0F, 2.0F));

        PartDefinition right_wing = partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 28).addBox(-1.0F, 0.0F, -5.0F, 1.0F, 7.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 12.0F, 0.0F));

        PartDefinition left_wing = partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(25, 11).addBox(0.0F, 0.0F, -5.0F, 1.0F, 7.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition neck = partdefinition.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, -4.0F));

        PartDefinition neck_r1 = neck.addOrReplaceChild("neck_r1", CubeListBuilder.create().texOffs(0, 34).addBox(-1.5F, -10.2966F, -0.9894F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 1.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 20).addBox(-3.0F, -7.0F, -2.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -3.0F));

        PartDefinition beak = head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(37, 11).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition mouth = beak.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(28, 28).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -2.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.right_leg, this.left_leg, this.right_wing, this.left_wing, this.neck);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.right_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.left_leg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.right_wing.zRot = ageInTicks;
        this.left_wing.zRot = -ageInTicks;
        if (entity.isAggressive()) {
            this.mouth.xRot = 0.5f;
        } else {
            this.mouth.xRot = 0;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}