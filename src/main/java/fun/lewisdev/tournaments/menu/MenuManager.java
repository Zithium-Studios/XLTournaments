/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.menu;

import fun.lewisdev.tournaments.XLTournamentsPlugin;
import fun.lewisdev.tournaments.menu.menus.TournamentGUI;

public class MenuManager {

    private final TournamentGUI tournamentGui;

    public MenuManager(XLTournamentsPlugin plugin) {
        tournamentGui = new TournamentGUI(plugin);
    }

    public TournamentGUI getTournamentGui() {
        return tournamentGui;
    }
}
