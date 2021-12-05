package com.notaflyingoose.goosemod.effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DragonSicknessMobEffect extends MobEffect {

    protected DragonSicknessMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF3333);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        /*if (entity instanceof Player) {
            ((Player)entity).causeFoodExhaustion(0.005F * (float)(amplifier + 1));
        }*/
    }


}
