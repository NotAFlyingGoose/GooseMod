package com.notaflyingoose.goosemod.fluids;

import com.notaflyingoose.goosemod.GooseMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class ModFluids {
    public static final NitroglycerinFluid.Flowing FLOWING_NITROGLYCERIN = (NitroglycerinFluid.Flowing) new NitroglycerinFluid.Flowing().setRegistryName("flowing_nitroglycerin");
    public static final NitroglycerinFluid.Source NITROGLYCERIN = (NitroglycerinFluid.Source) new NitroglycerinFluid.Source().setRegistryName("nitroglycerin");
    public static class Tags {
        public static Tag<Fluid> NITROGLYCERIN = FluidTags.createOptional(new ResourceLocation(GooseMod.MODID, "nitroglycerin"));
    }
}
