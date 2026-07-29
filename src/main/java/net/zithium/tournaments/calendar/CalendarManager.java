/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2026 Zithium Studios. All rights reserved.
 */

package net.zithium.tournaments.calendar;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.config.ConfigHandler;
import net.zithium.tournaments.storage.StorageHandler;
import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.tournament.TournamentManager;
import net.zithium.tournaments.utility.Timeline;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarManager {

    private final XLTournamentsPlugin plugin;
    private final Map<String, TournamentCalendar> calendars = new HashMap<>();

    public CalendarManager(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
    }

    public void onEnable(TournamentManager tournamentManager) {
        calendars.clear();

        File calendarDir = new File(plugin.getDataFolder(), "calendars");
        if (!calendarDir.exists()) {
            calendarDir.mkdir();
            new ConfigHandler(plugin, new File(calendarDir.getAbsolutePath()), "example_calendar").saveDefaultConfig();
            plugin.getLogger().info("Created calendars folder with example calendar.");
            return;
        }

        File[] yamlFiles = calendarDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (yamlFiles == null || yamlFiles.length == 0) {
            plugin.getLogger().info("No calendar files found in the calendars folder.");
            return;
        }

        StorageHandler storageHandler = plugin.getStorageManager().getStorageHandler();
        storageHandler.createCalendarTable();

        Logger logger = plugin.getLogger();

        for (File file : yamlFiles) {
            FileConfiguration config;
            try {
                config = YamlConfiguration.loadConfiguration(file);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "There was a YAML error while trying to load calendar " + file.getName() + ". Skipping..", e);
                continue;
            }

            String id = file.getName().replace(".yml", "");

            String timelineStr = config.getString("timeline", "WEEKLY").toUpperCase();
            Timeline timeline;
            try {
                timeline = Timeline.valueOf(timelineStr);
            } catch (IllegalArgumentException e) {
                logger.severe("Invalid timeline '" + timelineStr + "' in calendar " + id + ". Skipping..");
                continue;
            }

            if (timeline == Timeline.SPECIFIC || timeline == Timeline.CALENDAR || timeline == Timeline.NONE) {
                logger.severe("Calendar '" + id + "' has unsupported timeline '" + timeline + "'. Use HOURLY, DAILY, WEEKLY, or MONTHLY. Skipping..");
                continue;
            }

            List<String> tournaments = config.getStringList("tournaments");
            if (tournaments.isEmpty()) {
                logger.warning("Calendar '" + id + "' has no tournaments listed. Skipping..");
                continue;
            }

            boolean loop = config.getBoolean("loop", true);
            boolean randomize = config.getBoolean("randomize", false);

            int savedIndex = storageHandler.getCalendarIndex(id);
            int startIndex = (savedIndex >= 0 && savedIndex < tournaments.size()) ? savedIndex : 0;

            TournamentCalendar calendar = new TournamentCalendar(id, timeline, tournaments, loop, randomize, startIndex);
            calendars.put(id, calendar);
            logger.info("Loaded calendar '" + id + "' with " + tournaments.size() + " tournaments (starting at index " + startIndex + ").");

            activateCalendarTournament(calendar, tournamentManager, false);
        }
    }

    public void onDisable() {
        calendars.clear();
    }

    /**
     * Advances the given calendar to its next tournament, persists the new index,
     * and starts the next tournament. Called from TournamentUpdateTask when a
     * calendar-managed tournament ends.
     *
     * @param calendar          The calendar to advance.
     * @param tournamentManager The active TournamentManager instance.
     */
    public void advance(TournamentCalendar calendar, TournamentManager tournamentManager) {
        String nextId = calendar.advance();

        if (nextId == null) {
            plugin.getLogger().info("Calendar '" + calendar.getIdentifier() + "' has finished its sequence and loop is disabled.");
            return;
        }

        // Persist the new index
        plugin.getStorageManager().getStorageHandler().setCalendarIndex(calendar.getIdentifier(), calendar.getCurrentIndex());

        activateCalendarTournament(calendar, tournamentManager, true);
    }

    /**
     * Starts the current tournament in a calendar. Checks that the tournament
     * exists and is not already active (guarding against two calendars pointing
     * to the same tournament simultaneously).
     *
     * @param calendar          The calendar whose current tournament should be started.
     * @param tournamentManager The active TournamentManager instance.
     * @param clearParticipants Whether to clear participants on start.
     */
    private void activateCalendarTournament(TournamentCalendar calendar, TournamentManager tournamentManager, boolean clearParticipants) {
        String tournamentId = calendar.getCurrentTournamentId();

        Optional<Tournament> optional = tournamentManager.getTournament(tournamentId);
        if (optional.isEmpty()) {
            plugin.getLogger().severe("Calendar '" + calendar.getIdentifier() + "' references unknown tournament '" + tournamentId + "'. Skipping.");
            return;
        }

        Tournament tournament = optional.get();

        // Guard: prevent the same tournament being activated by two calendars at once
        if (tournament.getStatus() == net.zithium.tournaments.tournament.TournamentStatus.ACTIVE) {
            plugin.getLogger().warning("Tournament '" + tournamentId + "' is already active. Calendar '" + calendar.getIdentifier() + "' will not start it again.");
            return;
        }

        // Apply the calendar's timeline so start/end times are computed correctly
        tournament.setTimeline(calendar.getTimeline());
        tournament.updateStatus();
        tournament.start(clearParticipants);

        plugin.getLogger().info("Calendar '" + calendar.getIdentifier() + "' started tournament '" + tournamentId + "'.");
    }

    /**
     * Finds the calendar that currently owns a given tournament identifier,
     * i.e. the calendar whose current slot is that tournament.
     *
     * @param tournamentId The tournament identifier to look up.
     * @return The owning TournamentCalendar, or null if not calendar-managed.
     */
    public TournamentCalendar getCalendarForTournament(String tournamentId) {
        for (TournamentCalendar calendar : calendars.values()) {
            if (calendar.getCurrentTournamentId().equalsIgnoreCase(tournamentId)) {
                return calendar;
            }
        }
        return null;
    }

    public Map<String, TournamentCalendar> getCalendars() {
        return calendars;
    }

    public TournamentCalendar getCalendar(String identifier) {
        return calendars.values().stream()
                .filter(calendar -> calendar.getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);
    }
}