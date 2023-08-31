/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.objective.external;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.tournament.Tournament;
import me.clip.chatreaction.events.ReactionWinEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class CrackShotDamageObjective extends XLObjective {

    public CrackShotDamageObjective() {
        super("CRACKSHOT_DAMAGE");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeaponDamage(WeaponDamageEntityEvent event) {
        Player player = event.getPlayer();
        if(player == null) return;

        for(Tournament tournament : getTournaments()) {
            if(canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), (int) Math.round(event.getDamage()));
            }
        }
    }
}
