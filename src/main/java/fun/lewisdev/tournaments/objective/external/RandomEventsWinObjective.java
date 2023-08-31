/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.objective.external;

import com.adri1711.randomevents.api.events.ReventEndEvent;
import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class RandomEventsWinObjective extends XLObjective {

    public RandomEventsWinObjective() {
        super("RANDOMEVENTS_WINS");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler
    public void onEventEnd(ReventEndEvent event) {
        for(Tournament tournament : getTournaments()) {
            for(Player player : event.getWinners()) {
                if (canExecute(tournament, player)) {
                    tournament.addScore(player.getUniqueId(), 1);
                }
            }
        }
    }
}
