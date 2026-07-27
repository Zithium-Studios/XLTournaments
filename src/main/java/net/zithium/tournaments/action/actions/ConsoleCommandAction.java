/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.action.actions;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.action.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class ConsoleCommandAction implements Action {

    private final HashMap<UUID, LinkedList<String>> playerCommandHistory = new HashMap<>();
    private static final int MAX_HISTORY_SIZE = 50;

    @Override
    public String getIdentifier() {
        return "CONSOLE";
    }

    @Override
    public void execute(XLTournamentsPlugin plugin, Player player, String data) {
        UUID playerId = player.getUniqueId();

        // Get the player's command history or create a new list if it doesn't exist
        LinkedList<String> commandHistory = playerCommandHistory.getOrDefault(playerId, new LinkedList<>());

        // Check if the command has already been executed
        if (commandHistory.contains(data)) {
            return; // Command already executed, so skip it
        }

        // Execute the command
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), data);

        // Add the command to the history
        commandHistory.add(data);

        // Check if the history exceeds the maximum allowed size
        if (commandHistory.size() > MAX_HISTORY_SIZE) {
            commandHistory.removeFirst(); // Remove the oldest command
        }

        // Update the history in the map
        playerCommandHistory.put(playerId, commandHistory);
    }
}
