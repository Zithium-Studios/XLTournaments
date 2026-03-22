package net.zithium.tournaments.action;

import me.clip.placeholderapi.PlaceholderAPI;
import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.action.actions.*;
import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.utility.TextUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionManager {
    private final XLTournamentsPlugin plugin;
    private Map<String, Action> actions;

    public ActionManager(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
    }



    public void onEnable() {
        actions = new HashMap<>();

        registerAction(
                new MessageAction(),
                new BroadcastMessageAction(),
                new CommandAction(),
                new ConsoleCommandAction(),
                new SoundAction()
        );
    }

    public void registerAction(Action... actions) {
        Arrays.asList(actions).forEach(action -> this.actions.put(action.getIdentifier(), action));
    }

    public void executeActions(Player player, List<String> items) {
        executeActions(player, items, null);
    }

    public void executeActions(Player player, List<String> items, Tournament tournament) {
        items.forEach(item -> {

            String actionName = StringUtils.substringBetween(item, "[", "]");
            if(actionName != null) {
                actionName = actionName.toUpperCase();
                Action action = actionName.isEmpty() ? null : actions.get(actionName);

                if (action != null) {
                    item = item.contains(" ") ? item.split(" ", 2)[1] : "";
                    if (player != null) {
                        item = item.replace("{PLAYER}", player.getName());
                        item = PlaceholderAPI.setPlaceholders(player, item);

                        if(tournament != null) {
                            item = TextUtil.setPlaceholders(item, player.getUniqueId(), tournament);
                        }
                    }

                    action.execute(plugin, player, item);
                }
            }
        });
    }
}
