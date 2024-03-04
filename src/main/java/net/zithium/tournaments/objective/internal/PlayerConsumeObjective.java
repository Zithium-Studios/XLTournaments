package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

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
        return true;
    }


    @EventHandler
    public void playerConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack consumedItem = event.getItem();

        Bukkit.getLogger().log(Level.INFO, "Consumed Item: " + consumedItem.getType());
        if (consumedItem == null || !consumedItem.getType().isEdible()) {
            return;
        }

        for (Tournament tournament : getTournaments()) {

            Bukkit.getLogger().log(Level.WARNING, "Whitelist: " + tournament.getMeta("ITEM_WHITELIST_" + tournament.getIdentifier()));


            if (canExecute(tournament, player)) {
                String tournamentIdentifier = tournament.getIdentifier();

                if (tournament.hasMeta("ITEM_WHITELIST_" + tournamentIdentifier)) {
                    @SuppressWarnings("unchecked")
                    Set<String> whitelist = (Set<String>) tournament.getMeta("ITEM_WHITELIST_" + tournamentIdentifier);
                    Material consumedMaterial = consumedItem.getType();

                    if (whitelist.contains(consumedMaterial.toString())) {
                        tournament.addScore(player.getUniqueId(), 1);
                    }
                } else {
                    // Ignore the whitelist if not present.
                    tournament.addScore(player.getUniqueId(), 1);
                }
            }
        }
    }

}
