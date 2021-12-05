package com.notaflyingoose.goosemod.blocks;

import com.notaflyingoose.goosemod.entities.NetherReactorCoreBlockEntity;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntityTypes {
    public static final BlockEntityType<NetherReactorCoreBlockEntity> NETHER_REACTOR_CORE = (BlockEntityType<NetherReactorCoreBlockEntity>)
            BlockEntityType.Builder.of(NetherReactorCoreBlockEntity::new, ModBlocks.NETHER_REACTOR_CORE)
                    .build(Util.fetchChoiceType(References.BLOCK_ENTITY, "nether_reactor_core"))
                    .setRegistryName("nether_reactor_core");
}
