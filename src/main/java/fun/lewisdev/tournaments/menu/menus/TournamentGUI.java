/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 * Copyright (2) 2023 Zithium Studios
 */

package fun.lewisdev.tournaments.menu.menus;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import fun.lewisdev.tournaments.XLTournamentsPlugin;
import fun.lewisdev.tournaments.hook.HookManager;
import fun.lewisdev.tournaments.hook.hooks.VaultHook;
import fun.lewisdev.tournaments.tournament.Tournament;
import fun.lewisdev.tournaments.tournament.TournamentManager;
import fun.lewisdev.tournaments.tournament.TournamentStatus;
import fun.lewisdev.tournaments.utility.GuiUtils;
import fun.lewisdev.tournaments.utility.ItemStackBuilder;
import fun.lewisdev.tournaments.utility.Messages;
import fun.lewisdev.tournaments.utility.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Optional;

public class TournamentGUI {

    private final XLTournamentsPlugin plugin;

    public TournamentGUI(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
    }

    public void openInventory(Player player) {
        FileConfiguration config = plugin.getMenuFile().getConfig();

        //Gui gui = new Gui(config.getInt("rows"), TextUtil.color(config.getString("title")));

        PaginatedGui gui = Gui.paginated()
                .title(Component.text(TextUtil.color(config.getString("title"))))
                .rows(config.getInt("rows"))
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        GuiUtils.setFillerItems(gui, config.getConfigurationSection("filler_items"));

        GuiItem nextPage = new GuiItem(ItemStackBuilder.getItemStack(config.getConfigurationSection("page_items.next_page")).build());
        nextPage.setAction(event -> gui.next());
        gui.setItem(config.getInt("page_items.next_page.slot"), nextPage);

        GuiItem previousPage = new GuiItem(ItemStackBuilder.getItemStack(config.getConfigurationSection("page_items.previous_page")).build());
        previousPage.setAction(event -> gui.previous());
        gui.setItem(config.getInt("page_items.previous_page.slot"), previousPage);

        /*
        {// Filler items
            ConfigurationSection section = config.getConfigurationSection("filler_items");
            if (section != null) {
                for (String entry : section.getKeys(false)) {
                    ItemStackBuilder builder = ItemStackBuilder.getItemStack(config.getConfigurationSection(section.getCurrentPath() + "." + entry));
                    builder.withName(config.getString(section.getCurrentPath() + "." + entry + ".display_name").replace("{PLAYER}", player.getName()));
                    builder.withLore(config.getStringList(section.getCurrentPath() + "." + entry + ".lore").stream().map(line -> line
                            .replace("{PLAYER}", player.getName()))
                            .collect(Collectors.toList()));

                    GuiItem guiItem = new GuiItem(builder.build());
                    if (config.contains(section.getCurrentPath() + "." + entry + ".commands")) {
                        guiItem.setAction(event -> {
                            player.closeInventory();
                            for (String command : config.getStringList(section.getCurrentPath() + "." + entry + ".commands")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{PLAYER}", player.getName()));
                            }
                        });
                    }

                    if (config.contains(section.getCurrentPath() + "." + entry + ".slots")) {
                        for (String slot : config.getStringList(section.getCurrentPath() + "." + entry + ".slots")) {
                            gui.setItem(Integer.parseInt(slot), guiItem);
                        }
                    } else if (config.contains(section.getCurrentPath() + "." + entry + ".slot")) {
                        int slot = config.getInt(section.getCurrentPath() + "." + entry + ".slot");
                        if (slot == -1) {
                            gui.getFiller().fill(guiItem);
                        } else {
                            gui.setItem(slot, guiItem);
                        }
                    }
                }
            }
        }

         */


        {// Tournament items
            TournamentManager tournamentManager = plugin.getTournamentManager();
            ConfigurationSection section = config.getConfigurationSection("tournament_items");
            if (section != null) {
                for (String entry : section.getKeys(false)) {
                    Optional<Tournament> optionalTournament = tournamentManager.getTournament(entry);
                    if (optionalTournament.isEmpty()) continue;
                    Tournament tournament = optionalTournament.get();

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

                    /*

                    if (config.contains(section.getCurrentPath() + "." + entry + ".slots")) {
                        for (String slot : config.getStringList(section.getCurrentPath() + "." + entry + ".slots")) {
                            gui.setItem(Integer.parseInt(slot), guiItem);
                        }
                    } else if (config.contains(section.getCurrentPath() + "." + entry + ".slot")) {
                        int slot = config.getInt(section.getCurrentPath() + "." + entry + ".slot");
                        if (slot == -1) {
                            gui.getFiller().fill(guiItem);
                        } else {
                            gui.setItem(slot, guiItem);
                        }
                    }

                     */
                }
            }
        }
        gui.open(player);

    }

}
