/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.objective.external;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PlaceholderAPIObjective extends XLObjective {

    private final JavaPlugin JAVA_PLUGIN = JavaPlugin.getProvidingPlugin(XLTournamentsPlugin.class);
    private BukkitTask task;

    public PlaceholderAPIObjective() {
        super("PLACEHOLDERAPI");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        String objective = config.getString("objective");
        if (objective.contains(";")) {
            String placeholder = objective.substring(objective.lastIndexOf(";") + 1).replace("%", "");
            tournament.setMeta("PLACEHOLDER", placeholder);

            if (task == null || task.isCancelled()) {
                int time = JAVA_PLUGIN.getConfig().getInt("placeholderapi_objective_task_update", 100);
                task = Bukkit.getScheduler().runTaskTimerAsynchronously(JAVA_PLUGIN, this::updatePlaceholders, 20L, time);
            }
            return true;
        }

        JAVA_PLUGIN.getLogger().warning("The PlaceholderAPI placeholder in tournament " + tournament.getIdentifier() + " is invalid.");
        return true;
    }

    public void updatePlaceholders() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            for (Tournament tournament : getTournaments()) {
                if (tournament.hasMeta("PLACEHOLDER")) {
                    if (!canExecute(tournament, player)) continue;

                    int value;
                    try {
                        value = Integer.parseInt(PlaceholderAPI.setPlaceholders((OfflinePlayer) player, "%" + tournament.getMeta("PLACEHOLDER") + "%"));
                    } catch (Exception ex) {
                        JAVA_PLUGIN.getLogger().warning("Couldn't convert placeholder value to valid number; %"+tournament.getMeta("PLACEHOLDER")+"% returned an invalid number for user "+player.getName());
                        continue;
                    }

                    tournament.addScore(uuid, value, true);
                }
            }
        }
    }
}
