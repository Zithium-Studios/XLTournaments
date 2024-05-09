package net.zithium.tournaments.tournament;

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
}
