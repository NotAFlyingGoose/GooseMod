package com.notaflyingoose.goosemod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.notaflyingoose.goosemod.entities.Mimic;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MimicModel extends EntityModel<Mimic> {
    private final ModelPart bottom;
    private final ModelPart top;

    public MimicModel(ModelPart root) {
        this.bottom = root.getChild("bottom");
        this.top = root.getChild("top");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).mirror().addBox(-7.0F, -9.0F, -7.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, -3.1416F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -1.0F, -1.0F, 14.0F, 5.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, -3.0F, 13.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 6.0F, -3.1416F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Mimic entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.top.xRot = -3.14159f;
        this.top.zRot = 0f;
        this.bottom.xRot = -3.14159f;
        this.bottom.zRot = 0f;
        if (entity.isAngry()) {
            this.top.xRot -= Mth.sin(ageInTicks) * 0.5F;
            this.top.xRot -= Math.abs(Mth.cos(ageInTicks / 2) * 1.4F);
            this.top.zRot -= Mth.cos(ageInTicks / 2) * 0.5F;

            this.bottom.xRot -= Mth.sin(ageInTicks) * 0.5F;
            this.bottom.zRot -= Mth.cos(ageInTicks / 2) * 0.5F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bottom.render(poseStack, buffer, packedLight, packedOverlay);
        top.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
