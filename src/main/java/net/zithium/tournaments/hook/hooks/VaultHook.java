/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.hook.hooks;

import net.zithium.tournaments.hook.PluginHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultHook implements PluginHook {

    private Economy economy;

    @Override
    public boolean onEnable(JavaPlugin plugin) {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return true;
    }

    @Override
    public String getIdentifier() {
        return "Vault";
    }

    public double getBalance(Player player) {
        return economy.getBalance(player);
    }

    public void withdraw(Player player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

}
