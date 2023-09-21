/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.objective.external;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import me.realized.duels.api.event.match.MatchEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class DuelsObjective extends XLObjective {

    public DuelsObjective() {
        super("DUELS_WINS");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMatchEnd(MatchEndEvent event) {
        if(event.getWinner() == null) return;
        Player player = Bukkit.getPlayer(event.getWinner());
        if(player == null) return;

        for(Tournament tournament : getTournaments()) {
            if(canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }
}
