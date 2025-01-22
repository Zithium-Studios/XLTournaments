/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.objective.external;

import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class CrazyCratesObjective extends XLObjective {

    public CrazyCratesObjective() {
        super("CRAZYCRATES_OPEN");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPrize(PlayerPrizeEvent event) {
        Player player = event.getPlayer();
        for (Tournament tournament : getTournaments()) {
            if (canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }
}