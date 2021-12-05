package com.notaflyingoose.goosemod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.notaflyingoose.goosemod.client.model.GooseModel;
import com.notaflyingoose.goosemod.entities.Goose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GooseHeldItemLayer extends RenderLayer<Goose, GooseModel<Goose>> {
    public GooseHeldItemLayer(RenderLayerParent<Goose, GooseModel<Goose>> p_116994_) {
        super(p_116994_);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int p_117009_, Goose goose, float p_117011_, float p_117012_, float p_117013_, float p_117014_, float p_117015_, float p_117016_) {
        boolean isBaby = goose.isBaby();
        poseStack.pushPose();
        //poseStack.mulPose(Vector3f.XP.rotationDegrees(p_117016_));
        if (isBaby) {
            float f = 0.75F;
            poseStack.scale(0.75F, 0.75F, 0.75F);
            poseStack.translate(0.0D, 0.5D, 0.209375F);
        } else {
            poseStack.scale(0.9F, 0.9F, 0.9F);
        }
        GooseModel<Goose> model = this.getParentModel();
        //poseStack.translate(this.getParentModel().neck.x / 16.0F, this.getParentModel().neck.y / 16.0F, this.getParentModel().neck.z / 16.0F);
        poseStack.translate(model.neck.x / 16.0F, model.neck.y / 16.0F, model.neck.z / 16.0F);
        poseStack.translate(model.head.x / 16.0F, model.head.y / 16.0F, model.head.z / 16.0F);
        poseStack.mulPose(Vector3f.XP.rotation(this.getParentModel().head.xRot));
        poseStack.mulPose(Vector3f.YP.rotation(this.getParentModel().head.yRot));
        poseStack.translate(model.mouth.x / 16.0F, model.mouth.y / 16.0F, model.mouth.z / 16.0F);
        /*poseStack.mulPose(Vector3f.ZP.rotationDegrees(p_117014_));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(p_117015_));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(p_117016_));*/

        if (goose.isBaby()) {
            poseStack.translate(0F, 1F, -0.2D);
        } else {
            poseStack.translate(0F, 0.1F, -0.2D);
        }
        //poseStack.mulPose(Vector3f.XP.rotation(this.getParentModel().neck.xRot));

        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));

        ItemStack itemstack = goose.getItemBySlot(EquipmentSlot.MAINHAND);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(goose, itemstack, ItemTransforms.TransformType.GROUND, false, poseStack, buffer, p_117009_);
        poseStack.popPose();
    }
}