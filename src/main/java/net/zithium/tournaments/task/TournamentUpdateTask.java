/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2024 Zithium Studios. All rights reserved.
 */

package net.zithium.tournaments.task;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.tournament.TournamentManager;
import net.zithium.tournaments.tournament.TournamentStatus;
import net.zithium.tournaments.utility.Timeline;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class TournamentUpdateTask extends BukkitRunnable {

    private static final JavaPlugin JAVA_PLUGIN = JavaPlugin.getProvidingPlugin(XLTournamentsPlugin.class);
    private final TournamentManager tournamentManager;

    public TournamentUpdateTask(TournamentManager tournamentManager) {
        this.tournamentManager = tournamentManager;
    }

    public void run() {
        Iterator<Tournament> iterator = tournamentManager.getTournaments().stream().filter(tournament -> tournament.getStatus() != TournamentStatus.ENDED).iterator(); // Filters out already ended tournaments.
        while (iterator.hasNext()) {
            Tournament tournament = iterator.next();

            // Tournament starting
            if (tournament.getStatus() == TournamentStatus.WAITING && tournament.getStartTimeMillis() < System.currentTimeMillis()) {
                tournament.start(true);
                continue;
            }

            // Tournament ended
            if (tournament.getEndTimeMillis() < System.currentTimeMillis()) {
                tournament.stop();

                if (tournament.getTimeline() != Timeline.SPECIFIC) {
                    Bukkit.getScheduler().runTaskLater(JAVA_PLUGIN, () -> {
                        tournament.updateStatus();
                        tournament.start(true);
                    }, 100L);
                }
            }
        }
    }
}
