/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.action.actions;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.action.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConsoleCommandAction implements Action {

    @Override
    public String getIdentifier() {
        return "CONSOLE";
    }

    @Override
    public void execute(XLTournamentsPlugin plugin, Player player, String data) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), data);
    }
}