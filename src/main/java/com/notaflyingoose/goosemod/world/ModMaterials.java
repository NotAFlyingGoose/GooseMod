package com.notaflyingoose.goosemod.world;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

public class ModMaterials {
    public static final Material MILK = new Material(MaterialColor.TERRACOTTA_WHITE, true, false, false, false, false, true, PushReaction.DESTROY);
    public static final Material NITROGLYCERIN = new Material(MaterialColor.COLOR_LIGHT_GREEN, true, false, false, false, false, true, PushReaction.DESTROY);
}
