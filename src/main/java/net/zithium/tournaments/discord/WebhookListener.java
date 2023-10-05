/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

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

        plugin.getLogger().log(Level.INFO, "[WEBHOOK] Attempting to execute the webhook.");
        FileConfiguration config = plugin.getConfig();

        Tournament tournament = event.getTournament();
        DiscordWebhook webhook = new DiscordWebhook(config.getString("discord_webhook.url"));
        //DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();


        webhook.setContent(replacePlaceholders(config.getString("discord_webhook.content"), tournament));

        webhook.setAvatarUrl(config.getString("discord_webhook.avatar_url"));
        try {
            webhook.execute();
        } catch (MalformedURLException ex) {
            plugin.getLogger().severe("Unable to send Discord webhook for tournament '" + tournament.getIdentifier() + "': Invalid URL");
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "There was an error attempting to send the webhook!", ex);
        }
    }

    private String replacePlaceholders(String text, Tournament tournament) {
        for (int i = 0; i < 3; i++) {
            OfflinePlayer player = tournament.getPlayerFromPosition(i + 1);
            String playerName = (player != null) ? player.getName() : "No " + getPositionName(i) + " place winner";
            int score = tournament.getScoreFromPosition(i + 1);
            text = text.replace("{" + getPositionName(i).toUpperCase() + "_PLACE}", playerName);
            text = text.replace("{" + getPositionName(i).toUpperCase() + "_PLACE_SCORE}", String.valueOf(score));
        }

        text = text.replace("{TOURNAMENT}", tournament.getIdentifier());

        return text;
    }

    private String getPositionName(int position) {
        return switch (position) {
            case 0 -> "first";
            case 1 -> "second";
            case 2 -> "third";
            default -> "invalid";
        };
    }
}
