/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.objective.external;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class MythicMobsObjective extends XLObjective {

    public MythicMobsObjective() {
        super("MYTHICMOBS_KILLS");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(MythicMobDeathEvent event) {
        if(!(event.getKiller() instanceof Player)) return;

        Player player = (Player) event.getKiller();
        for (Tournament tournament : getTournaments()) {
            if (canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }
}
