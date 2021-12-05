package com.notaflyingoose.goosemod.client.model;

import com.google.common.collect.ImmutableList;
import com.notaflyingoose.goosemod.entities.Crewmate;
import com.notaflyingoose.goosemod.entities.Lemur;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

public class LemurModel<T extends Lemur> extends AgeableListModel<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ModelPart leftLegBack;
    private final ModelPart rightLegBack;
    private final ModelPart leftLegFront;
    private final ModelPart rightLegFront;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart tailEnd;

    public LemurModel(ModelPart model) {
        this(model, RenderType::entityCutoutNoCull);
    }

    public LemurModel(ModelPart model, Function<ResourceLocation, RenderType> function) {
        super(function, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
        this.leftLegBack = model.getChild("left_leg_back");
        this.rightLegBack = model.getChild("right_leg_back");
        this.leftLegFront = model.getChild("left_leg_front");
        this.rightLegFront = model.getChild("right_leg_front");
        this.body = model.getChild("body");
        this.head = model.getChild("head");
        this.tail = model.getChild("tail");
        this.tailEnd = tail.getChild("tail_end");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition leftLegBack = partdefinition.addOrReplaceChild("left_leg_back", CubeListBuilder.create().texOffs(13, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 20.0F, 6.0F));

        PartDefinition rightLegBack = partdefinition.addOrReplaceChild("right_leg_back", CubeListBuilder.create().texOffs(0, 6).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 20.0F, 6.0F));

        PartDefinition leftLegFront = partdefinition.addOrReplaceChild("left_leg_front", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 20.0F, -5.0F));

        PartDefinition rightLegFront = partdefinition.addOrReplaceChild("right_leg_front", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 20.0F, -5.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -6.5F, 4.0F, 4.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 0.5F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 17).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(21, 8).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 6).addBox(1.0F, -4.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(6, 0).addBox(-2.0F, -4.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 0).addBox(-2.5F, -6.0F, -3.5F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, -6.0F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(13, 19).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, 6.0F));

        PartDefinition tailEnd = tail.addOrReplaceChild("tail_end", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 9.0F));

        return LayerDefinition.create(meshdefinition, 48, 48);
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.head, this.body, this.rightLegBack, this.rightLegFront, this.leftLegBack, this.leftLegFront, this.tail);
    }

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.tail.yRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }

    public void setupAnim(Lemur lemur, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch) {
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.head.yRot = headYaw * ((float)Math.PI / 180F);
        this.rightLegBack.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLegBack.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.rightLegFront.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.leftLegFront.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.tail.xRot = lemur.getHealth() / lemur.getMaxHealth() * 45 * ((float)Math.PI / 180F);
        this.tailEnd.xRot = lemur.getHealth() / lemur.getMaxHealth() * -45 * ((float)Math.PI / 180F);
        this.tail.yRot += Mth.cos(age/2) * 0.01F;
    }


}
