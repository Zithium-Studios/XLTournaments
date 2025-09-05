package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class PlayerConsumeObjective extends XLObjective {

    public PlayerConsumeObjective() {
        super("PLAYER_CONSUME");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        if (config.contains("item_whitelist")) {
            Set<String> itemWhitelist = new HashSet<>(config.getStringList("item_whitelist"));
            tournament.setMeta("ITEM_WHITELIST_" + tournament.getIdentifier(), itemWhitelist);
        }

        if (config.contains("item_blacklist")) {
            Set<String> itemBlacklist = new HashSet<>(config.getStringList("item_blacklist"));
            tournament.setMeta("ITEM_BLACKLIST_" + tournament.getIdentifier(), itemBlacklist);
        }
        return true;
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack consumedItem = event.getItem();

        if (consumedItem == null || !consumedItem.getType().isEdible()) {
            return;
        }

        Material consumedMaterial = consumedItem.getType();

        for (Tournament tournament : getTournaments()) {
            if (!canExecute(tournament, player)) continue;

            String tournamentIdentifier = tournament.getIdentifier();

            // Check blacklist first
            if (tournament.hasMeta("ITEM_BLACKLIST_" + tournamentIdentifier)) {
                @SuppressWarnings("unchecked")
                Set<String> blacklist = (Set<String>) tournament.getMeta("ITEM_BLACKLIST_" + tournamentIdentifier);

                if (blacklist.contains(consumedMaterial.toString())) {
                    continue; // skip scoring for this tournament
                }
            }

            // Check whitelist if it exists
            if (tournament.hasMeta("ITEM_WHITELIST_" + tournamentIdentifier)) {
                @SuppressWarnings("unchecked")
                Set<String> whitelist = (Set<String>) tournament.getMeta("ITEM_WHITELIST_" + tournamentIdentifier);

                if (whitelist.contains(consumedMaterial.toString())) {
                    tournament.addScore(player.getUniqueId(), 1);
                }
            } else {
                // No whitelist → allow all items (except blacklisted ones already filtered above)
                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }
}
