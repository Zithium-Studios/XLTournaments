/*
 * XLTournaments Plugin
 * Copyright (c) 2023 Zithium Studios. All rights reserved.
 */

package net.zithium.tournaments.objective;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.tournament.TournamentStatus;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public abstract class XLObjective implements Listener {

    private final String identifier;
    private boolean listenerRegistered;
    private final Map<String, Tournament> tournamentsLinked;

    public XLObjective(String identifier) {
        this.identifier = identifier;
        tournamentsLinked = new HashMap<>();
        listenerRegistered = false;
    }

    public abstract boolean loadTournament(Tournament tournament, FileConfiguration config);

    public void addTournament(Tournament tournament) {
        if(!listenerRegistered) {
            Bukkit.getServer().getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(XLTournamentsPlugin.class));
            listenerRegistered = true;
        }
        tournamentsLinked.put(tournament.getIdentifier(), tournament);
    }

    public boolean canExecute(Tournament tournament, Player player) {
        UUID uuid = player.getUniqueId();
        return tournament.isParticipant(uuid) && !tournament.hasFinishedChallenge(uuid) && !tournament.getDisabledWorlds().contains(player.getWorld().getName()) && !tournament.getDisabledGamemodes().contains(player.getGameMode());
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Tournament> getTournaments() {
        return Collections.unmodifiableList(tournamentsLinked.values().stream().filter(tournament -> tournament.getStatus() == TournamentStatus.ACTIVE).collect(Collectors.toList()));
    }
}
