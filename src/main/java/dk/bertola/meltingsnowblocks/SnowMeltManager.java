package dk.bertola.meltingsnowblocks;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SnowMeltManager {

    @Deprecated
    private static int checkAndMeltSnowAroundPlayer(ServerPlayerEntity player) {
        World world = player.getWorld();
        BlockPos playerPos = player.getBlockPos();
        int radius = MeltingSnowBlocks.CONFIG.meltRadius;
        int snowMelted = 0;

        MeltingSnowBlocks.LOGGER.debug("Checking around player at {}", playerPos);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);

                    if (isChunkLoaded(world, checkPos)) {
                        BlockState state = world.getBlockState(checkPos);
                        if (isSnowBlock(state)) {
                            MeltingSnowBlocks.LOGGER.debug("Found snow block at {}", checkPos);
                            if (checkAndMeltSnow(world, checkPos)) {
                                snowMelted++;
                            }
                        }
                    }
                }
            }
        }

        return snowMelted;
    }

    public static boolean checkAndMeltSnow(World world, BlockPos snowPos) {
        if (hasHeatSourceNearby(world, snowPos)) {
            MeltingSnowBlocks.LOGGER.debug("Heat source found near snow at {}", snowPos);
            meltSnowBlock(world, snowPos);
            return true;
        }
        return false;
    }

    private static boolean hasHeatSourceNearby(World world, BlockPos pos) {
        int radius = MeltingSnowBlocks.CONFIG.meltRadius;
        int heatSourcesFound = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    BlockState state = world.getBlockState(checkPos);
                    if (isHeatSource(state)) {
                        heatSourcesFound++;
                        MeltingSnowBlocks.LOGGER.debug("Found heat source {} at {}",
                                Registries.BLOCK.getId(state.getBlock()), checkPos);
                        return true;
                    }
                }
            }
        }

        MeltingSnowBlocks.LOGGER.debug("Found {} heat sources near {}", heatSourcesFound, pos);
        return false; //heatSourcesFound > 0;
    }

    private static boolean isChunkLoaded(World world, BlockPos pos) {
        boolean loaded = world.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
        if (!loaded) {
            MeltingSnowBlocks.LOGGER.debug("Chunk not loaded for position {}", pos);
        }
        return loaded;
    }

    private static boolean isSnowBlock(BlockState state) {
        boolean isSnow = state.isOf(Blocks.SNOW_BLOCK) || state.isOf(Blocks.SNOW);
        if (isSnow) {
            MeltingSnowBlocks.LOGGER.trace("Block at is snow: {}", state.getBlock());
        }
        return isSnow;
    }

    private static boolean isHeatSource(BlockState state) {
        if (MeltingSnowBlocks.CONFIG == null) {
            MeltingSnowBlocks.LOGGER.error("CONFIG IS NULL!");
            return false;
        }
        if (MeltingSnowBlocks.CONFIG.heatSources == null) {
            MeltingSnowBlocks.LOGGER.error("heatSources IS NULL!");
            return false;
        }

        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();
        boolean isHeatSource = MeltingSnowBlocks.CONFIG.heatSources.contains(blockId);

        if (isHeatSource) {
            MeltingSnowBlocks.LOGGER.info("Block {} is a heat source", blockId);
        }

        return isHeatSource;
    }

    private static void meltSnowBlock(World world, BlockPos pos) {
        try {
            BlockState state = world.getBlockState(pos);
            MeltingSnowBlocks.LOGGER.info("Melting snow block at {}", pos);
            BlockState above = world.getBlockState(pos.up());
            if (!above.isOf(Blocks.SNOW) && !above.isOf(Blocks.SNOW_BLOCK)) {
                if (state.isOf(Blocks.SNOW_BLOCK)) {
                    world.setBlockState(pos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 8));
                    world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.05f, 1.0f);
                    MeltingSnowBlocks.LOGGER.info("Melted snow block at {}", pos);
                } else if (state.isOf(Blocks.SNOW)) {
                    int layers = state.get(SnowBlock.LAYERS);
                    if (layers > 1) {
                        world.setBlockState(pos, state.with(SnowBlock.LAYERS, layers - 1));
                        MeltingSnowBlocks.LOGGER.info("Reduced snow layers to {} at {}", layers - 1, pos);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        MeltingSnowBlocks.LOGGER.info("Melted snow layer at {}", pos);
                    }
                    world.playSound(null, pos, SoundEvents.BLOCK_SNOW_BREAK, SoundCategory.BLOCKS, 0.5f, 1.0f);
                }
            }
        } catch (Exception e) {
            MeltingSnowBlocks.LOGGER.error("Error melting snow block at {}: {}", pos, e.getMessage());
        }
    }
}