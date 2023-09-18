package net.zithium.tournaments.utility;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class GuiUtils {

    public static void setSlot(BaseGui gui, GuiItem guiItem, ConfigurationSection section) {
        if (section.getBoolean("fill_border")) {
            gui.getFiller().fillBorder(guiItem);
        }

        if (section.contains("slot")) {
            int slot = section.getInt("slot");
            if (slot == -1) {
                gui.getFiller().fill(guiItem);
            } else {
                gui.setItem(slot, guiItem);
            }
        } else if (section.contains("slots")) {
            for (String slot : section.getStringList("slots")) {
                if (slot.contains("-")) {
                    String[] args = slot.split("-");
                    for (int i = Integer.parseInt(args[0]); i < Integer.parseInt(args[1]) + 1; ++i)
                        gui.setItem(i, guiItem);
                } else {
                    gui.setItem(Integer.parseInt(slot), guiItem);
                }

            }
        }
    }

    /**
     * @param gui     The gui this method is being used in.
     * @param section The configuration section to grab the information from.
     */
    public static void setFillerItems(BaseGui gui, ConfigurationSection section) {
        if (section != null) {
            for (String entry : section.getKeys(false)) {
                ConfigurationSection itemSection = section.getConfigurationSection(entry);

                ItemStackBuilder itemStackBuilder = ItemStackBuilder.getItemStack(itemSection);
                ItemStack fillerItem = itemStackBuilder.build();

                if (fillerItem.hasItemMeta() && fillerItem.getItemMeta().hasLore()) {
                    List<String> newLore = new ArrayList<>(fillerItem.getItemMeta().getLore());
                    itemStackBuilder.withLore(newLore);
                }

                GuiItem fillerGuiItem = new GuiItem(itemStackBuilder.build());

                setSlot(gui, fillerGuiItem, itemSection);
            }
        }
    }

}