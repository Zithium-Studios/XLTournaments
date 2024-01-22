/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2023 Zithium Studios. All rights reserved.
 */

package net.zithium.tournaments.action.actions;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.action.Action;
import org.bukkit.entity.Player;

public class CommandAction implements Action {

    @Override
    public String getIdentifier() {
        return "COMMAND";
    }

    @Override
    public void execute(XLTournamentsPlugin plugin, Player player, String data) {
        player.chat(data.contains("/") ? data : "/" + data);
    }
}