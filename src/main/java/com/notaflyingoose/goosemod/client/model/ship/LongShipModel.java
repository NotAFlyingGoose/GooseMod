package com.notaflyingoose.goosemod.client.model.ship;

import com.notaflyingoose.goosemod.entities.vehicle.AbstractShip;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LongShipModel extends AbstractShipModel {

    public LongShipModel(ModelPart root) {
        super(root, root.getChild("sail"));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-43.0F, -10.0F, 0.0F, 56.0F, 20.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(15.0F, 6.0F, 0.0F, 1.5708F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 78).addBox(-10.0F, -3.0F, -1.0F, 20.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(28.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
        partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(74, 59).addBox(-10.0F, -3.0F, -1.0F, 20.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-28.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
        partdefinition.addOrReplaceChild("right", CubeListBuilder.create().texOffs(34, 31).addBox(-27.0F, -3.0F, 0.0F, 54.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -9.0F, 0.0F, -3.1416F, 0.0F));
        partdefinition.addOrReplaceChild("left", CubeListBuilder.create().texOffs(34, 23).addBox(-27.0F, -3.0F, -1.0F, 54.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));
        partdefinition.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(74, 39).addBox(-1.0F, -1.0F, -5.5F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(16, 23).addBox(-0.01F, -4.0F, 8.5F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -4.0F, 9.0F, -0.5236F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(56, 73).addBox(-1.0F, -1.0F, -5.5F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(0, 23).addBox(-0.99F, -4.0F, 8.5F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -4.0F, -9.0F, -0.5236F, 3.1416F, 0.0F));
        partdefinition.addOrReplaceChild("sail", CubeListBuilder.create()
                .texOffs(66, 39).addBox(0.5F, -51.0F, -0.5F, 2.0F, 50.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 23).addBox(2.5F, -50.0F, -15.5F, 1.0F, 23.0F, 32.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-10.0F, 4.0F, 0.0F));
        partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(0, 0).addBox(-28.0F, -10.0F, 0.0F, 56.0F, 20.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 156, 128);
    }

    public void setupAnim(AbstractShip ship, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        animatePaddle(ship, 0, this.leftPaddle, limbSwing);
        animatePaddle(ship, 1, this.rightPaddle, limbSwing);
    }

}
