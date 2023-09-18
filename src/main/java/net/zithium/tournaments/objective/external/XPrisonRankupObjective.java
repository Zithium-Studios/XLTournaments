package net.zithium.tournaments.objective.external;

import dev.drawethree.xprison.ranks.api.events.PlayerRankUpEvent;
import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class XPrisonRankupObjective extends XLObjective {
    public XPrisonRankupObjective() {
        super("XPRISON_RANKUP");
    }

    @Override
    public boolean loadTournament(Tournament tournament, FileConfiguration config) {
        return false;
    }



    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRankup(PlayerRankUpEvent event) {
        Player player = (Player) event.getPlayer();
        for(Tournament tournament : getTournaments()) {
            if(canExecute(tournament, player)) {
                tournament.addScore(player.getUniqueId(), 1);
            }
        }
    }
}
