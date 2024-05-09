/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.events;

import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.tournament.TournamentData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TournamentEndEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final boolean cancelled = false;

    private final Tournament tournament;
    private final TournamentData tournamentData;

    public TournamentEndEvent(Tournament tournament, TournamentData tournamentData) {
        this.tournament = tournament;
        this.tournamentData = tournamentData;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public TournamentData getTournamentData() {
        return tournamentData;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
