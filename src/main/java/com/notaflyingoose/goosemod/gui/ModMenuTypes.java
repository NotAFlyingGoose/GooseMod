package com.notaflyingoose.goosemod.gui;

import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static final MenuType<MixerMenu> MIXER = (MenuType<MixerMenu>) new MenuType<>(MixerMenu::new).setRegistryName("mixer");
}
