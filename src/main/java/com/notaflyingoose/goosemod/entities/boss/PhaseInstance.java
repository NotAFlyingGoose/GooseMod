package com.notaflyingoose.goosemod.entities.boss;

public interface PhaseInstance {

    void doClientTick();

    void doServerTick();

    void begin();

    void end();

}
