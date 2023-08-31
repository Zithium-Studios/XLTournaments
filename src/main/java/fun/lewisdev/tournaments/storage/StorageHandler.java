/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.storage;

import fun.lewisdev.tournaments.XLTournamentsPlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StorageHandler {

    /**
     * Called when a storage handler is enabling
     *
     * @return true if the enable was successful, otherwise false
     */
    boolean onEnable(XLTournamentsPlugin plugin);

    /**
     * Called when a storage handler is disabling
     */
    void onDisable();

    /**
     * Called to create the queue table
     */
    void createQueueTable();

    /**
     * Create a tournament table
     *
     * @param identifier The tournament ID
     */
    void createTournamentTable(String identifier);

    /**
     * Add a participant
     *
     * @param identifier The ID of the tournament
     * @param uuid The UUID of the Player
     */
    void addParticipant(String identifier, UUID uuid);

    /**
     * Update participant data
     *
     * @param identifier The ID of the tournament
     * @param uuid The UUID of the Player
     * @param score The amount to update
     */
    void updateParticipant(String identifier, UUID uuid, int score);

    /**
     * Clear all tournament data
     *
     * @param identifier The ID of the tournament
     */
    void clearParticipants(String identifier);

    /**
     * Clear tournament data for specific player
     *
     * @param identifier The ID of the tournament
     * @param uuid The UUID of the player
     */
    void clearParticipant(String identifier, UUID uuid);

    /**
     * Get the actions in queue for a player
     *
     * @param uuid The UUID of the player
     * @return Array of string actions
     */
    List<String> getPlayerQueueActions(String uuid);

    /**
     * Add an action to the queue
     *
     * @param uuid The UUID of the player
     * @param action The action to queue
     */
    void addActionToQueue(String uuid, String action);

    /**
     * Remove all actions in the queue for a player
     *
     * @param uuid The UUID of the player
     */
    void removeQueueActions(String uuid);

    /**
     * Get the top players ordered by score
     *
     * @param identifier The ID of the tournament
     * @return Map of sorted UUID and score
     */
    Map<UUID, Integer> getTopPlayers(String identifier);

    /**
     * Get the top players being reached a certain score
     *
     * @param identifier The ID of the tournament
     * @param score The score required
     * @return Map of sorted UUID and score
     */
    Map<UUID, Integer> getTopPlayersByScore(String identifier, int score);

    /**
     * Get the score of a player
     *
     * @param identifier The ID of the tournament
     * @param uuid The UUID of the player
     * @return Score of the player
     */
    int getPlayerScore(String identifier, String uuid);

    /**
     * Save player score
     *
     * @param identifier The ID of the tournament
     * @param uuid The UUID of the player
     * @param score The score
     */
    void setPlayerScore(String identifier, String uuid, int score);

}
