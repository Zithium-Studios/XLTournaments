/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.utility;

import net.zithium.tournaments.tournament.Tournament;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    public static boolean isMCMarket() {
        String hash = "%__FILEHASH__%";
        return !(hash.charAt(0) + hash + hash.charAt(0)).equals("%%__FILEHASH__%%");
    }

    public static String color(String message) {
        Component component = MiniMessage.miniMessage().deserialize(replaceLegacy("<!i>" + message));
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static String replaceLegacy(String legacyText) {
        return legacyText
                .replaceAll("&1", "<dark_blue>")
                .replaceAll("&2", "<dark_green>")
                .replaceAll("&3", "<dark_aqua>")
                .replaceAll("&4", "<dark_red>")
                .replaceAll("&5", "<dark_purple>")
                .replaceAll("&6", "<gold>")
                .replaceAll("&7", "<gray>")
                .replaceAll("&8", "<dark_gray>")
                .replaceAll("&9", "<blue>")
                .replaceAll("&a", "<green>")
                .replaceAll("&b", "<aqua>")
                .replaceAll("&c", "<red>")
                .replaceAll("&d", "<light_purple>")
                .replaceAll("&e", "<yellow>")
                .replaceAll("&f", "<white>")
                .replaceAll("&l", "<bold>")
                .replaceAll("&k", "<obfuscated>")
                .replaceAll("&m", "<strikethrough>")
                .replaceAll("&n", "<u>")
                .replaceAll("&r", "<reset>")
                .replaceAll("&o", "<italic>");
    }


    public static boolean isValidDownload() {
        String hash = "%__USER__%";
        return !(hash.charAt(0) + hash + hash.charAt(0)).equals("%%__USER__%%");
    }

    public static String getNumberFormatted(int value) {
        return NUMBER_FORMAT.format(value);
    }

    public static String fromList(List<?> list) {
        if (list == null || list.isEmpty()) return null;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0 && list.get(i).toString().equals(""))
                builder.append("&r\n ");
            else
                builder.append(list.get(i).toString());
            builder.append(i + 1 != list.size() ? "\n" : "");
        }
        return builder.toString();
    }

    public static List<String> setPlaceholders(List<String> text, UUID uuid, Tournament tournament) {
        List<String> list = new ArrayList<>();
        for (String line : text) {
            list.add(setPlaceholders(line, uuid, tournament));
        }
        return list;
    }

    public static String setPlaceholders(String text, UUID uuid, Tournament tournament) {
        text = text.replace("{START_DAY}", tournament.getStartDay())
                .replace("{END_DAY}", tournament.getEndDay())
                .replace("{START_MONTH}", tournament.getStartMonth())
                .replace("{START_MONTH_NUMBER}", tournament.getStartMonthNumber())
                .replace("{END_MONTH_NUMBER}", tournament.getEndMonthNumber())
                .replace("{END_MONTH}", tournament.getEndMonth())
                .replace("{PLAYER_POSITION}", String.valueOf(tournament.getPosition(uuid)))
                .replace("{PLAYER_POSITION_FORMATTED}", TextUtil.getNumberFormatted(tournament.getPosition(uuid)))
                .replace("{PLAYER_SCORE}", String.valueOf(tournament.getScore(uuid)))
                .replace("{PLAYER_SCORE_FORMATTED}", TextUtil.getNumberFormatted(tournament.getScore(uuid)))
                .replace("{PLAYER_SCORE_TIME_FORMATTED}", TimeUtil.formatTime(tournament.getScore(uuid)))
                .replace("{TIME_REMAINING}", tournament.getTimeRemaining());

        try {
            final String LEADER_PATTERN = "\\{LEADER_NAME_(\\w+)}";
            final Pattern pattern = Pattern.compile(LEADER_PATTERN);
            final Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                OfflinePlayer player = tournament.getPlayerFromPosition(Integer.parseInt(matcher.group(1)));
                text = matcher.replaceAll(player != null ? player.getName() : "N/A");
            }
        } catch (Exception ignored) {
        }

        try {
            final String LEADER_PATTERN = "\\{LEADER_SCORE_(\\w+)}";
            final Pattern pattern = Pattern.compile(LEADER_PATTERN);
            final Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                text = matcher.replaceAll(String.valueOf(tournament.getScoreFromPosition(Integer.parseInt(matcher.group(1)))));
            }
        } catch (Exception ignored) {
        }

        try {
            final String LEADER_PATTERN = "\\{LEADER_SCORE_FORMATTED_(\\w+)}";
            final Pattern pattern = Pattern.compile(LEADER_PATTERN);
            final Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                text = matcher.replaceAll(TextUtil.getNumberFormatted(tournament.getScoreFromPosition(Integer.parseInt(matcher.group(1)))));
            }
        } catch (Exception ignored) {
        }

        try {
            final String LEADER_PATTERN = "\\{LEADER_SCORE_TIME_FORMATTED_(\\w+)}";
            final Pattern pattern = Pattern.compile(LEADER_PATTERN);
            final Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                text = matcher.replaceAll(TimeUtil.formatTime(tournament.getScoreFromPosition(Integer.parseInt(matcher.group(1)))));
            }
        } catch (Exception ignored) {
        }

        return text;
    }

}
