/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.events;

import fun.lewisdev.tournaments.tournament.Tournament;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CompletedChallengeEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean cancelled = false;

    private Player player;
    private int position;
    private Tournament tournament;

    public CompletedChallengeEvent(Player player, int position, Tournament tournament) {
        this.player = player;
        this.position = position;
        this.tournament = tournament;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public int getPosition() {
        return position;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
