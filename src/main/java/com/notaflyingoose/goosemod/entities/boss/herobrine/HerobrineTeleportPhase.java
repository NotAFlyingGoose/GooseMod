package com.notaflyingoose.goosemod.entities.boss.herobrine;

import com.notaflyingoose.goosemod.entities.boss.AbstractPhaseInstance;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HerobrineTeleportPhase extends AbstractPhaseInstance<HerobrineBoss> {
    protected static final Logger LOGGER = LogManager.getLogger();

    public HerobrineTeleportPhase(HerobrineBoss boss) {
        super(boss);
    }

    @Override
    public void doClientTick() {
        //boss.getMoveControl().setWantedPosition(0, 0, 0, 0.5);
    }

    @Override
    public void doServerTick() {
        if (boss.getPhaseTicks() % 20 != 0)
            return;

        LivingEntity target = boss.getTarget();
        if (target != null) {
            boss.teleportTowards(target);
            boss.getLookControl().setLookAt(target);
            if (boss.distanceTo(target) < 3)
                boss.doHurtTarget(target);
        }
    }
}
