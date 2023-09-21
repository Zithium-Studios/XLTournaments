/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.hook.hooks;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.tournament.TournamentManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private static final Pattern LEADER_NAME_PATTERN = Pattern.compile("(\\w+)_LEADER_NAME_(\\w+)");
    private static final Pattern LEADER_SCORE_PATTERN = Pattern.compile("(\\w+)_LEADER_SCORE_(\\w+)");
    private static final Pattern PLAYER_SCORE_PATTERN = Pattern.compile("(\\w+)_SCORE");
    private static final Pattern PLAYER_POSITION_PATTERN = Pattern.compile("(\\w+)_POSITION");
    private static final Pattern TIME_REMAINING_PATTERN = Pattern.compile("(\\w+)_TIME_REMAINING");
    private static final Pattern START_MONTH_PATTERN = Pattern.compile("(\\w+)_START_MONTH");
    private static final Pattern START_MONTH_NUMBER_PATTERN = Pattern.compile("(\\w+)_START_MONTH_NUMBER");
    private static final Pattern END_MONTH_PATTERN = Pattern.compile("(\\w+)_END_MONTH");
    private static final Pattern END_MONTH_NUMBER_PATTERN = Pattern.compile("(\\w+)_END_MONTH_NUMBER");
    private static final Pattern START_DAY_PATTERN = Pattern.compile("(\\w+)_START_DAY");
    private static final Pattern END_DAY_PATTERN = Pattern.compile("(\\w+)_END_DAY");

    private final XLTournamentsPlugin plugin;
    private final TournamentManager tournamentManager;

    public PlaceholderAPIHook(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
        this.tournamentManager = plugin.getTournamentManager();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "xltournaments";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) return "";

        final UUID uuid = player.getUniqueId();

        try {
            final Matcher matcher = START_DAY_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                if (optionalTournament.isEmpty()) return "Invalid Tournament ID";
                return optionalTournament.get().getStartDay();
            }
        } catch (Exception ignored) {
        }

        try {
            final Matcher matcher = END_DAY_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                if (optionalTournament.isEmpty()) return "Invalid Tournament ID";
                return optionalTournament.get().getEndDay();
            }
        } catch (Exception ignored) {
        }

        try {
            final Matcher matcher = START_MONTH_NUMBER_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                if (optionalTournament.isEmpty()) return "Invalid Tournament ID";
                return optionalTournament.get().getStartMonthNumber();
            }
        } catch (Exception ignored) {
        }

        try {
            final Matcher matcher = START_MONTH_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                if (optionalTournament.isEmpty()) return "Invalid Tournament ID";
                return optionalTournament.get().getStartMonth();
            }
        } catch (Exception ignored) {
        }

        try {
            final Matcher matcher = END_MONTH_NUMBER_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                if (optionalTournament.isEmpty()) return "Invalid Tournament ID";
                return optionalTournament.get().getEndMonthNumber();
            }
        } catch (Exception ignored) {
        }

        try {
            final Matcher matcher = END_MONTH_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                if (optionalTournament.isEmpty()) return "Invalid Tournament ID";
                return optionalTournament.get().getEndMonth();
            }
        } catch (Exception ignored) {
        }

        try {
            final Matcher matcher = TIME_REMAINING_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                if (optionalTournament.isEmpty()) return "Invalid Tournament ID";
                return optionalTournament.get().getTimeRemaining();
            }
        } catch (Exception ignored) {
        }

        try {
            final Matcher matcher = LEADER_NAME_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                if (optionalTournament.isEmpty()) return "Invalid Tournament ID";

                final Tournament tournament = optionalTournament.get();
                final OfflinePlayer offlinePlayer = tournament.getPlayerFromPosition(Integer.parseInt(matcher.group(2)));
                if (offlinePlayer == null) {
                    return "N/A";
                } else {
                    return offlinePlayer.getName();
                }
            }
        } catch (Exception ignored) {
        }

        try {
            final Matcher matcher = LEADER_SCORE_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                return optionalTournament.map(tournament -> String.valueOf(tournament.getScoreFromPosition(Integer.parseInt(matcher.group(2))))).orElse("Invalid Tournament ID");
            }
        } catch (Exception ignored) {
        }

        try {
            final Matcher matcher = PLAYER_SCORE_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                if (optionalTournament.isEmpty()) return "Invalid Tournament ID";

                final Tournament tournament = optionalTournament.get();
                if (tournament.isParticipant(uuid)) {
                    return String.valueOf(tournament.getScore(uuid));
                } else {
                    return "N/A";
                }
            }
        } catch (Exception ignored) {
        }

        try {
            final Matcher matcher = PLAYER_POSITION_PATTERN.matcher(identifier.toUpperCase());
            if (matcher.find()) {
                final Optional<Tournament> optionalTournament = tournamentManager.getTournament(matcher.group(1));
                if (optionalTournament.isEmpty()) return "Invalid Tournament ID";

                final Tournament tournament = optionalTournament.get();
                if (tournament.isParticipant(uuid)) {
                    return String.valueOf(tournament.getPosition(uuid));
                } else {
                    return "N/A";
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}