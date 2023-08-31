/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.utility;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public enum Messages {

    PREFIX("general.prefix"),
    RELOAD("general.reload"),
    NO_PERMISSION("general.no_permission"),

    FORCE_UPDATED_TOURNAMENTS("tournament.force_updated_tournaments"),
    ALREADY_PARTICIPATING("tournament.already_participating"),
    NOT_ENOUGH_FUNDS("tournament.not_enough_funds"),
    TOURNAMENT_NO_PERMISSION("tournament.tournament_no_permission"),
    TOURNAMENT_WAITING("tournament.waiting"),
    TOURNAMENT_ENDED("tournament.ended"),
    TOURNAMENT_CLEARED("tournament.cleared"),
    TOURNAMENT_CLEARED_PLAYER("tournament.cleared_player"),
    LIST_TOURNAMENTS("tournament.list");

    private static FileConfiguration config;
    private final String path;

    Messages(String path) {
        this.path = path;
    }

    public static void setConfiguration(FileConfiguration c) {
        config = c;
    }

    public void send(CommandSender receiver, Object... replacements) {
        Object value = config.get(this.path);

        String message;
        if (value == null) {
            message = "XLTournaments: message not found (" + this.path + ")";
        }else {
            message = value instanceof List ? TextUtil.fromList((List<?>) value) : value.toString();
        }

        if (!message.isEmpty()) {
            receiver.sendMessage(TextUtil.color(replace(message, replacements)));
        }
    }

    private String replace(String message, Object... replacements) {
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 >= replacements.length) break;
            message = message.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }

        String prefix = config.getString(PREFIX.getPath());
        return message.replace("{PREFIX}", prefix != null && !prefix.isEmpty() ? prefix : "");
    }

    public String getPath() {
        return this.path;
    }

    private String getConfiguration() {
        return "%%__NONCE__%%";
    }

}
