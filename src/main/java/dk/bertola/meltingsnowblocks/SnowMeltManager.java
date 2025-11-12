package dk.bertola.meltingsnowblocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SnowMeltManager {


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
        //int heatSourcesFound = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    BlockState state = world.getBlockState(checkPos);
                    if (isHeatSource(state)) {
                        //heatSourcesFound++;
                        return true;
                    }
                }
            }
        }

        return false; //heatSourcesFound > 0;
    }


    private static boolean isHeatSource(BlockState state) {
        if (MeltingSnowBlocks.CONFIG.heatSources == null) {
            MeltingSnowBlocks.LOGGER.error("heatSources IS NULL!");
            return false;
        }
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        return MeltingSnowBlocks.CONFIG.heatSources.contains(blockId);
    }

    public static void meltSnowBlock(World world, BlockPos pos) {
        try {
            BlockState state = world.getBlockState(pos);
            BlockState above = world.getBlockState(pos.up());

            if (!above.isOf(Blocks.SNOW) && !above.isOf(Blocks.SNOW_BLOCK)) {
                if (state.isOf(Blocks.SNOW_BLOCK)) {
                    world.setBlockState(pos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 8));
                    world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.05f, 1.0f);
                } else if (state.isOf(Blocks.SNOW)) {
                    int layers = state.get(SnowBlock.LAYERS);
                    if (layers > 1) {
                        world.setBlockState(pos, state.with(SnowBlock.LAYERS, layers - 1));
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                    world.playSound(null, pos, SoundEvents.BLOCK_SNOW_BREAK, SoundCategory.BLOCKS, 0.5f, 1.0f);
                }
            }
        } catch (Exception e) {
            MeltingSnowBlocks.LOGGER.error("Error melting snow block at {}: {}", pos, e.getMessage());
        }
    }
}