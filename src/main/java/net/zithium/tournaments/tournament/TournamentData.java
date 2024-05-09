package net.zithium.tournaments.tournament;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.UUID;

public class TournamentData {

    private final String identifier;
    private final UUID gameUniqueId;
    private final Map<UUID, Integer> sortedParticipants;

    public TournamentData(String identifier, UUID gameUniqueId, Map<UUID, Integer> sortedParticipants) {
        this.identifier = identifier;
        this.gameUniqueId = gameUniqueId;
        this.sortedParticipants = sortedParticipants;
    }

    public String getIdentifier() {
        return identifier;
    }

    public UUID getGameUniqueId() {
        return gameUniqueId;
    }

    public Map<UUID, Integer> getSortedParticipants() {
        return sortedParticipants;
    }

    /**
     * Gets the OfflinePlayer associated with the specified position in the tournament.
     *
     * @param position The position in the tournament (1 for first place, 2 for second, etc.).
     * @return The OfflinePlayer associated with the given position or null if not found.
     */
    public OfflinePlayer getPlayerFromPosition(int position) {
        if (position < 1 || position > sortedParticipants.size()) return null;
        int count = 1;
        for (UUID uuid : sortedParticipants.keySet()) {
            if (count == position) {
                return Bukkit.getOfflinePlayer(uuid);
            }
            count++;
        }
        return null;
    }

    /**
     * Retrieves the score of the participant at the specified position in the sorted list of participants.
     * The position should be within the valid range of 1 to the size of the sorted participant list.
     *
     * @param position The position of the participant in the sorted list.
     * @return The score of the participant at the given position, or 0 if the position is invalid or the score is non-positive.
     */
    public int getScoreFromPosition(int position) {
        // Check if the specified position is out of bounds or non-positive.
        if (position < 1 || position > sortedParticipants.size()) {
            return 0;
        }

        // Get the UUID of the participant at the specified position.
        UUID uuid = (UUID) sortedParticipants.keySet().toArray()[position - 1];

        // Get the score of the participant at the specified position.
        int score = sortedParticipants.get(uuid);

        // Return the score (positive or 0).
        return Math.max(0, score);
    }

}