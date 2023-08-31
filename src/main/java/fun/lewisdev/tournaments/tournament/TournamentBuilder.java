/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.tournament;

import fun.lewisdev.tournaments.XLTournamentsPlugin;
import fun.lewisdev.tournaments.exception.ObjectiveNotFoundException;
import fun.lewisdev.tournaments.exception.TournamentLoadException;
import fun.lewisdev.tournaments.objective.ObjectiveManager;
import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.utility.Timeline;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TournamentBuilder {

    private final Tournament tournament;

    public TournamentBuilder(XLTournamentsPlugin plugin, String identifier) {
        tournament = new Tournament(plugin, identifier);
    }

    public TournamentBuilder loadFromFile(ObjectiveManager objectiveManager, FileConfiguration config) {
        // Check for challenge type
        if (config.getBoolean("challenge.enabled")) {
            withChallengeGoal(config.getInt("challenge.goal"));
        }

        // Disabled worlds
        withDisabledWorlds(config.getStringList("disabled_worlds"));

        if (config.contains("disabled_gamemodes")) {
            List<GameMode> disabledGamemodes = new ArrayList<>();
            for (String gamemode : config.getStringList("disabled_gamemodes")) {
                try {
                    disabledGamemodes.add(GameMode.valueOf(gamemode));
                } catch (Exception ignored) {
                }
            }
            withDisabledGamemodes(disabledGamemodes);
        }

        // Set timeline
        Timeline timeline;
        try {
            timeline = Timeline.valueOf(config.getString("timeline"));
        } catch (Exception e) {
            throw new TournamentLoadException("The timeline (" + config.getString("timeline") + ") set in file " + tournament.getIdentifier() + ".yml does not exist. Skipping..");
        }
        withTimeline(timeline);

        ZoneId zoneId;
        if (config.contains("timezone_options") && !config.getBoolean("timezone_options.automatically_detect")) {
            zoneId = ZoneId.of(config.getString("timezone_options.force_timezone"));
        } else {
            zoneId = ZoneId.systemDefault();
        }

        withZoneId(zoneId);

        // Check for specific timeline
        if (timeline == Timeline.SPECIFIC) {
            ZonedDateTime start = ZonedDateTime.of(LocalDateTime.parse(config.getString("start_date"), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")), zoneId);
            withStartDate(start);

            ZonedDateTime end = ZonedDateTime.of(LocalDateTime.parse(config.getString("end_date"), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")), zoneId);
            withEndDate(end);
        }

        // Get the tournament objective
        String obj = config.getString("objective");
        if (obj.contains(";")) obj = obj.split(";")[0];
        XLObjective objective = objectiveManager.getObjective(obj);

        if (objective == null) {
            throw new ObjectiveNotFoundException("The objective (" + obj + ") set in file " + tournament.getIdentifier() + ".yml does not exist. Skipping..");
        }

        withObjective(objective);

        // Leaderboard update time
        withUpdateTime(config.getInt("leaderboard_refresh", 60));

        // Participation settings
        if (config.getBoolean("participation.automatic")) {
            withAutomaticParticipation();
        } else {
            withParticipationCost(config.getDouble("participation.cost"));
        }

        if (config.contains("participation.permission")) {
            withParticipationPermission(config.getString("participation.permission"));
        }

        withParticipationActions(config.getStringList("participation.join_actions"));

        Map<Integer, List<String>> rewards = new HashMap<>();
        for (String place : config.getConfigurationSection("rewards").getKeys(false)) {
            rewards.put(Integer.parseInt(place), config.getStringList("rewards." + place));
        }
        withRewards(rewards);

        withStartActions(config.getStringList("start_actions"));
        withEndActions(config.getStringList("end_actions"));

        return this;
    }

    public Tournament build() {
        return tournament;
    }

    public TournamentBuilder withObjective(XLObjective objective) {
        tournament.setObjective(objective);
        return this;
    }

    public TournamentBuilder withChallengeGoal(int amount) {
        tournament.setChallenge(true);
        tournament.setChallengeGoal(amount);
        return this;
    }

    public TournamentBuilder withTimeline(Timeline timeline) {
        tournament.setTimeline(timeline);
        return this;
    }

    public TournamentBuilder withStartDate(ZonedDateTime date) {
        tournament.setStartDate(date);
        return this;
    }

    public TournamentBuilder withEndDate(ZonedDateTime date) {
        tournament.setEndDate(date);
        return this;
    }

    public TournamentBuilder withUpdateTime(int time) {
        tournament.setUpdateTime(time);
        return this;
    }

    public TournamentBuilder withAutomaticParticipation() {
        tournament.setAutomaticParticipation(true);
        return this;
    }

    public TournamentBuilder withParticipationCost(double cost) {
        tournament.setParticipationCost(cost);
        return this;
    }

    public TournamentBuilder withParticipationPermission(String permission) {
        tournament.setParticipationPermission(new Permission(permission));
        return this;
    }

    public TournamentBuilder withParticipationActions(List<String> actions) {
        tournament.setParticipationActions(actions);
        return this;
    }

    public TournamentBuilder withDisabledWorlds(List<String> worlds) {
        tournament.setDisabledWorlds(worlds);
        return this;
    }

    public TournamentBuilder withDisabledGamemodes(List<GameMode> gamemodes) {
        tournament.setDisabledGamemodes(gamemodes);
        return this;
    }

    public TournamentBuilder withRewards(Map<Integer, List<String>> rewards) {
        tournament.setRewards(rewards);
        return this;
    }

    public TournamentBuilder withStartActions(List<String> actions) {
        tournament.setStartActions(actions);
        return this;
    }

    public TournamentBuilder withEndActions(List<String> actions) {
        tournament.setEndActions(actions);
        return this;
    }

    public TournamentBuilder withZoneId(ZoneId zoneId) {
        tournament.setZoneId(zoneId);
        return this;
    }

}
