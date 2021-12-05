package com.notaflyingoose.goosemod.effects;

import net.minecraft.world.effect.MobEffect;

public class ModMobEffects {
    public static final MobEffect HIGH = new HighMobEffect().setRegistryName("high");
    public static final MobEffect DRAGON_SICKNESS = new DragonSicknessMobEffect().setRegistryName("dragon_sickness");
}
