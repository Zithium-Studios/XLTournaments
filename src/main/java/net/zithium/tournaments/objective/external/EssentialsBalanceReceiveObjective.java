/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.objective.external;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class EssentialsBalanceReceiveObjective extends XLObjective {

    public EssentialsBalanceReceiveObjective() {
        super("ESSENTIALS_BALANCE_RECEIVE");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler
    public void onBalanceUpdate(UserBalanceUpdateEvent event) {
        if(event.getCause() == UserBalanceUpdateEvent.Cause.COMMAND_PAY) return;

        long oldBalance = event.getOldBalance().longValue();
        long newBalance = event.getNewBalance().longValue();

        if(newBalance > oldBalance) {
            Player player = event.getPlayer();
            for(Tournament tournament : getTournaments()) {
                if(canExecute(tournament, player)) {
                    tournament.addScore(player.getUniqueId(), (int) (newBalance - oldBalance));
                }
            }
        }
    }
}
