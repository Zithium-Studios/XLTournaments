package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerConsumeObjective extends XLObjective {
    public PlayerConsumeObjective() {
        super("PLAYER_CONSUME");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        if (config.contains("item_whitelist")) {
            tournament.setMeta("ITEM_WHITELIST_" + tournament.getIdentifier(), config.getStringList("item_whitelist"));
        }
        return true;
    }


    @EventHandler
    public void playerConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack consumedItem = event.getItem();

        if (consumedItem == null || !consumedItem.getType().isEdible()) {
            return;
        }

        for (Tournament tournament : getTournaments()) {
            if (canExecute(tournament, player)) {
                String tournamentIdentifier = tournament.getIdentifier();

                if (tournament.hasMeta("ITEM_WHITELIST_" + tournamentIdentifier)) {
                    List<String> whitelist = (List<String>) tournament.getMeta("ITEM_WHITELIST_" + tournamentIdentifier);

                    String item = consumedItem.toString();

                    if (whitelist.contains(item)) {
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
