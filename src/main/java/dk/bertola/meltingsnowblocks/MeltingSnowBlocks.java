package dk.bertola.meltingsnowblocks;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeltingSnowBlocks implements ModInitializer {
    public static final String MOD_ID = "melting-snow-blocks";
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Config CONFIG = Config.createAndLoad();

	@Override
	public void onInitialize() {
        try {
            LOGGER.info("Melting Snow Blocks initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Snow Melt Mod", e);
            throw new RuntimeException(e);
        }
	}


}