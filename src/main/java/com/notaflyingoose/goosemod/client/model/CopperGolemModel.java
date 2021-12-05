package com.notaflyingoose.goosemod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class CopperGolemModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    private final ModelPart right_leg;
    private final ModelPart left_leg;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart body;
    private final ModelPart head;

    public CopperGolemModel(ModelPart root) {
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
        this.right_arm = root.getChild("right_arm");
        this.left_arm = root.getChild("left_arm");
        this.body = root.getChild("body");
        this.head = root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(26, 14).addBox(-2.0F, 1.0F, -2.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 19.0F, 1.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(25, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 20.0F, 0.5F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(10, 25).addBox(-2.0F, -1.0F, -2.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 14.0F, 0.5F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 25).addBox(0.0F, -1.0F, -2.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 14.0F, 0.5F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -10.0F, -2.5F, 8.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(20, 28).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 21).addBox(-2.0F, -11.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, -0.5F));

        return LayerDefinition.create(meshdefinition, 48, 48);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.right_leg.xRot = -1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.left_leg.xRot = 1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.right_leg.yRot = 0.0F;
        this.left_leg.yRot = 0.0F;
    }

    public void prepareMobModel(T p_102957_, float p_102958_, float p_102959_, float p_102960_) {
        this.right_arm.xRot = (-0.2F + 1.5F * Mth.triangleWave(p_102958_, 13.0F)) * p_102959_;
        this.left_arm.xRot = (-0.2F - 1.5F * Mth.triangleWave(p_102958_, 13.0F)) * p_102959_;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        right_leg.render(poseStack, buffer, packedLight, packedOverlay);
        left_leg.render(poseStack, buffer, packedLight, packedOverlay);
        right_arm.render(poseStack, buffer, packedLight, packedOverlay);
        left_arm.render(poseStack, buffer, packedLight, packedOverlay);
        body.render(poseStack, buffer, packedLight, packedOverlay);
        head.render(poseStack, buffer, packedLight, packedOverlay);
    }
}