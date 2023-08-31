/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.storage;

import fun.lewisdev.tournaments.XLTournamentsPlugin;
import fun.lewisdev.tournaments.storage.impl.MySQLHandler;
import fun.lewisdev.tournaments.storage.impl.SQLiteHandler;

public class StorageManager {

    private final XLTournamentsPlugin plugin;
    private StorageHandler storageHandler;

    public StorageManager(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        String storageType = plugin.getConfig().getString("storage.type");

        switch(storageType.toUpperCase()) {
            case "SQLITE":
                storageHandler = new SQLiteHandler();
                break;
            case "MYSQL":
                storageHandler = new MySQLHandler();
                break;
            default:
                plugin.getServer().getPluginManager().disablePlugin(plugin);

        }

        if(!storageHandler.onEnable(plugin)) {
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public void onDisable() {
        storageHandler.onDisable();
    }

    public StorageHandler getStorageHandler() {
        return storageHandler;
    }
}
