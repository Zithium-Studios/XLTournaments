/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments;

import dev.triumphteam.gui.guis.BaseGui;
import net.zithium.tournaments.action.ActionManager;
import net.zithium.tournaments.command.TournamentsCommand;
import net.zithium.tournaments.config.ConfigHandler;
import net.zithium.tournaments.discord.WebhookListener;
import net.zithium.tournaments.hook.HookManager;
import net.zithium.tournaments.hook.hooks.PlaceholderAPIHook;
import net.zithium.tournaments.menu.MenuManager;
import net.zithium.tournaments.objective.ObjectiveManager;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.storage.StorageManager;
import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.tournament.TournamentManager;
import net.zithium.tournaments.config.Messages;
import me.mattstudios.mf.base.CommandManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class XLTournamentsPlugin extends JavaPlugin implements XLTournamentsAPI {

    private ActionManager actionManager;
    private StorageManager storageManager;
    private TournamentManager tournamentManager;
    private ObjectiveManager objectiveManager;
    private MenuManager menuManager;
    private HookManager hookManager;
    private static boolean debugMode;

    private ConfigHandler messagesFile, menuFile;

    @Override
    public void onEnable() {
        getLogger().info("");
        getLogger().info("\\/|      XLTournaments v" + getDescription().getVersion());
        getLogger().info("/\\|_     Author: " + getDescription().getAuthors());
        getLogger().info("         Copyright (c) Zithium Studios 2023. All Rights Reserved.");
        getLogger().info("");
        getLogger().info("Loading plugin..");

        loadMetrics();


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

        getLogger().info("");

        if (getConfig().getBoolean("discord_webhook.enable", false)) {
            new WebhookListener(this);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            objectiveManager.onEnable();
            tournamentManager.onEnable();

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new PlaceholderAPIHook(this).register();
            }
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().stream().filter(player -> player.getOpenInventory().getTopInventory().getHolder() instanceof BaseGui).forEach(HumanEntity::closeInventory);

        if (tournamentManager != null) {
            tournamentManager.onDisable(false);
        }
    }

    public void reload() {
        reloadConfig();
        messagesFile.reload();
        menuFile.reload();
        Messages.setConfiguration(messagesFile.getConfig());
        menuManager = new MenuManager(this);

        tournamentManager.onDisable(true);
        tournamentManager.onEnable();

    }

    private void loadMetrics() {
        if (getConfig().getBoolean("enable_metrics")) {
            getLogger().log(Level.INFO, "Loading bstats metrics.");
            int pluginId = 19726;
            @SuppressWarnings("unused")
            Metrics metrics = new Metrics(this, pluginId);
        }
        getLogger().log(Level.INFO, "Metrics are disabled.");
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

    public static boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode() {
        debugMode = getConfig().getBoolean("debug", false);
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

    @Override
    public List<Tournament> getTournaments(){
        return tournamentManager.getTournaments();
    }

}
