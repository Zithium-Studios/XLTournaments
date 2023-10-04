package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;


public class PotionBrewObjective extends XLObjective {
    public PotionBrewObjective() {
        super("POTION_BREW");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }


    @EventHandler(ignoreCancelled = true)
    public void onCraftPotion(CraftItemEvent event){
        Player player = (Player) event.getWhoClicked();

        if (!event.getInventory().getType().equals(InventoryType.BREWING)) return;
        if (!event.getRecipe().getResult().getType().equals(Material.POTION)) return;

        for(final Tournament tournament : getTournaments()) {
            if(canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), 1);
            }
        }

    }
}
