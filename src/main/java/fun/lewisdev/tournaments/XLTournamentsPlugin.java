/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments;

import dev.triumphteam.gui.guis.BaseGui;
import fun.lewisdev.tournaments.action.ActionManager;
import fun.lewisdev.tournaments.command.TournamentsCommand;
import fun.lewisdev.tournaments.config.ConfigHandler;
import fun.lewisdev.tournaments.hook.HookManager;
import fun.lewisdev.tournaments.hook.hooks.PlaceholderAPIHook;
import fun.lewisdev.tournaments.menu.MenuManager;
import fun.lewisdev.tournaments.objective.ObjectiveManager;
import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.storage.StorageManager;
import fun.lewisdev.tournaments.tournament.Tournament;
import fun.lewisdev.tournaments.tournament.TournamentManager;
import fun.lewisdev.tournaments.utility.Messages;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.stream.Collectors;

public final class XLTournamentsPlugin extends JavaPlugin implements XLTournamentsAPI {

    private ActionManager actionManager;
    private StorageManager storageManager;
    private TournamentManager tournamentManager;
    private ObjectiveManager objectiveManager;
    private MenuManager menuManager;
    private HookManager hookManager;

    private ConfigHandler messagesFile, menuFile;

    @Override
    public void onEnable() {
        getLogger().info("");
        getLogger().info("\\/|      XLTournaments v" + getDescription().getVersion());
        getLogger().info("/\\|_     Author: " + getDescription().getAuthors());
        getLogger().info("         Copyright (c) Zithium Studios 2023. All Rights Reserved.");
        getLogger().info("");
        getLogger().info("Loading plugin..");

        saveDefaultConfig();
        (messagesFile = new ConfigHandler(this, "messages")).saveDefaultConfig();
        (menuFile = new ConfigHandler(this, "menu")).saveDefaultConfig();
        Messages.setConfiguration(messagesFile.getConfig());

        (hookManager = new HookManager(this)).onEnable();
        (storageManager = new StorageManager(this)).onEnable();
        (actionManager = new ActionManager(this)).onEnable();

        objectiveManager = new ObjectiveManager(this);
        tournamentManager = new TournamentManager(this);
        menuManager = new MenuManager(this);

        // Load Command Manager
        CommandManager commandManager = new CommandManager(this, true);
        commandManager.getMessageHandler().register("cmd.no.permission", Messages.NO_PERMISSION::send);
        commandManager.getCompletionHandler().register("#tournaments", input -> tournamentManager.getTournaments().stream().map(Tournament::getIdentifier).collect(Collectors.toList()));

        // Register commands
        commandManager.register(new TournamentsCommand(this));

        //todo Discord Webhook
        //new WebhookListener(this);

        getLogger().info("");

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            objectiveManager.onEnable();
            tournamentManager.onEnable();

            if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new PlaceholderAPIHook(this).register();
            }
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().stream().filter(player -> player.getOpenInventory().getTopInventory().getHolder() instanceof BaseGui).forEach(HumanEntity::closeInventory);

        if(tournamentManager != null) {
            tournamentManager.onDisable(false);
        }
    }

    public void reload() {
        reloadConfig();
        messagesFile.reload();
        menuFile.reload();
        Messages.setConfiguration(messagesFile.getConfig());

        tournamentManager.onDisable(true);
        tournamentManager.onEnable();
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public TournamentManager getTournamentManager() {
        return tournamentManager;
    }

    public ObjectiveManager getObjectiveManager() {
        return objectiveManager;
    }

    public ConfigHandler getMenuFile() {
        return menuFile;
    }

    public ConfigHandler getMessagesFile() {
        return messagesFile;
    }

    @Override
    public void registerObjective(XLObjective objective) {
        objectiveManager.registerObjective(objective);
    }

    @Override
    public void registerObjective(XLObjective objective, String requiredPlugin) {
        objectiveManager.registerObjective(objective, requiredPlugin);
    }

    @Override
    public Optional<Tournament> getTournament(String identifier) {
        return tournamentManager.getTournament(identifier);
    }
}
