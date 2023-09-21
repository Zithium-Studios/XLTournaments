/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.utility.universal.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class CraftTournament extends XLObjective {

    public CraftTournament() {
        super("ITEM_CRAFT");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        String objective = config.getString("objective");
        if(objective.contains(";")) {
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(objective.split(";")[1]);
            if (xMaterial.isPresent()) {
                tournament.setMeta("ITEM_CRAFT", xMaterial.get().parseMaterial());
                return true;
            }
        }

        JavaPlugin.getProvidingPlugin(XLTournamentsPlugin.class).getLogger().warning("The crafting material in tournament " + tournament.getIdentifier() + " is invalid.");
        return false;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemCraft(CraftItemEvent event) {
        ItemStack craftedItem = event.getCurrentItem();
        int amount = craftedItem.getAmount();

        if (event.isShiftClick()) {
            int max = event.getInventory().getMaxStackSize();
            ItemStack[] matrix = event.getInventory().getMatrix();
            for (ItemStack is: matrix) {
                if (is == null || is.getType() == Material.AIR) continue;
                int tmp = is.getAmount();
                if (tmp < max && tmp > 0) max = tmp;
            }
            amount *= max;
        }

        Player player = (Player) event.getWhoClicked();
        for(Tournament tournament : getTournaments()) {
            if(tournament.getMeta("ITEM_CRAFT") == craftedItem.getType() && canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), amount);
            }
        }
    }

}
