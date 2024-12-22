package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class CraftTournament extends XLObjective {


    public CraftTournament() {
        super("ITEM_CRAFT");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        if (config.contains("item_whitelist")) {
            Set<String> itemWhitelist = new HashSet<>(config.getStringList("item_whitelist"));
            tournament.setMeta("ITEM_WHITELIST", itemWhitelist);
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack craftedItem = event.getRecipe().getResult(); // Get the recipe's result.
        if (craftedItem == null) return;

        int amount = craftedItem.getAmount();

        // Handle shift-click to calculate all items crafted in one go.
        if (event.isShiftClick()) {
            // Estimate the maximum number of items crafted in one shift-click.
            int maxPossibleCrafts = calculateMaxCrafts(event);
            amount *= maxPossibleCrafts;
        }

        for (Tournament tournament : getTournaments()) {
            if (!canExecute(tournament, player)) continue;

            Set<String> itemWhitelist = getItemWhitelist(tournament);
            Material craftedMaterial = craftedItem.getType();

            if (itemWhitelist == null || itemWhitelist.contains(craftedMaterial.toString())) {
                tournament.addScore(player.getUniqueId(), amount);
            }
        }
    }

    private Set<String> getItemWhitelist(Tournament tournament) {
        if (tournament.hasMeta("ITEM_WHITELIST")) {
            return (Set<String>) tournament.getMeta("ITEM_WHITELIST");
        }
        return null;
    }

    private int calculateMaxCrafts(CraftItemEvent event) {
        ItemStack[] matrix = event.getInventory().getMatrix();
        int maxCrafts = Integer.MAX_VALUE;

        for (ItemStack ingredient : matrix) {
            if (ingredient != null) {
                maxCrafts = Math.min(maxCrafts, ingredient.getAmount());
            }
        }

        return maxCrafts;
    }
}
