/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.discord;

import fun.lewisdev.tournaments.XLTournamentsPlugin;
import fun.lewisdev.tournaments.events.TournamentEndEvent;
import fun.lewisdev.tournaments.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.net.MalformedURLException;

public class WebhookListener implements Listener {

    private final XLTournamentsPlugin plugin;

    public WebhookListener(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /*@EventHandler(priority = EventPriority.HIGH)
    public void onTournamentEnd(TournamentEndEvent event) {
        Tournament tournament = event.getTournament();
        DiscordWebhook webhook = new DiscordWebhook(plugin.getConfig().getString("discord_webhook.url"));
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        
        webhook.setContent("tournament " + tournament.getIdentifier() + " has ended.");
        try {
            webhook.execute();
        } catch (MalformedURLException ex) {
            plugin.getLogger().severe("Unable to send Discord webhook for tournament '" + tournament.getIdentifier() + "': Invalid URL");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }*/

}
