package com.notaflyingoose.goosemod.items;

import com.notaflyingoose.goosemod.ObfuscationReflectionUtils;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.util.Lazy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class ModSpawnEggItem extends SpawnEggItem {

    private static final Logger LOGGER = LogManager.getLogger();
    protected static final List<ModSpawnEggItem> UNADDED_EGGS = new ArrayList<>();
    private final Lazy<? extends EntityType<?>> entityTypeSupplier;

    public ModSpawnEggItem(final Supplier<? extends EntityType<?>> entityTypeSupplier, int primaryColor, int secondaryColor, Properties builder) {
        super(null, primaryColor, secondaryColor, builder);
        this.entityTypeSupplier = Lazy.of(entityTypeSupplier::get);
        UNADDED_EGGS.add(this);
    }

    public static void initSpawnEggs() {
        final Map<EntityType<? extends Mob>, SpawnEggItem> EGGS = ObfuscationReflectionUtils.getStaticFieldOfType(SpawnEggItem.class, Map.class);
        DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
                type.spawn(source.getLevel(), stack, null, source.getPos(), MobSpawnType.DISPENSER, direction != Direction.UP, false);
                stack.shrink(1);
                return stack;
            }
        };

        for (final SpawnEggItem spawnEgg : UNADDED_EGGS) {
            EGGS.put((EntityType<? extends Mob>) spawnEgg.getType(null), spawnEgg);
            DispenserBlock.registerBehavior(spawnEgg, dispenseBehavior);
        }
        UNADDED_EGGS.clear();
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag nbt) {
        return this.entityTypeSupplier.get();
    }
}
