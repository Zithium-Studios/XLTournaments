package net.zithium.tournaments.action.actions;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.action.Action;
import net.zithium.tournaments.utility.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastMessageAction implements Action {
    @Override
    public String getIdentifier() {
        return "BROADCAST";
    }

    @Override
    public void execute(XLTournamentsPlugin plugin, Player player, String data) {
        Bukkit.broadcastMessage(ColorUtil.color(data));
    }
}
