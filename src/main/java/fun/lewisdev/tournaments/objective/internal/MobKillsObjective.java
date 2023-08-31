/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.objective.internal;

import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

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

                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }

}
