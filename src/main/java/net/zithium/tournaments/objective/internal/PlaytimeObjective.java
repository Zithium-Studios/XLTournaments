/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class PlaytimeObjective extends XLObjective {

    private final JavaPlugin JAVA_PLUGIN = JavaPlugin.getProvidingPlugin(XLTournamentsPlugin.class);
    private BukkitTask task;

    public PlaytimeObjective() {
        super("PLAYTIME");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        if (task == null || task.isCancelled()) {
            int time = JAVA_PLUGIN.getConfig().getInt("playtime_objective_task_update", 200);
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(JAVA_PLUGIN, this::updatePlaytime, 20L, time);
        }

        return true;
    }

    private void updatePlaytime() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Tournament tournament : getTournaments()) {
                if (canExecute(tournament, player)) {
                    tournament.addScore(player.getUniqueId(), 10);
                }
            }
        }
    }

}
