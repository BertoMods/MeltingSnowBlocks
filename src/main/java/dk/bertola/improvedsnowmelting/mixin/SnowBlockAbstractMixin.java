package dk.bertola.improvedsnowmelting.mixin;

import dk.bertola.improvedsnowmelting.ImprovedSnowMelting;
import dk.bertola.improvedsnowmelting.SnowMeltManager;
import dk.bertola.improvedsnowmelting.config.ConfigManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class SnowBlockAbstractMixin {

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void heatBasedSnowMelting(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.isClient) return;
        if (!state.isOf(Blocks.SNOW_BLOCK)) return;
        if (ConfigManager.getConfig().simpleMelting) {
            SnowMeltManager.simpleCheckAndMeltSnow(world, pos);
        } else {
            if (SnowMeltManager.checkAndMeltSnow(world, pos)) {
                ImprovedSnowMelting.LOGGER.debug("Snow Block melt progress at {}", pos.toString());
            }
        }
    }
}
