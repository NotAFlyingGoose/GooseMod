package com.notaflyingoose.goosemod.world.structures;

import com.notaflyingoose.goosemod.GooseMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.Random;

public class DragonLairFeature extends StructureFeature<NoneFeatureConfiguration> {
    public DragonLairFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return DragonLairFeature.FeatureStart::new;
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }

    @Override
    public String getFeatureName() {
        return super.getFeatureName();
    }

    public static class FeatureStart extends StructureStart<NoneFeatureConfiguration> {
        public FeatureStart(StructureFeature<NoneFeatureConfiguration> p_159550_, ChunkPos p_159551_, int p_159552_, long p_159553_) {
            super(p_159550_, p_159551_, p_159552_, p_159553_);
        }

        public void generatePieces(RegistryAccess registry, ChunkGenerator generator, StructureManager manager, ChunkPos chunkPos, Biome biome, NoneFeatureConfiguration codec, LevelHeightAccessor height) {
            BlockPos blockpos = new BlockPos(chunkPos.getMinBlockX(), 50, chunkPos.getMinBlockZ());
            Rotation rotation = Rotation.getRandom(this.random);
            DragonLairPiece lairPiece = new DragonLairPiece(manager, DragonLairPiece.STRUCTURE_LOCATION, blockpos, rotation);
            this.addPiece(lairPiece);
        }
    }

    static class DragonLairPiece extends TemplateStructurePiece {
        public static final StructurePieceType LAIR = StructurePieceType.setPieceId(DragonLairPiece::new, "GMrdl");
        static final ResourceLocation STRUCTURE_LOCATION = new ResourceLocation(GooseMod.MODID, "dragon_lair");

        public DragonLairPiece(StructureManager manager, ResourceLocation nbt, BlockPos pos, Rotation rotation) {
            super(LAIR, 0, manager, nbt, nbt.toString(), makeSettings(rotation, nbt), pos);
        }

        public DragonLairPiece(ServerLevel p_162441_, CompoundTag p_162442_) {
            super(LAIR, p_162442_, p_162441_, (p_162451_) -> makeSettings(Rotation.valueOf(p_162442_.getString("Rot")), p_162451_));
        }

        private static StructurePlaceSettings makeSettings(Rotation p_162447_, ResourceLocation p_162448_) {
            return (new StructurePlaceSettings()).setRotation(p_162447_).setMirror(Mirror.NONE).setRotationPivot(new BlockPos(0, 0, 0)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        }

        protected void addAdditionalSaveData(ServerLevel p_162444_, CompoundTag p_162445_) {
            super.addAdditionalSaveData(p_162444_, p_162445_);
            p_162445_.putString("Rot", this.placeSettings.getRotation().name());
        }

        protected void handleDataMarker(String p_71260_, BlockPos p_71261_, ServerLevelAccessor p_71262_, Random p_71263_, BoundingBox p_71264_) {
            if ("chest".equals(p_71260_)) {
                p_71262_.setBlock(p_71261_, Blocks.AIR.defaultBlockState(), 3);
                BlockEntity blockentity = p_71262_.getBlockEntity(p_71261_.below());
                if (blockentity instanceof ChestBlockEntity) {
                    ((ChestBlockEntity) blockentity).setLootTable(BuiltInLootTables.BASTION_TREASURE, p_71263_.nextLong());
                }

            }
        }

        public boolean postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator generator, Random random, BoundingBox structureSize, ChunkPos chunkPos, BlockPos blockPos) {
            ResourceLocation resourcelocation = new ResourceLocation(this.templateName);
            StructurePlaceSettings structureplacesettings = makeSettings(this.placeSettings.getRotation(), resourcelocation);
            BlockPos blockpos1 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(structureplacesettings, new BlockPos(3, 0, 0)));
            int i = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockpos1.getX(), blockpos1.getZ());
            BlockPos blockpos2 = this.templatePosition;
            this.templatePosition = this.templatePosition.offset(0, i - 90 - 1, 0);
            boolean flag = super.postProcess(level, manager, generator, random, structureSize, chunkPos, blockPos);
            if (resourcelocation.equals(DragonLairPiece.STRUCTURE_LOCATION)) {
                BlockPos blockpos3 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(structureplacesettings, new BlockPos(3, 0, 5)));
                BlockState blockstate = level.getBlockState(blockpos3.below());
                if (!blockstate.isAir()) {
                    level.setBlock(blockpos3, Blocks.AIR.defaultBlockState(), 3);
                } else if (blockstate.is(Blocks.GOLD_BLOCK) && random.nextInt(10) == 0) {
                    level.setBlock(blockpos3, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                }
            }

            this.templatePosition = blockpos2;
            return flag;
        }
    }
}
