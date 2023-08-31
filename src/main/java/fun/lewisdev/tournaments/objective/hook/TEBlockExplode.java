/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.objective.hook;

import com.vk2gpz.tokenenchant.event.TEBlockExplodeEvent;
import fun.lewisdev.tournaments.objective.internal.BreakObjective;
import fun.lewisdev.tournaments.tournament.Tournament;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class TEBlockExplode implements Listener {

    private final BreakObjective breakObjective;
    private final boolean excludePlaced;

    public TEBlockExplode(BreakObjective breakObjective, boolean excludePlaced) {
        this.breakObjective = breakObjective;
        this.excludePlaced = excludePlaced;
    }

    @EventHandler
    public void onBlockExplode(TEBlockExplodeEvent event) {
        Player player = event.getPlayer();

        for (Tournament tournament : breakObjective.getTournaments()) {
            if (breakObjective.canExecute(tournament, player)) {
                int amount = event.blockList().size();

                for (Block block : event.blockList()) {
                    if (excludePlaced && block.hasMetadata("XLTPlacedBlock") || tournament.hasMeta("BLOCK_WHITELIST") && !((List<String>) tournament.getMeta("BLOCK_WHITELIST")).contains(event.getBlock().getType().toString())) {
                        amount--;
                    }
                }

                if (amount > 0) {
                    tournament.addScore(player.getUniqueId(), amount);
                }
            }
        }
    }


}
