/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.hook;

import org.bukkit.plugin.java.JavaPlugin;

public interface PluginHook {

    boolean onEnable(JavaPlugin plugin);

    String getIdentifier();

}
