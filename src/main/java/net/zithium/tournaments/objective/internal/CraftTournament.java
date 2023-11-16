/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

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

    private final XLTournamentsPlugin plugin;


    public CraftTournament(XLTournamentsPlugin plugin) {
        super("ITEM_CRAFT");
        this.plugin = plugin;
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
        ItemStack craftedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (craftedItem == null) return;

        int amount = craftedItem.getAmount();

        if (event.isShiftClick()) {
            int max = event.getInventory().getMaxStackSize();
            ItemStack[] matrix = event.getInventory().getMatrix();
            for (ItemStack is : matrix) {
                if (is == null || is.getType() == Material.AIR) continue;
                int tmp = is.getAmount();
                if (tmp < max && tmp > 0) max = tmp;
            }
            amount *= max;
        }


        for (Tournament tournament : getTournaments()) {
            if (!canExecute(tournament, player)) {
                return;
            }

            if (tournament.hasMeta("ITEM_WHITELIST")) {

                Set<String> itemWhitelist = (Set<String>) tournament.getMeta("ITEM_WHITELIST");
                Material craftedMaterial = craftedItem.getType();

                if (itemWhitelist.contains(craftedMaterial.toString())) {
                    tournament.addScore(player.getUniqueId(), amount);
                }
            } else {
                tournament.addScore(player.getUniqueId(), amount);
            }
        }
    }
}
