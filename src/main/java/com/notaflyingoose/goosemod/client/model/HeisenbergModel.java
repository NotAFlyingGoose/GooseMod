package com.notaflyingoose.goosemod.client.model;

import com.notaflyingoose.goosemod.entities.Heisenberg;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public class HeisenbergModel<T extends Heisenberg> extends HumanoidModel<T> {

    public HeisenbergModel(ModelPart model) {
        super(model);
    }

}
