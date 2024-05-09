/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.discord;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.events.TournamentEndEvent;
import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.tournament.TournamentData;
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
    public void onTournamentEnd(TournamentEndEvent event) {

        FileConfiguration config = plugin.getConfig();

        TournamentData tournament = event.getTournamentData();
        DiscordWebhook webhook = new DiscordWebhook(config.getString("discord_webhook.url"));
        //DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();


        webhook.setContent(replacePlaceholders(config.getString("discord_webhook.content", "'discord_webhook.content' not found."), tournament));

        webhook.setAvatarUrl(config.getString("discord_webhook.avatar_url"));
        try {
            webhook.execute();
        } catch (MalformedURLException ex) {
            plugin.getLogger().severe("Unable to send Discord webhook for tournament '" + tournament.getIdentifier() + "': Invalid URL");
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "There was an error attempting to send the webhook!", ex);
        }
    }

    private String replacePlaceholders(@NotNull String text, TournamentData tournament) {
        for (int i = 1; i <= 3; i++) {
            OfflinePlayer player = tournament.getPlayerFromPosition(i);
            String playerName = (player != null) ? player.getName() : "Unknown";

            // Ensure that both text and playerName are not null before replacement
            if (playerName != null) {
                text = text.replace("{" + i + "_PLACE}", playerName);
            }

            // Replace score placeholders
            Integer playerScore = tournament.getScoreFromPosition(i);
            String scorePlaceholder = String.valueOf(playerScore);
            text = text.replace("{" + i + "_SCORE}", scorePlaceholder);
        }

        // Make sure that the text is not null before further replacements
        text = text.replace("{TOURNAMENT}", tournament.getIdentifier());

        return text;
    }


}
