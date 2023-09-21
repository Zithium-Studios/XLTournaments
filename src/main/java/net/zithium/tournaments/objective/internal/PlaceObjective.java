/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class PlaceObjective extends XLObjective {

    public PlaceObjective() {
        super("BLOCK_PLACE");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        if(config.contains("block_whitelist")) {
            tournament.setMeta("BLOCK_WHITELIST", config.getStringList("block_whitelist"));
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        for(Tournament tournament : getTournaments()) {
            if(canExecute(tournament, player)) {

                if(tournament.hasMeta("BLOCK_WHITELIST") && !((List<String>) tournament.getMeta("BLOCK_WHITELIST")).contains(event.getBlock().getType().toString())) {
                    continue;
                }

                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }

}
