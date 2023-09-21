/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.objective.external;

import com.badbones69.crazyenvoys.paper.api.events.EnvoyOpenEvent;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class CrazyEnvoyObjective extends XLObjective {

    public CrazyEnvoyObjective() {
        super("CRAZYENVOY_OPEN");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnvoyOpen(EnvoyOpenEvent event) {
        Player player = event.getPlayer();
        for(Tournament tournament : getTournaments()) {
            if(canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }

}
