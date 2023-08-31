/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.objective.external;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class NuVotifierObjective extends XLObjective {

    public NuVotifierObjective() {
        super("NUVOTIFIER_VOTES");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerVote(VotifierEvent event) {
        Vote vote = event.getVote();
        String name = vote.getUsername();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        Player player = offlinePlayer.getPlayer();
        if(offlinePlayer.isOnline() && player != null) {
            for (Tournament tournament : getTournaments()) {
                if (canExecute(tournament, player)) {
                    tournament.addScore(player.getUniqueId(), 1);
                }
            }
        }
    }
}
