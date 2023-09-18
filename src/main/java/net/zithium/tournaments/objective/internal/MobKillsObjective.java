/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2023 Zithium Studios. All rights reserved.
 */

package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class MobKillsObjective extends XLObjective {

    public MobKillsObjective() {
        super("MOB_KILLS");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        if(config.contains("mob_whitelist")) {
            tournament.setMeta("MOB_WHITELIST", config.getStringList("mob_whitelist"));
        }

        if (config.contains("player_kills_only")) {
            tournament.setMeta("PLAYER_KILLS_ONLY", config.getBoolean("player_kills_only"));
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity instanceof Player || entity.getKiller() == null) return;

        Player player = entity.getKiller();

        for(Tournament tournament : getTournaments()) {
            if(canExecute(tournament, player)) {

                if(tournament.hasMeta("MOB_WHITELIST") && !((List<String>) tournament.getMeta("MOB_WHITELIST")).contains(entity.getType().toString())) {
                    continue;
                }

                if (tournament.hasMeta("PLAYER_KILLS_ONLY")) {
                    if (entity.getKiller() != player) {
                        return;
                    }
                }

                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }

}
