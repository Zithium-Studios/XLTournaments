package net.zithium.tournaments.discord;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.events.TournamentEndEvent;
import net.zithium.tournaments.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;

public class WebhookListener implements Listener {

    private final XLTournamentsPlugin plugin;

    public WebhookListener(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTournamentEnd(@NotNull TournamentEndEvent event) {

        Tournament tournament = event.getTournament();
        plugin.getLogger().log(Level.INFO, "Tournament 1st placement: " + tournament.getPlayerFromPosition(1));

        FileConfiguration config = plugin.getConfig();
        DiscordWebhook webhook = new DiscordWebhook(config.getString("discord_webhook.url"));

        String content = config.getString("discord_webhook.content", "'discord_webhook.content' not found.");

        for (int i = 1; i <= 3; i++) {
            OfflinePlayer player = tournament.getPlayerFromPosition(i);
            if (player != null) {
                String playerName = player.getName();
                if (playerName != null) {
                    content = content.replace("{" + i + "_PLACE}", playerName);
                }
            } else {
                content = content.replace("{" + i + "_PLACE}", "Unknown");
            }

            Integer playerScore = tournament.getScoreFromPosition(i);
            content = content.replace("{" + i + "_SCORE}", String.valueOf(playerScore));
        }

        content = content.replace("{TOURNAMENT}", tournament.getIdentifier());
        webhook.setContent(content);

        webhook.setAvatarUrl(config.getString("discord_webhook.avatar_url"));
        try {
            webhook.execute();
        } catch (MalformedURLException ex) {
            plugin.getLogger().severe("Unable to send Discord webhook for tournament '" + tournament.getIdentifier() + "': Invalid URL");
        } catch (IOException | NullPointerException ex) {
            plugin.getLogger().severe("There was an error attempting to send the webhook! Error: " + ex);
        }
    }

}
