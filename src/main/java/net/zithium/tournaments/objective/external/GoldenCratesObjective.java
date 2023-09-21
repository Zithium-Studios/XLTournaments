/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.objective.external;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import su.nightexpress.goldencrates.api.event.CrateOpenEvent;

public class GoldenCratesObjective extends XLObjective {

    public GoldenCratesObjective() {
        super("GOLDENCRATES_OPEN");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler
    public void onCrateOpen(CrateOpenEvent event) {
        Player player = event.getPlayer();
        for(Tournament tournament : getTournaments()) {
            if(canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }
}
