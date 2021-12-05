package com.notaflyingoose.goosemod.entities.boss.herobrine;

import com.notaflyingoose.goosemod.entities.boss.AbstractPhaseInstance;
import net.minecraft.world.entity.projectile.LargeFireball;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HerobrineMeteorPhase extends AbstractPhaseInstance<HerobrineBoss> {
    protected static final Logger LOGGER = LogManager.getLogger();

    public HerobrineMeteorPhase(HerobrineBoss boss) {
        super(boss);
    }

    @Override
    public void doClientTick() {
        //boss.getMoveControl().setWantedPosition(0, 0, 0, 0.5);
    }

    @Override
    public void doServerTick() {
        if (boss.getPhaseTicks() % 10 != 0)
            return;
        double rX = boss.getX() + (level.random.nextDouble() - 0.5D) * 64.0D;
        double rY = boss.getY() + (level.random.nextInt(32) - 16);
        double rZ = boss.getZ() + (level.random.nextDouble() - 0.5D) * 64.0D;

        boss.startAttackAnimation();
        boss.getNavigation().setCanFloat(true);

        LargeFireball largefireball = new LargeFireball(level, this.boss, 0, -1, 0, 1);
        largefireball.setPos(rX, this.boss.getY() + rY, rZ);
        level.addFreshEntity(largefireball);
    }
}
