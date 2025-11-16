package dk.bertola.improvedsnowmelting.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dk.bertola.improvedsnowmelting.ImprovedSnowMelting;
import dk.bertola.improvedsnowmelting.SnowMeltManager;
import dk.bertola.improvedsnowmelting.config.ConfigManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowBlock.class)
public abstract class SnowBlockMixin {
    @Inject(method = "randomTick", at = @At("HEAD"))
    private void heatBasedSnowMelting(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.isClient) return;
        if (ConfigManager.getConfig().simpleMelting) {
            if(ConfigManager.getConfig().simpleMeltingLightLevel <= world.getLightLevel(LightType.BLOCK, pos)){
                SnowMeltManager.meltSnowBlock(world, pos);
            } else if (state.get(SnowBlock.LAYERS) == 8){
                SnowMeltManager.simpleCheckAndMeltSnow(world,pos);
            }
        } else {
            if (SnowMeltManager.checkAndMeltSnow(world, pos)) {
                ImprovedSnowMelting.LOGGER.debug("Snow Block melt progress at {}", pos);
            }
        }
    }

    @Definition(id = "getLightLevel", method = "Lnet/minecraft/server/world/ServerWorld;getLightLevel(Lnet/minecraft/world/LightType;Lnet/minecraft/util/math/BlockPos;)I")
    @Definition(id = "BLOCK", field = "Lnet/minecraft/world/LightType;BLOCK:Lnet/minecraft/world/LightType;")
    @Expression("?.getLightLevel(BLOCK, ?) > 11")
    @ModifyExpressionValue(method = "randomTick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean disableDefaultMeltBehavior(boolean original) {
        return false;
    }
}
