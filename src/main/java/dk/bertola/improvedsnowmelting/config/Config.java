package dk.bertola.improvedsnowmelting.config;


import java.util.Arrays;
import java.util.List;

public class Config {
    public List<String> heatSources = Arrays.asList(
            "minecraft:torch",
            "minecraft:wall_torch",
            "minecraft:lava",
            "minecraft:campfire",
            "minecraft:lantern"
    );
    public int meltRadius = 3;
    /// Simple melting is the basic setting of just melting snow blocks and layered snow by light level.
    /// Default: True
    public boolean simpleMelting = true;
    public int simpleMeltingLightLevel = 11;

}
