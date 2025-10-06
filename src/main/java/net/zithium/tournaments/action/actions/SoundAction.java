/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.action.actions;

import com.cryptomorin.xseries.XSound;
import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.action.Action;
import org.bukkit.entity.Player;

public class SoundAction implements Action {

    @Override
    public String getIdentifier() {
        return "SOUND";
    }

    @Override
    public void execute(XLTournamentsPlugin plugin, Player player, String data) {
        try {
            player.playSound(player.getLocation(), XSound.matchXSound(data).get().parseSound(), 1L, 1L);
        } catch (Exception ex) {
            plugin.getLogger().warning("Invalid sound name in action: " + data.toUpperCase());
        }
    }
}
