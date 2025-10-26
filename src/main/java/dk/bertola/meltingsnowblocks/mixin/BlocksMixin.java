package dk.bertola.meltingsnowblocks.mixin;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import static dk.bertola.meltingsnowblocks.MeltingSnowBlocks.LOGGER;


@Mixin(Blocks.class)
public class BlocksMixin {
    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;",
                    ordinal = 195 //Instance of when the SNOW_BLOCK is using create()
            )
    )
    private static AbstractBlock.Settings modifySnowBlockSettings(AbstractBlock.Settings original) {

        return original
                .mapColor(MapColor.WHITE)
                .requiresTool()
                .strength(0.2f)
                .sounds(BlockSoundGroup.SNOW)
                .ticksRandomly();
    }
}
