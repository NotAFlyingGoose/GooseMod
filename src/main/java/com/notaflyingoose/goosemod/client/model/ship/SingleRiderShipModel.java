package com.notaflyingoose.goosemod.client.model.ship;

import com.notaflyingoose.goosemod.entities.vehicle.AbstractShip;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SingleRiderShipModel extends AbstractShipModel {

    public SingleRiderShipModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 19).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(-15.0F, 4.0F, 4.0F, 0.0F, ((float)Math.PI * 1.5F), 0.0F));
        partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 27).addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(15.0F, 4.0F, 0.0F, 0.0F, ((float)Math.PI / 2F), 0.0F));
        partdefinition.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 35).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 4.0F, -9.0F, 0.0F, (float)Math.PI, 0.0F));
        partdefinition.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 43).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), PartPose.offset(0.0F, 4.0F, 9.0F));
        partdefinition.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(62, 0).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F), PartPose.offsetAndRotation(8.0F, -5.0F, 9.0F, 0.0F, 0.0F, 0.19634955F));
        partdefinition.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(62, 20).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F), PartPose.offsetAndRotation(8.0F, -5.0F, -9.0F, 0.0F, (float)Math.PI, 0.19634955F));
        partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public void setupAnim(AbstractShip ship, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        animatePaddle(ship, 0, this.leftPaddle, limbSwing);
        animatePaddle(ship, 1, this.rightPaddle, limbSwing);
    }

}
