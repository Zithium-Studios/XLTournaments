/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.menu;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.menu.menus.TournamentGUI;

public class MenuManager {

    private final TournamentGUI tournamentGui;

    public MenuManager(XLTournamentsPlugin plugin) {
        tournamentGui = new TournamentGUI(plugin);
    }

    public TournamentGUI getTournamentGui() {
        return tournamentGui;
    }
}
