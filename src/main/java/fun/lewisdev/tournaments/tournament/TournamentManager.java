/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.tournament;

import fun.lewisdev.tournaments.XLTournamentsPlugin;
import fun.lewisdev.tournaments.config.ConfigHandler;
import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.storage.StorageHandler;
import fun.lewisdev.tournaments.task.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TournamentManager {

    private final XLTournamentsPlugin plugin;
    private Map<String, Tournament> tournaments;
    private boolean listenersRegistered;

    private BukkitTask timerTask;

    public TournamentManager(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
        tournaments = new HashMap<>();
    }

    public void onEnable() {
        tournaments = new HashMap<>();

        File dataFolder = plugin.getDataFolder();
        File directory = new File(dataFolder.getAbsolutePath() + File.separator + "tournaments");

        // Check if tournaments folder exists and create default files if needed
        if (!directory.exists()) {
            directory.mkdir();
            new ConfigHandler(plugin, new File(directory.getAbsolutePath()), "block_break_tournament").saveDefaultConfig();
            new ConfigHandler(plugin, new File(directory.getAbsolutePath()), "item_craft_challenge_tournament").saveDefaultConfig();
            new ConfigHandler(plugin, new File(directory.getAbsolutePath()), "player_kills_tournament").saveDefaultConfig();
        }

        // Get all tournament files
        File[] yamlFiles = new File(directory.getAbsolutePath()).listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (yamlFiles == null) {
            plugin.getLogger().warning("Could not find any tournaments in the tournaments folder.");
            return;
        }

        // Load tournaments
        for (File file : yamlFiles) {
            FileConfiguration config;
            try {
                config = YamlConfiguration.loadConfiguration(file);
            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().severe("There was a YAML error while trying to load " + file.getName() + ". Skipping..");
                continue;
            }
            registerTournament(file.getName().replace(".yml", ""), config);
        }

        if(!listenersRegistered) {
            Stream.of(
                    new Listener() {
                        @EventHandler(priority = EventPriority.MONITOR)
                        public void onPlayerJoin(final PlayerJoinEvent event) {
                            loadPlayerCache(event.getPlayer());
                        }
                    }, new Listener() {
                        @EventHandler(priority = EventPriority.MONITOR)
                        public void onPlayerQuit(final PlayerQuitEvent event) {
                            savePlayerCache(event.getPlayer().getUniqueId());
                        }
                    }).forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
            listenersRegistered = true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayerCache(player);
        }

        timerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new TimerTask(this), 100L, 20L);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> tournaments.values().forEach(Tournament::update));
    }

    public void onDisable(boolean reload) {
        timerTask.cancel();
        Bukkit.getScheduler().cancelTasks(plugin);
        plugin.getLogger().info("Saving player data to database..");

        tournaments.values().forEach(tournament -> {
            BukkitTask task = tournament.getUpdateTask();
            if(task != null) {
                task.cancel();
            }
        });

        StorageHandler handler = plugin.getStorageManager().getStorageHandler();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            for (Tournament tournament : getTournaments(uuid)) {
                handler.setPlayerScore(tournament.getIdentifier(), uuid.toString(), tournament.getScore(uuid));
                tournament.removeParticipant(uuid);
            }
        }

        if (!reload) {
            plugin.getStorageManager().getStorageHandler().onDisable();
        }
    }

    public Set<Tournament> getTournaments(final UUID uuid) {
        return tournaments.values().stream().filter(tournament -> tournament.isParticipant(uuid)).collect(Collectors.toSet());
    }

    public void loadPlayerCache(Player player) {
        UUID uuid = player.getUniqueId();
        StorageHandler handler = plugin.getStorageManager().getStorageHandler();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            List<String> actions = new ArrayList<>(handler.getPlayerQueueActions(uuid.toString()));
            if (!actions.isEmpty()) {
                plugin.getStorageManager().getStorageHandler().removeQueueActions(uuid.toString());
                Bukkit.getScheduler().runTask(plugin, () -> plugin.getActionManager().executeActions(player, actions));
            }

            for (Tournament tournament : getTournaments()) {
                int score = handler.getPlayerScore(tournament.getIdentifier(), uuid.toString());
                if (score > -1) {
                    tournament.addParticipant(uuid, score, false);
                } else if (tournament.isAutomaticParticipation()) {
                    Permission permission = tournament.getParticipationPermission();
                    if (permission != null) {
                        if (player.hasPermission(permission)) {
                            tournament.addParticipant(uuid, 0, true);
                        }
                    } else {
                        tournament.addParticipant(uuid, 0, true);
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> plugin.getActionManager().executeActions(player, tournament.getParticipationActions()));
                }
            }
        });
    }

    public void savePlayerCache(UUID uuid) {
        StorageHandler handler = plugin.getStorageManager().getStorageHandler();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Tournament tournament : getTournaments(uuid)) {
                handler.setPlayerScore(tournament.getIdentifier(), uuid.toString(), tournament.getScore(uuid));
                tournament.removeParticipant(uuid);
            }
        });
    }

    public void registerTournament(String identifier, FileConfiguration config) {
        if (!config.getBoolean("enabled")) return;

        TournamentBuilder builder = new TournamentBuilder(plugin, identifier);

        try {
            builder.loadFromFile(plugin.getObjectiveManager(), config);
        }catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        Tournament tournament = builder.build();
        XLObjective objective = tournament.getObjective();
        Logger logger = plugin.getLogger();

        if (!objective.loadTournament(tournament, config)) {
            logger.severe("The objective (\" + obj + \") in file \" + identifier + \" did not load correctly. Skipping..");
            return;
        }

        objective.addTournament(tournament);
        tournament.updateStatus();
        if(tournament.getStatus() == TournamentStatus.ACTIVE) {
            tournament.start(false);
        }

        plugin.getStorageManager().getStorageHandler().createTournamentTable(identifier);

        tournaments.put(identifier, tournament);
        logger.info("Loaded '" + identifier + "' tournament.");
    }

    public Optional<Tournament> getTournament(String identifier) {
        return tournaments.values().stream().filter(tournament -> tournament.getIdentifier().equalsIgnoreCase(identifier)).findFirst();
    }

    public List<Tournament> getTournaments() {
        return new ArrayList<>(tournaments.values());
    }
}
