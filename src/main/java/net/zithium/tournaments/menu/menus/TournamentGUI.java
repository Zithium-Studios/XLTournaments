/*
 * XLTournaments Plugin
 * Copyright (2) 2023 Zithium Studios
 */

package net.zithium.tournaments.menu.menus;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.zithium.library.items.ItemStackBuilder;
import net.zithium.library.utils.Color;
import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.hook.HookManager;
import net.zithium.tournaments.hook.hooks.VaultHook;
import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.tournament.TournamentManager;
import net.zithium.tournaments.tournament.TournamentStatus;
import net.zithium.tournaments.utility.GuiUtils;
import net.zithium.tournaments.config.Messages;
import net.zithium.tournaments.utility.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Optional;
import java.util.logging.Level;

public class TournamentGUI {

    private final XLTournamentsPlugin plugin;

    public TournamentGUI(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
    }

    public void openInventory(Player player) {
        FileConfiguration config = plugin.getMenuFile().getConfig();

        PaginatedGui gui = Gui.paginated()
                .title(Component.text(Color.stringColor(config.getString("title"))))
                .rows(config.getInt("rows"))
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        GuiUtils.setFillerItems(gui, config.getConfigurationSection("filler_items"));


        {// Tournament items
            TournamentManager tournamentManager = plugin.getTournamentManager();
            ConfigurationSection section = config.getConfigurationSection("tournament_items");
            if (section != null) {
                for (String entry : section.getKeys(false)) {
                    Optional<Tournament> optionalTournament = tournamentManager.getTournament(entry);
                    if (!optionalTournament.isPresent()) continue;
                    Tournament tournament = optionalTournament.get();

                    // Not displaying tournaments in the menu if they are not running.
                    if (config.getBoolean("hide_completed_tournaments", false)) {
                        if (tournament.getStatus() == TournamentStatus.ENDED) {
                            continue;
                        }
                    }

                    ItemStackBuilder builder = null;
                    TournamentStatus status = tournament.getStatus();
                    if (status == TournamentStatus.ACTIVE) {
                        builder = ItemStackBuilder.getItemStack(config.getConfigurationSection(section.getCurrentPath() + "." + entry + ".active"));
                        builder.withLore(TextUtil.setPlaceholders(config.getStringList(section.getCurrentPath() + "." + entry + ".active.lore"), player.getUniqueId(), tournament));
                    } else if (status == TournamentStatus.WAITING) {
                        builder = ItemStackBuilder.getItemStack(config.getConfigurationSection(section.getCurrentPath() + "." + entry + ".waiting"));
                        builder.withLore(TextUtil.setPlaceholders(config.getStringList(section.getCurrentPath() + "." + entry + ".waiting.lore"), player.getUniqueId(), tournament));
                    } else if (status == TournamentStatus.ENDED) {
                        builder = ItemStackBuilder.getItemStack(config.getConfigurationSection(section.getCurrentPath() + "." + entry + ".ended"));
                        builder.withLore(TextUtil.setPlaceholders(config.getStringList(section.getCurrentPath() + "." + entry + ".ended.lore"), player.getUniqueId(), tournament));
                    }

                    GuiItem guiItem = new GuiItem(builder.build());
                    guiItem.setAction(event -> {

                        switch (status) {
                            case ACTIVE:
                                if (tournament.isParticipant(player.getUniqueId())) {
                                    Messages.ALREADY_PARTICIPATING.send(player);
                                    gui.close(player);
                                    return;
                                }

                                Permission permission = tournament.getParticipationPermission();
                                if (permission != null && !player.hasPermission(permission)) {
                                    Messages.TOURNAMENT_NO_PERMISSION.send(player);
                                    gui.close(player);
                                    return;
                                }

                                HookManager hookManager = plugin.getHookManager();
                                double cost = tournament.getParticipationCost();
                                if (hookManager.isHookEnabled("Vault") && cost > 0) {
                                    VaultHook vaultHook = (VaultHook) hookManager.getPluginHook("Vault");
                                    if (vaultHook.getBalance(player) >= cost) {
                                        vaultHook.withdraw(player, cost);
                                        tournament.addParticipant(player.getUniqueId(), 0, true);
                                        plugin.getActionManager().executeActions(player, tournament.getParticipationActions());
                                        gui.close(player);
                                    } else {
                                        Messages.NOT_ENOUGH_FUNDS.send(player, "{AMOUNT}", String.valueOf(cost));
                                        gui.close(player);
                                    }
                                } else {
                                    tournament.addParticipant(player.getUniqueId(), 0, true);
                                    plugin.getActionManager().executeActions(player, tournament.getParticipationActions());
                                    gui.close(player);
                                }
                                break;
                            case WAITING:
                                Messages.TOURNAMENT_WAITING.send(player);
                                gui.close(player);
                                break;
                            case ENDED:
                                Messages.TOURNAMENT_ENDED.send(player);
                                gui.close(player);
                                break;
                        }

                    });

                    gui.addItem(guiItem);

                    // Adding the next & previous page items.
                    addPageItems(gui);

                }
            }
        }
        gui.open(player);

    }

    private void addPageItems(PaginatedGui gui) {

        FileConfiguration config = plugin.getMenuFile().getConfig();
        // Making sure they are enabled in the config before adding them.
        if (config.getBoolean("page_items.enabled", true)) {
            GuiItem nextPage = new GuiItem(ItemStackBuilder.getItemStack(config.getConfigurationSection("page_items.next_page")).build());
            nextPage.setAction(event -> gui.next());
            gui.setItem(config.getInt("page_items.next_page.slot"), nextPage);

            GuiItem previousPage = new GuiItem(ItemStackBuilder.getItemStack(config.getConfigurationSection("page_items.previous_page")).build());
            previousPage.setAction(event -> gui.previous());
            gui.setItem(config.getInt("page_items.previous_page.slot"), previousPage);
        }
    }

    /**
     * Creates a paginated graphical user interface (GUI) for tournaments.
     * <p>
     * This method creates a PaginatedGui for displaying tournament items in a GUI menu. The GUI
     * title and row count are determined based on the provided parameters, with support for
     * components for versions that allow it.
     * </p>
     * @param rows   The number of rows in the GUI.
     * @param title  The title of the GUI.
     * @return A PaginatedGui instance for displaying tournament items.
     */
    private PaginatedGui createGUI(int rows, String title) {
        String stringTitle = Color.stringColor(title);
        Component componentTitle = LegacyComponentSerializer.legacyAmpersand().deserialize(stringTitle);
        if (supportsComponents()) {
            return Gui.paginated().title(componentTitle).rows(rows).create();
        } else {
            @SuppressWarnings("deprecation") // new PaginatedGui is deprecated but needed as certain versions doesn't have components.
            PaginatedGui gui = new PaginatedGui(rows, title);
            return gui;
        }
    }

    private boolean supportsComponents() {
        if (Bukkit.getServer().getVersion().contains("1.18.2")) {
            return true;
        } else {
            return false;
        }
    }

}
