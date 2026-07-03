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

public class WebhookListener implements Listener {

    private final XLTournamentsPlugin plugin;

    public WebhookListener(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTournamentEnd(@NotNull TournamentEndEvent event) {
        Tournament tournament = event.getTournament();
        plugin.getLogger().info("Tournament 1st placement: " + tournament.getPlayerFromPosition(1));

        FileConfiguration config = plugin.getConfig();
        String url = config.getString("discord_webhook.url");

        if (url == null || url.isBlank()) {
            plugin.getLogger().warning("Discord webhook URL is not configured.");
            return;
        }

        DiscordWebhook webhook = new DiscordWebhook(url);
        String content = config.getString("discord_webhook.content", "'discord_webhook.content' not found.");

        for (int i = 1; i <= 3; i++) {
            OfflinePlayer player = tournament.getPlayerFromPosition(i);
            String playerName = (player != null && player.getName() != null) ? player.getName() : "Unknown";
            content = content.replace("{" + i + "_PLACE}", playerName);

            Integer playerScore = tournament.getScoreFromPosition(i);
            content = content.replace("{" + i + "_SCORE}", playerScore != null ? String.valueOf(playerScore) : "0");
        }

        content = content.replace("{TOURNAMENT}", tournament.getIdentifier());
        webhook.setContent(content);
        webhook.setAvatarUrl(config.getString("discord_webhook.avatar_url"));

        plugin.getWebhookQueue().enqueue(webhook, tournament.getIdentifier());
    }

}
