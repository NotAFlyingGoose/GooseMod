package com.notaflyingoose.goosemod.blocks;

import com.notaflyingoose.goosemod.entities.PrimedC4;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class C4Block extends Block {
    public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

    public C4Block() {
        super(BlockBehaviour.Properties.of(Material.EXPLOSIVE)
                .instabreak()
                .sound(SoundType.GRASS));
        this.registerDefaultState(this.defaultBlockState().setValue(UNSTABLE, Boolean.valueOf(true)));
    }

    @Override
    public void catchFire(BlockState state, Level world, BlockPos pos, @Nullable net.minecraft.core.Direction face, @Nullable LivingEntity igniter) {
        explode(world, pos, igniter);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState neighbor, boolean p_57470_) {
        if (!neighbor.is(state.getBlock())) {
            if (level.hasNeighborSignal(pos)) {
                catchFire(state, level, pos, null, null);
                level.removeBlock(pos, false);
            }

        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean p_57462_) {
        if (level.hasNeighborSignal(pos)) {
            catchFire(state, level, pos, null, null);
            level.removeBlock(pos, false);
        }

    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && !player.isCreative() && state.getValue(UNSTABLE)) {
            catchFire(state, level, pos, null, null);
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide) {
            PrimedC4 PrimedC4 = new PrimedC4(level, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, explosion.getSourceMob());
            int i = PrimedC4.getFuse();
            PrimedC4.setFuse((short)(level.random.nextInt(i / 4) + i / 8));
            level.addFreshEntity(PrimedC4);
        }
    }

    @Deprecated //Forge: Prefer using IForgeBlock#catchFire
    public static void explode(Level level, BlockPos pos) {
        explode(level, pos, (LivingEntity)null);
    }

    @Deprecated //Forge: Prefer using IForgeBlock#catchFire
    private static void explode(Level level, BlockPos pos, @Nullable LivingEntity igniter) {
        if (!level.isClientSide) {
            PrimedC4 PrimedC4 = new PrimedC4(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, igniter);
            level.addFreshEntity(PrimedC4);
            level.playSound(null, PrimedC4.getX(), PrimedC4.getY(), PrimedC4.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!itemstack.is(Items.FLINT_AND_STEEL) && !itemstack.is(Items.FIRE_CHARGE)) {
            return super.use(state, level, pos, player, hand, hit);
        } else {
            catchFire(state, level, pos, hit.getDirection(), player);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            Item item = itemstack.getItem();
            if (!player.isCreative()) {
                if (itemstack.is(Items.FLINT_AND_STEEL)) {
                    itemstack.hurtAndBreak(1, player, (e) -> {
                        e.broadcastBreakEvent(hand);
                    });
                } else {
                    itemstack.shrink(1);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(item));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide) {
            BlockPos blockpos = hit.getBlockPos();
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire() && projectile.mayInteract(level, blockpos)) {
                catchFire(state, level, blockpos, null, entity instanceof LivingEntity ? (LivingEntity)entity : null);
                level.removeBlock(blockpos, false);
            }
        }
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateDefinitions) {
        stateDefinitions.add(UNSTABLE);
    }

}
