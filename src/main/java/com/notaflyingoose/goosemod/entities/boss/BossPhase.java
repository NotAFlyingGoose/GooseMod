package com.notaflyingoose.goosemod.entities.boss;

import java.lang.reflect.Constructor;

public class BossPhase<T extends AbstractBoss, I extends PhaseInstance> {
    private final Class<T> bossClass;
    private final Class<I> instanceClass;
    private final int id;
    private final String name;
    private final int ticks;
    private PhaseInstance phaseInstance;

    BossPhase(int id, Class<T> bossClass, Class<I> instanceClass, int ticks, String name) {
        this.id = id;
        this.bossClass = bossClass;
        this.instanceClass = instanceClass;
        this.ticks = ticks;
        this.name = name;
    }

    public PhaseInstance createInstance(AbstractBoss boss) {
        try {
            Constructor<? extends PhaseInstance> constructor = this.instanceClass.getConstructor(this.bossClass);
            return this.phaseInstance = constructor.newInstance((T) boss);
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    public int getId() {
        return this.id;
    }

    public String toString() {
        return this.name + " (#" + this.id + ")";
    }

    public PhaseInstance getInstance() {
        return this.phaseInstance;
    }

    public int getTicks() {
        return this.ticks;
    }
}
