package com.notaflyingoose.goosemod.effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HighMobEffect extends MobEffect {

    protected HighMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF66FA);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getHealth() > 1.0F) {
            entity.hurt(DamageSource.MAGIC, 1.0F);
        }
        if (entity instanceof Player) {
            ((Player)entity).causeFoodExhaustion(0.005F * (float)(amplifier + 1));
        }
    }


}
