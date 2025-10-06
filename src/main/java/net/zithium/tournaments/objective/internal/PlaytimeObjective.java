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

    private final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(XLTournamentsPlugin.class);
    private BukkitTask task;

    public PlaytimeObjective() {
        super("PLAYTIME");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        if (task == null || task.isCancelled()) {
            int intervalTicks = plugin.getConfig().getInt("playtime_objective_task_update", 200);
            task = Bukkit.getScheduler().runTaskTimer(plugin, this::updatePlaytime, 20L, intervalTicks);
        }
        return true;
    }

    private void updatePlaytime() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Tournament tournament : getTournaments()) {
                if (canExecute(tournament, player)) {
                    int intervalTicks = plugin.getConfig().getInt("playtime_objective_task_update", 200);
                    int seconds = intervalTicks / 20; // convert ticks → seconds

                    tournament.addScore(player.getUniqueId(), seconds);
                }
            }
        }
    }
}
