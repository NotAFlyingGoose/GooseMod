package com.notaflyingoose.goosemod.entities.boss.herobrine;

import com.notaflyingoose.goosemod.entities.boss.AbstractPhaseInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HerobrineSummonMinionsPhase extends AbstractPhaseInstance<HerobrineBoss> {
    protected static final Logger LOGGER = LogManager.getLogger();

    public HerobrineSummonMinionsPhase(HerobrineBoss boss) {
        super(boss);
    }

    @Override
    public void doClientTick() {
    }

    @Override
    public void doServerTick() {
        /*Player target = boss.level.getNearestPlayer(boss, 64);
        if (target == null) {
            return;
        }
        boss.getLookControl().setLookAt(target);*/

        if (boss.getPhaseTicks() % 10 != 0)
            return;

        boss.getNavigation().setCanFloat(true);

        Monster minion = switch(level.random.nextInt(5)) {
            case 0 -> EntityType.ZOMBIFIED_PIGLIN.create(level);
            case 1 -> EntityType.SKELETON.create(level);
            default -> EntityType.ZOMBIE.create(level);
        };
        double summonX = boss.getX() + (level.random.nextDouble() - 0.5D) * 16;
        double summonY = boss.getY() + (level.random.nextInt(16) - 8);
        double summonZ = boss.getZ() + (level.random.nextDouble() - 0.5D) * 16;
        BlockPos.MutableBlockPos summonPos = new BlockPos.MutableBlockPos(summonX, summonY, summonZ);

        while(summonPos.getY() > this.level.getMinBuildHeight() && !this.level.getBlockState(summonPos).getMaterial().blocksMotion()) {
            summonPos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level.getBlockState(summonPos);
        if (blockstate.getMaterial().blocksMotion() && !blockstate.getFluidState().is(FluidTags.WATER)) {
            minion.setPosRaw(summonX, summonPos.move(Direction.UP).getY() + 1f, summonZ);
            minion.setItemSlot(EquipmentSlot.HEAD, new ItemStack(level.random.nextFloat() < 0.1F ? Items.DIAMOND_HELMET : Items.GOLDEN_HELMET));
            if (minion instanceof Skeleton)
                minion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            if (minion instanceof ZombifiedPiglin)
                minion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
            minion.setTarget(boss.getTarget());
            LOGGER.info("Summoning {} at {}, {}, {}", minion.getType().getRegistryName() ,minion.getX(), minion.getY(), minion.getZ());
            level.addFreshEntity(minion);
        }
    }
}
