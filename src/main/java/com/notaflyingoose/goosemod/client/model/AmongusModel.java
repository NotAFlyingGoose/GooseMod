package com.notaflyingoose.goosemod.client.model;

import com.google.common.collect.ImmutableList;
import com.notaflyingoose.goosemod.entities.Imposter;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

import java.util.function.Function;

public class AmongusModel<T extends Mob> extends AgeableListModel<T> {
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart body;
    private final ModelPart jaw;
    private final ModelPart bottom_teeth;
    private final ModelPart right_corner_tooth;
    private final ModelPart left_corner_tooth;
    private final ModelPart head;
    private final ModelPart top_teeth;
    private float headXRot;

    public AmongusModel(ModelPart model) {
        this(model, RenderType::entityCutoutNoCull);
    }

    public AmongusModel(ModelPart model, Function<ResourceLocation, RenderType> function) {
        super(function, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
        this.rightLeg = model.getChild("right_leg");
        this.leftLeg = model.getChild("left_leg");
        this.body = model.getChild("body");
        this.jaw = this.body.getChild("jaw");
        this.bottom_teeth = this.jaw.getChild("bottom_teeth");
        this.right_corner_tooth = this.bottom_teeth.getChild("right_corner_tooth");
        this.left_corner_tooth = this.bottom_teeth.getChild("left_corner_tooth");
        this.head = this.body.getChild("head");
        this.top_teeth = this.head.getChild("top_teeth");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0, 6, 0));
        // jaw
        PartDefinition jaw = body.addOrReplaceChild("jaw",
                CubeListBuilder.create()
                        .texOffs(34, 21).addBox(-4.0F, -7.0F, 5.0F, 8.0F, 4.0F, 3.0F)
                        .texOffs(0, 21).addBox(-6.0F, -7.0F, -5.0F, 12.0F, 7.0F, 10.0F),
                PartPose.offset(0, 0, 0));
        PartDefinition bottom_teeth = jaw.addOrReplaceChild("bottom_teeth",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.5F, -8.0F, -4.0F, 1.0F, 1.0F, 0.0F)
                        .texOffs(0, 0).addBox(0.5F, -8.0F, -4.0F, 1.0F, 1.0F, 0.0F)
                        .texOffs(0, 0).addBox(-3.5F, -8.0F, -4.0F, 1.0F, 1.0F, 0.0F)
                        .texOffs(0, 0).addBox(2.5F, -8.0F, -4.0F, 1.0F, 1.0F, 0.0F)
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, -3.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, -1.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, 1.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, 3.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(5.0F, -8.0F, -3.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(5.0F, -8.0F, 3.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(5.0F, -8.0F, -1.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(5.0F, -8.0F, 1.0F, 0.0F, 1.0F, 1.0F),
                PartPose.ZERO);
        bottom_teeth.addOrReplaceChild("right_corner_tooth",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(0.0F, -8.0F, -6.0F, 1.0F, 1.0F, 0.0F),
                PartPose.rotation(0.0F, -0.7854F, 0.0F));
        bottom_teeth.addOrReplaceChild("left_corner_tooth",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.0F, -8.0F, -6.0F, 1.0F, 1.0F, 0.0F),
                PartPose.rotation(0.0F, 0.7854F, 0.0F));
        // head
        PartDefinition head = body.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-6.0F, -11.0F, -10.0F, 12.0F, 11.0F, 10.0F)
                        .texOffs(34, 0).addBox(-4.0F, -6.0F, 0.0F, 8.0F, 6.0F, 3.0F)
                        .texOffs(32, 38).addBox(-4.0F, -9.0F, -11.0F, 8.0F, 6.0F, 1.0F),
                PartPose.offset(0, -7, 5));
        head.addOrReplaceChild("top_teeth",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-0.5F, -8.0F, -4.0F, 1.0F, 1.0F, 0.0F)
                        .texOffs(0, 0).addBox(1.5F, -8.0F, -4.0F, 1.0F, 1.0F, 0.0F)
                        .texOffs(0, 0).addBox(-2.5F, -8.0F, -4.0F, 1.0F, 1.0F, 0.0F)
                        .texOffs(0, 0).addBox(3.5F, -8.0F, -4.0F, 1.0F, 1.0F, 0.0F)
                        .texOffs(0, 0).addBox(-4.5F, -8.0F, -4.0F, 1.0F, 1.0F, 0.0F)
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, -2.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, 0.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, 2.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(5.0F, -8.0F, 2.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(5.0F, -8.0F, 0.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(5.0F, -8.0F, -2.0F, 0.0F, 1.0F, 1.0F)
                        .texOffs(0, 0).addBox(5.0F, -8.0F, -4.0F, 0.0F, 1.0F, 1.0F),
                PartPose.offset(0.0F, 8.0F, -5.0F));

        // legs
        partdefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(22, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(4, -6, 0));
        partdefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(34, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(-4, -6, 0));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightLeg, this.leftLeg);
    }

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        if (entity instanceof Imposter imposter)
            this.headXRot = imposter.getHeadEatAngleScale(partialTicks);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        limbSwingAmount = Math.min(0.25F, limbSwingAmount);
        this.body.xRot = headPitch * ((float)Math.PI / 180F) + this.headXRot/2;
        this.body.yRot = headYaw * ((float)Math.PI / 180F);
        this.body.zRot = 0.1F * Mth.sin(limbSwing * 0.5f) * 4.0F * limbSwingAmount;
        this.body.y = 19 - Mth.cos(limbSwing * 0.375f) * 1.0F * limbSwingAmount;

        this.head.y = -6;
        //this.head.xRot = -Mth.abs(Mth.sin(limbSwing * 0.375f * 0.5F + (float)Math.PI)) * 2.0F * limbSwingAmount;
        this.head.xRot = -this.headXRot;

        this.leftLeg.xRot = Mth.sin(limbSwing * 0.375f * 0.5F) * 2.0F * limbSwingAmount;
        this.rightLeg.xRot = Mth.sin(limbSwing * 0.375f * 0.5F + (float)Math.PI) * 2.0F * limbSwingAmount;
        this.leftLeg.zRot = 0.17453292F * Mth.cos(limbSwing * 0.375f * 0.5F) * limbSwingAmount;
        this.rightLeg.zRot = 0.17453292F * Mth.cos(limbSwing * 0.375f * 0.5F + (float)Math.PI) * limbSwingAmount;
        this.leftLeg.y = 19 + Mth.sin(limbSwing * 0.375f * 0.5F + (float)Math.PI) * 2.0F * limbSwingAmount;
        this.rightLeg.y = 19 + Mth.sin(limbSwing * 0.375f * 0.5F) * 2.0F * limbSwingAmount;
    }


}
