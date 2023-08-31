/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.objective.internal;

import fun.lewisdev.tournaments.XLTournamentsPlugin;
import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.objective.hook.TEBlockExplode;
import fun.lewisdev.tournaments.tournament.Tournament;
import fun.lewisdev.tournaments.utility.universal.XBlock;
import fun.lewisdev.tournaments.utility.universal.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class BreakObjective extends XLObjective {

    private final XLTournamentsPlugin plugin;
    private final boolean excludePlaced;

    public BreakObjective(XLTournamentsPlugin plugin) {
        super("BLOCK_BREAK");

        FileConfiguration config = plugin.getConfig();
        excludePlaced = config.getBoolean("exclude_placed_blocks");

        if(plugin.getServer().getPluginManager().isPluginEnabled("TokenEnchant") && config.getBoolean("tokenenchant_explode_event")) {
            Bukkit.getServer().getPluginManager().registerEvents(new TEBlockExplode(this, excludePlaced), plugin);
        }

        this.plugin = plugin;
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        if(config.contains("block_whitelist")) {
            tournament.setMeta("BLOCK_WHITELIST", config.getStringList("block_whitelist"));
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(XBlock.isCrop(block) && !XBlock.isFullyGrown(block)) return;

        for(final Tournament tournament : getTournaments()) {
            if(canExecute(tournament, player) && !block.hasMetadata("XLTPlacedBlock")) {

                if(tournament.hasMeta("BLOCK_WHITELIST") && !((List<String>) tournament.getMeta("BLOCK_WHITELIST")).contains(block.getType().toString())) {
                    continue;
                }

                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(excludePlaced) {
            event.getBlock().setMetadata("XLTPlacedBlock", new FixedMetadataValue(plugin, event.getPlayer().getName()));
        }
    }
}
