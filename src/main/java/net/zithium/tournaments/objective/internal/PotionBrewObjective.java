package net.zithium.tournaments.objective.internal;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.Material;
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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

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

            // Ensure the item is a potion and not a water bottle
            if (item != null && item.getType() == Material.POTION) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta instanceof PotionMeta) {
                    PotionMeta potionMeta = (PotionMeta) itemMeta;

                    // Check that the potion is not a water bottle
                    if (potionMeta.getBasePotionData().getType() != PotionType.WATER) {
                        NamespacedKey key = new NamespacedKey(plugin, "XLPotion");
                        PersistentDataType<Integer, Integer> dataType = PersistentDataType.INTEGER;
                        Integer value = potionMeta.getPersistentDataContainer().get(key, dataType);

                        // Only award points if the potion has not been scored
                        if (value == null) {
                            for (Tournament tournament : getTournaments()) {
                                if (canExecute(tournament, player)) {
                                    tournament.addScore(player.getUniqueId(), 1);
                                }
                            }

                            // Apply metadata if configuration option is set to true.
                            if (applyMetadata) {
                                item.addUnsafeEnchantment(Enchantment.AQUA_AFFINITY, 1);
                            }

                            // Mark the potion as scored to prevent future scoring
                            potionMeta.getPersistentDataContainer().set(key, dataType, 1);
                            item.setItemMeta(potionMeta);
                        }
                    }
                }
            }
        }
    }


}
