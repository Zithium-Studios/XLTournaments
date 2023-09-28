/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.action.actions;

import net.zithium.library.utils.Color;
import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.action.Action;
import net.zithium.tournaments.utility.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastMessageAction implements Action {

    @Override
    public String getIdentifier() {
        return "BROADCAST";
    }

    @Override
    public void execute(XLTournamentsPlugin plugin, Player player, String data) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.sendMessage(Color.stringColor(data)));
    }
}
