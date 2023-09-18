/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.action;

import net.zithium.tournaments.XLTournamentsPlugin;
import org.bukkit.entity.Player;

public interface Action {

    String getIdentifier();

    void execute(XLTournamentsPlugin plugin, Player player, String data);

}
