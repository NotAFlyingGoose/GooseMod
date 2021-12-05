package com.notaflyingoose.goosemod.blocks;

import com.notaflyingoose.goosemod.items.ModItems;
import com.notaflyingoose.goosemod.world.ModWorldGeneration;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.Vec3;

public class DebugBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public DebugBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE).strength(-1.0F, 3600000.0F).noDrops().isValidSpawn((p_61031_, p_61032_, p_61033_, p_61034_) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49751_) {
        p_49751_.add(POWERED);
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block p_49732_, BlockPos p_49733_, boolean p_49734_) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            if (flag) {
                if (level.isClientSide)
                    debugClient((ClientLevel) level, pos);
                else
                    debugServer((ServerLevel) level, pos);
            }

            level.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(flag)), 3);
        }
    }

    public void debugClient(ClientLevel level, BlockPos pos) {
    }

    public void debugServer(ServerLevel level, BlockPos pos) {
        Player nearestPlayer = level.getNearestPlayer(TargetingConditions.forNonCombat().selector(LivingEntity::isAlive), pos.getX(), pos.getY(), pos.getZ());
        if (nearestPlayer == null)
            return;
        nearestPlayer.addItem(getDragonLairMap(level, nearestPlayer.position()));
    }

    public static ItemStack getDragonLairMap(ServerLevel level, Vec3 nearestFrom) {
        BlockPos blockpos = level.findNearestMapFeature(ModWorldGeneration.Structures.LAIR_FEATURE, new BlockPos(nearestFrom), 50, true);
        ItemStack map = MapItem.create(level, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
        MapItem.renderBiomePreviewMap(level, map);

        ListTag listtag;
        if (map.hasTag() && map.getTag().contains("Decorations", 9)) {
            listtag = map.getTag().getList("Decorations", 10);
        } else {
            listtag = new ListTag();
            map.addTagElement("Decorations", listtag);
        }

        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putByte("type", MapDecoration.Type.TARGET_POINT.getIcon());
        compoundtag.putString("id", "+");
        compoundtag.putDouble("x", blockpos.getX());
        compoundtag.putDouble("z", blockpos.getZ());
        compoundtag.putDouble("rot", 180.0D);
        listtag.add(compoundtag);
        CompoundTag compoundtag1 = map.getOrCreateTagElement("display");
        compoundtag1.putInt("MapColor", 0x993636);
        map.setHoverName(new TranslatableComponent("filled_map.goosemod.dragon_lair"));
        ModItems.addLore(map, new TranslatableComponent("filled_map.goosemod.dragon_lair.desc_" + (level.random.nextInt(5) + 1)).getString());
        return map;
    }
    
}
