/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.hook;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.hook.hooks.VaultHook;

import java.util.HashMap;
import java.util.Map;

public class HookManager {

    private final XLTournamentsPlugin plugin;
    private final Map<String, PluginHook> pluginHooks;

    public HookManager(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
        pluginHooks = new HashMap<>();
    }

    public void onEnable() {
        registerHook(new VaultHook());
    }

    public boolean isHookEnabled(String identifier) {
        return pluginHooks.containsKey(identifier);
    }

    public PluginHook getPluginHook(String identifier) {
        return pluginHooks.get(identifier);
    }

    public void registerHook(PluginHook pluginHook) {
        if(plugin.getServer().getPluginManager().isPluginEnabled(pluginHook.getIdentifier()) && pluginHook.onEnable(plugin)) {
            pluginHooks.put(pluginHook.getIdentifier(), pluginHook);
            plugin.getLogger().info("Hooked into " + pluginHook.getIdentifier());
        }

    }

}
