/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.objective.external;

import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;

public class BedWars1058WinObjective extends XLObjective {

    public BedWars1058WinObjective() {
        super("BEDWARS1058_WINS");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        for(Tournament tournament : getTournaments()) {
            for(UUID uuid : event.getWinners()) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) continue;

                if (canExecute(tournament, player)) {
                    tournament.addScore(uuid, 1);
                }
            }
        }
    }
}
