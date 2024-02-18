package net.zithium.tournaments.objective.external;

import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;

public class ODailyQuestsObjective extends XLObjective {
    public ODailyQuestsObjective(String identifier) {
        super("ODAILYQUESTS_COMPLETE");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }


    @EventHandler
    public void onQuestComplete(QuestCompletedEvent event) {
        if (event.getPlayer() == null) return;

        for (Tournament tournament : getTournaments()) {
            if (canExecute(tournament, event.getPlayer())) {
                tournament.addScore(event.getPlayer().getUniqueId(), 1);
            }
        }

    }
}
