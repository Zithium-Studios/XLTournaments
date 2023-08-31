/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    private final JavaPlugin plugin;
    private final String name;
    private final File file;
    private FileConfiguration configuration;

    public ConfigHandler(JavaPlugin plugin, File path, String name) {
        this.plugin = plugin;
        this.name = name.endsWith(".yml") ? name : name + ".yml";
        this.file = new File(path, this.name);
        this.configuration = new YamlConfiguration();
    }

    public ConfigHandler(JavaPlugin plugin, String name) {
        this(plugin, plugin.getDataFolder(), name);
    }

    public void saveDefaultConfig() {

        if (!file.exists()) {
            final int length = file.toPath().getNameCount();
            plugin.saveResource(file.getParentFile().getName().equals(plugin.getName()) ? name : file.toPath().subpath(length - 2, length).toFile().getPath(), false);
        }

        try {
            configuration.load(file);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("============= CONFIGURATION ERROR =============");
            plugin.getLogger().severe("There was an error loading " + name);
            plugin.getLogger().severe("Please check for any obvious configuration mistakes");
            plugin.getLogger().severe("such as using tabs for spaces or forgetting to end quotes");
            plugin.getLogger().severe("before reporting to the developer. The plugin will now disable..");
            plugin.getLogger().severe("============= CONFIGURATION ERROR =============");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

    }

    public void save() {
        if (configuration == null || file == null) return;
        try {
            getConfig().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return configuration;
    }

    public File getFile() {
        return file;
    }
}
