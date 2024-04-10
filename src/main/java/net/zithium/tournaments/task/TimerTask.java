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

import java.util.Iterator;
import java.util.logging.Level;

public class TimerTask implements Runnable {

    private static final JavaPlugin JAVA_PLUGIN = JavaPlugin.getProvidingPlugin(XLTournamentsPlugin.class);
    private final TournamentManager tournamentManager;

    public TimerTask(TournamentManager tournamentManager) {
        this.tournamentManager = tournamentManager;
    }

    public void run() {
        Iterator<Tournament> iterator = tournamentManager.getTournaments().stream().filter(tournament -> tournament.getStatus() != TournamentStatus.ENDED).iterator();
        while (iterator.hasNext()) {
            Tournament tournament = iterator.next();

            // Tournament starting
            if (tournament.getStatus() == TournamentStatus.WAITING && tournament.getStartTimeMillis() < System.currentTimeMillis()) {
                tournament.start(true);
                continue;
            }

            // Tournament ended
            if (tournament.getEndTimeMillis() < System.currentTimeMillis()) {
                if (tournament.getStatus() == TournamentStatus.ENDED) {
                    if (XLTournamentsPlugin.isDebugMode()) JAVA_PLUGIN.getLogger().log(Level.INFO, "Attempted to end already ended tournament. This has been averted, please report to developer.");
                    iterator.remove();
                    continue;
                }

                tournament.stop();
                tournament.setStatus(TournamentStatus.ENDED);
                Bukkit.getScheduler().runTaskAsynchronously(JAVA_PLUGIN, tournament::clearParticipants);

                if (tournament.getTimeline() != Timeline.SPECIFIC) {
                    Bukkit.getScheduler().runTaskLater(JAVA_PLUGIN, () -> {
                        tournament.updateStatus();
                        tournament.start(true);
                    }, 100L);
                }
            }

            // Remove the tournament if it's already ended
            if (tournament.getStatus() == TournamentStatus.ENDED) {
                try {
                    iterator.remove();
                } catch (IllegalStateException e) {
                    // Catch possible error when attempting to remove tournament.
                    Bukkit.getLogger().log(Level.WARNING, "Error removing already ended tournament", e);
                }
            }

        }
    }
}
