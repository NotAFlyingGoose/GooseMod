package com.notaflyingoose.goosemod.world;

import com.notaflyingoose.goosemod.items.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModTabs {
    public static CreativeModeTab GOOSEMOD = new CreativeModeTab("goosemod") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.DRAGON_SKULL);
        }
    };
}
