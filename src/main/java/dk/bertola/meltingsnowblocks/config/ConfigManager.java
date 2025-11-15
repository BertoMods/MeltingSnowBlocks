package dk.bertola.meltingsnowblocks.config;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static Config CONFIG;
    private static Path CONFIG_PATH;

    public static Config getConfig(){
        return CONFIG;
    }

    public static void createOrLoad(Path configPath) throws IOException {
        CONFIG = new Config();
        CONFIG_PATH = configPath;
        if (configPath.toFile().isFile()) {
            // Load Config
            String jsonString = Files.readString(configPath);
            CONFIG = new Gson().fromJson(jsonString, Config.class);
        } else {
            // create new with defaults
            try {
                Files.createFile(configPath);
                String json = new Gson().toJson(CONFIG);
                Files.writeString(configPath, json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void save() throws Exception {
        if (CONFIG_PATH == null) {
            throw new Exception("CONFIG_PATH is NULL");
        }
        String jsonString = new Gson().toJson(CONFIG);
        Files.writeString(CONFIG_PATH, jsonString);
    }
}
