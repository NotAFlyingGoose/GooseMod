package com.notaflyingoose.goosemod.client.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.notaflyingoose.goosemod.client.model.ship.AbstractShipModel;
import com.notaflyingoose.goosemod.client.model.ship.LongShipModel;
import com.notaflyingoose.goosemod.client.model.ship.SingleRiderShipModel;
import com.notaflyingoose.goosemod.entities.vehicle.AbstractShip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat.Type;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.stream.Stream;

public class ShipRenderer extends EntityRenderer<AbstractShip> {
    private final Map<Type, Pair<ResourceLocation, AbstractShipModel>> boatResources;

    public ShipRenderer(EntityRendererProvider.Context context, AbstractShip.Variant variant) {
        super(context);
        this.shadowRadius = variant.isSmall() ? 0.8f : 1.8f;
        this.boatResources = Stream.of(Type.values()).collect(ImmutableMap.toImmutableMap((type) -> type,
                (type) -> Pair.of(new ResourceLocation(variant.getResources().getNamespace(), "textures/entity/" + variant.getResources().getPath() + "/" + type.getName() + ".png"),
                        variant.isSmall() ? new SingleRiderShipModel(context.bakeLayer(ModModelLayers.createShipModelName(variant, type))) :
                                new LongShipModel(context.bakeLayer(ModModelLayers.createShipModelName(variant, type))))));
    }

    public void render(AbstractShip boat, float p_113930_, float p_113931_, PoseStack poseStack, MultiBufferSource buffer, int p_113934_) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.375D, 0.0D);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_113930_));
        float f = (float)boat.getHurtTime() - p_113931_;
        float f1 = boat.getDamage() - p_113931_;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float)boat.getHurtDir()));
        }

        float f2 = boat.getBubbleAngle(p_113931_);
        if (!Mth.equal(f2, 0.0F)) {
            poseStack.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), boat.getBubbleAngle(p_113931_), true));
        }

        Pair<ResourceLocation, AbstractShipModel> pair = getModelWithLocation(boat);
        ResourceLocation resourcelocation = pair.getFirst();
        AbstractShipModel boatmodel = pair.getSecond();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        boatmodel.setupAnim(boat, p_113931_, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = buffer.getBuffer(boatmodel.renderType(resourcelocation));
        boatmodel.renderToBuffer(poseStack, vertexconsumer, p_113934_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (!boat.isUnderWater()) {
            VertexConsumer vertexconsumer1 = buffer.getBuffer(RenderType.waterMask());
            boatmodel.waterPatch().render(poseStack, vertexconsumer1, p_113934_, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
        super.render(boat, p_113930_, p_113931_, poseStack, buffer, p_113934_);

        BlockState blockstate = boat.getDisplayBlockState();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            poseStack.pushPose();
            float f4 = 0.75F;
            //poseStack.scale(0.99F, 0.99F, 0.99F);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-p_113930_));
            if (f > 0.0F) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-(Mth.sin(f) * f * f1 / 10.0F * (float)boat.getHurtDir())));
            }
            if (!Mth.equal(f2, 0.0F)) {
                poseStack.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), -boat.getBubbleAngle(p_113931_), true));
            }
            //poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
            poseStack.translate(-0.5D, 3 / 16f, boat.getDisplayBlockOffset() / 16f);
            this.renderBoatContents(boat, p_113931_, blockstate, poseStack, buffer, p_113934_);
            poseStack.popPose();
        }

        /*poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.model.setupAnim(boat, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer1 = buffer.getBuffer(this.model.renderType(this.getTextureLocation(boat)));
        this.model.renderToBuffer(buffer, vertexconsumer, buffer, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();*/
    }

    @Deprecated // forge: override getModelWithLocation to change the texture / model
    public ResourceLocation getTextureLocation(AbstractShip boat) {
        return getModelWithLocation(boat).getFirst();
    }

    public Pair<ResourceLocation, AbstractShipModel> getModelWithLocation(AbstractShip boat) { return this.boatResources.get(boat.getBoatType()); }

    protected void renderBoatContents(AbstractShip boat, float p_115425_, BlockState state, PoseStack poseStack, MultiBufferSource buffer, int p_115429_) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, buffer, p_115429_, OverlayTexture.NO_OVERLAY);
    }
}
