/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.task;

import fun.lewisdev.tournaments.XLTournamentsPlugin;
import fun.lewisdev.tournaments.tournament.Tournament;
import fun.lewisdev.tournaments.tournament.TournamentManager;
import fun.lewisdev.tournaments.tournament.TournamentStatus;
import fun.lewisdev.tournaments.utility.Timeline;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

public class TimerTask implements Runnable {

    private static final JavaPlugin JAVA_PLUGIN = JavaPlugin.getProvidingPlugin(XLTournamentsPlugin.class);
    private final TournamentManager tournamentManager;

    public TimerTask(TournamentManager tournamentManager) {
        this.tournamentManager = tournamentManager;
    }

    @Override
    public void run() {
        for(Tournament tournament : tournamentManager.getTournaments().stream().filter(tournament -> tournament.getStatus() != TournamentStatus.ENDED).collect(Collectors.toList())) {

            // Tournament starting
            if(tournament.getStatus() == TournamentStatus.WAITING && tournament.getStartTimeMillis() < System.currentTimeMillis()) {
                tournament.start(true);
                continue;
            }

            // Tournament ended
            if(tournament.getEndTimeMillis() < System.currentTimeMillis()) {

                tournament.stop();
                tournament.setStatus(TournamentStatus.ENDED);

                if(tournament.getTimeline() != Timeline.SPECIFIC) {
                    Bukkit.getScheduler().runTaskAsynchronously(JAVA_PLUGIN, tournament::clearParticipants);
                    tournament.setStatus(TournamentStatus.ENDED);

                    Bukkit.getScheduler().runTaskLater(JAVA_PLUGIN, () -> {
                        tournament.updateStatus();
                        tournament.start(true);
                    }, 100L);
                }
            }

        }
    }
}
