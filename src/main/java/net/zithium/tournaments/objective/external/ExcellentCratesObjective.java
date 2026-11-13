package net.zithium.tournaments.objective.external;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import su.nightexpress.excellentcrates.api.reward.event.RewardGrantedEvent;

public class ExcellentCratesObjective extends XLObjective {
    public ExcellentCratesObjective() {
        super("EXCELLENTCRATES_OPEN");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return true;
    }

    @EventHandler
    public void onCrateOpen(RewardGrantedEvent event) {
        Player player = event.getPlayer();
        for(Tournament tournament : getTournaments()) {
            if(canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }

}
