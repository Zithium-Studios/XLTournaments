/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.objective.external;

import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class BedWars1058FinalKillsObjective extends XLObjective {

    public BedWars1058FinalKillsObjective() {
        super("BEDWARS1058_FINAL_KILLS");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler
    public void onPlayerKill(PlayerKillEvent event) {
        if(!event.getCause().isFinalKill()) return;

        Player player = event.getKiller();
        if(player == null) return;

        for(Tournament tournament : getTournaments()) {
            if (canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }
}
