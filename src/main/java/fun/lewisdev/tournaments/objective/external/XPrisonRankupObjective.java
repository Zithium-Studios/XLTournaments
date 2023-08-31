package fun.lewisdev.tournaments.objective.external;

import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;

public class XPrisonRankupObjective extends XLObjective {
    public XPrisonRankupObjective(String identifier) {
        super(identifier);
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return false;
    }
}
