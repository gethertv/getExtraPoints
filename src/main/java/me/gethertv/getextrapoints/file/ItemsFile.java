package me.gethertv.getextrapoints.file;

import me.gethertv.getextrapoints.GetExtraPoints;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ItemsFile {

    private static File file;

    private static FileConfiguration config;

    public static void setup() {
        file = new File(GetExtraPoints.getInstance().getDataFolder(), "items.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            GetExtraPoints.getInstance().saveResource("items.yml", false);
        }
        config = (FileConfiguration)new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Nie mozna zapisac pliku!");
        }
    }

    public static void loadFile() {
        setup();
        save();
    }
}
