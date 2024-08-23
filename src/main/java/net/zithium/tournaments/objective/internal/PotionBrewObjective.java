package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PotionBrewObjective extends XLObjective {

    private final XLTournamentsPlugin plugin;
    private boolean applyMetadata;

    public PotionBrewObjective(XLTournamentsPlugin plugin) {
        super("POTION_BREW");

        this.plugin = plugin;
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        if (config.contains("apply_potion_metadata")) {
            applyMetadata = config.getBoolean("apply_potion_metadata");
        }
        return true;
    }

    @EventHandler
    public void onBrewPotion(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof BrewerInventory)) return;

        if (event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            Player player = (Player) event.getWhoClicked();

            ItemStack item = event.getCurrentItem();
            if (item != null) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null) {
                    // Check if the potion does not have the new PersistentDataType
                    NamespacedKey key = new NamespacedKey(plugin, "XLPotion");
                    PersistentDataType<Integer, Integer> dataType = PersistentDataType.INTEGER;
                    Integer value = itemMeta.getPersistentDataContainer().get(key, dataType);

                    if (value == null) {
                        // The potion does not have the PersistentDataType set
                        // Add a score to the player for the associated tournament to ensure no duping.
                        for (Tournament tournament : getTournaments()) {
                            if (canExecute(tournament, player)) {
                                tournament.addScore(player.getUniqueId(), 1);
                            }
                        }

                        // Only apply the meta data if the configuration option is set to true.
                        if (applyMetadata) {
                            item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
                            itemMeta.getPersistentDataContainer().set(key, dataType, 0);
                            item.setItemMeta(itemMeta);
                        }
                    }
                }
            }
        }
    }
}
