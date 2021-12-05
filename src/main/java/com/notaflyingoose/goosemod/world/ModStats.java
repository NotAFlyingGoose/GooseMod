package com.notaflyingoose.goosemod.world;

import com.notaflyingoose.goosemod.GooseMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class ModStats {
    public static final ResourceLocation INTERACT_WITH_MIXER = registerCustomStat("interact_with_mixer");
    public static final ResourceLocation TALKED_TO_HEISENBERG = registerCustomStat("talked_to_heisenberg");

    private static ResourceLocation registerCustomStat(String name) {
        ResourceLocation resourcelocation = new ResourceLocation(GooseMod.MODID, name);
        Registry.register(Registry.CUSTOM_STAT, name, resourcelocation);
        Stats.CUSTOM.get(resourcelocation, StatFormatter.DEFAULT);
        return resourcelocation;
    }
}
