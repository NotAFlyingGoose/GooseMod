package com.notaflyingoose.goosemod.entities.boss.herobrine;

import com.notaflyingoose.goosemod.entities.boss.AbstractPhaseInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HerobrineLightningPhase extends AbstractPhaseInstance<HerobrineBoss> {
    protected static final Logger LOGGER = LogManager.getLogger();

    public HerobrineLightningPhase(HerobrineBoss boss) {
        super(boss);
    }

    @Override
    public void begin() {
        if (boss.getHealth() / boss.getMaxHealth() > 0.5f)
            boss.setCurrentPhase();
    }

    @Override
    public void doClientTick() {
        //boss.getMoveControl().setWantedPosition(0, 0, 0, 0.5);
    }

    @Override
    public void doServerTick() {
        if (boss.hasExploded()) {
            summonLightningBolt(boss);
        } else {
            LOGGER.info("lightning explosion");
            boss.setExploded(true);
            boss.startAttackAnimation();

            double range = boss.getAttributeValue(Attributes.FOLLOW_RANGE);
            AABB aabb = AABB.unitCubeFromLowerCorner(boss.position()).inflate(range, 10.0D, range);
            level.getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.NO_SPECTATORS).stream().filter((e) -> !(e instanceof Player)).forEach((e) -> {
                for (int i = 0; i < 3; i++) {
                    summonLightningBolt(e);
                }
            });
            LivingEntity target = boss.getTarget();
            if (target != null) {
                summonLightningBolt(target);
            }
        }
    }

    private void summonLightningBolt(Entity entity) {
        LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(this.level);
        lightningbolt.moveTo(Vec3.atBottomCenterOf(entity.getOnPos()));
        level.addFreshEntity(lightningbolt);
    }
}
