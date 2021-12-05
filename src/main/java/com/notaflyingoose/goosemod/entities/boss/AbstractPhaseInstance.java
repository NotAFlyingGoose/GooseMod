package com.notaflyingoose.goosemod.entities.boss;

import net.minecraft.world.level.Level;

public abstract class AbstractPhaseInstance<T extends AbstractBoss> implements PhaseInstance {
    protected final Level level;
    protected final T boss;

    public AbstractPhaseInstance(T boss) {
        this.boss = boss;
        this.level = boss.level;
    }

    @Override
    public void doClientTick() {

    }

    @Override
    public void doServerTick() {

    }

    @Override
    public void begin() {

    }

    @Override
    public void end() {

    }
}
