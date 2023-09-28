/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.command;

import net.zithium.library.utils.Color;
import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.tournament.Tournament;
import net.zithium.tournaments.tournament.TournamentStatus;
import net.zithium.tournaments.config.Messages;
import net.zithium.tournaments.utility.TextUtil;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Command("tournament")
@SuppressWarnings("unused")
public class TournamentsCommand extends CommandBase {

    private final XLTournamentsPlugin plugin;

    public TournamentsCommand(XLTournamentsPlugin plugin) {
        this.plugin = plugin;
        List<String> aliases = plugin.getConfig().getStringList("command_aliases");
        if (!aliases.isEmpty()) {
            this.setAliases(plugin.getConfig().getStringList("command_aliases"));
        }
    }

    @Default
    public void defaultCommand(final Player player) {
        plugin.getMenuManager().getTournamentGui().openInventory(player);
    }

    @SubCommand("help")
    @Permission({"tournaments.admin", "tournaments.command.help"})
    @WrongUsage("&c/tournament help")
    public void helpSubCommand(final CommandSender sender) {
        for (String s : plugin.getMessagesFile().getConfig().getStringList("general.help")) {
            sender.sendMessage(TextUtil.color(s).replace("{VERSION}", plugin.getDescription().getVersion()));
        }
    }

    @SubCommand("reload")
    @Permission({"tournaments.admin", "tournaments.command.reload"})
    @WrongUsage("&c/tournament reload")
    public void reloadSubCommand(final CommandSender sender) {
        plugin.reload();
        Messages.RELOAD.send(sender);
    }

    @SubCommand("about")
    @WrongUsage("&c/tournament about")
    public void aboutSubCommand(final CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(Color.stringColor("&b&lXLTournaments"));
        sender.sendMessage(Color.stringColor("&bVersion: &fv" + plugin.getDescription().getVersion()));
        sender.sendMessage(Color.stringColor("&bAuthor: &fZithium Studios"));

        if (!TextUtil.isValidDownload()) {
            sender.sendMessage(Color.stringColor("&4Registered to: &cFailed to find licensed owner to this plugin. Contact developer to report possible leak (itzsave)."));
        } else if (TextUtil.isMCMarket()) {
            sender.sendMessage(Color.stringColor("&4Registered to: &chttps://builtbybit.com/members/%%__USER__%%/"));
        } else {
            sender.sendMessage(Color.stringColor("&4Registered to: &chttps://www.spigotmc.org/members/%%__USER__%%/"));
        }
        sender.sendMessage("");
    }

    @SubCommand("update")
    @Permission({"tournaments.admin", "tournaments.command.update"})
    @WrongUsage("&c/tournament update")
    public void updateSubCommand(final CommandSender sender) {
        for (Tournament tournament : plugin.getTournamentManager().getTournaments()) {
            if (tournament.getStatus() == TournamentStatus.ACTIVE) {
                tournament.update();
            }
        }
        Messages.FORCE_UPDATED_TOURNAMENTS.send(sender);
    }

    @SubCommand("info")
    @Permission({"tournaments.admin", "tournaments.command.info"})
    @WrongUsage("&c/tournament info <tournament>")
    @Completion("#tournaments")
    public void infoSubCommand(final CommandSender sender, final String input) {
        Optional<Tournament> optionalTournament = plugin.getTournamentManager().getTournament(input);
        if (optionalTournament.isEmpty()) {
            sender.sendMessage(Color.stringColor("&cCould not find tournament with that ID"));
            return;
        }

        Tournament tournament = optionalTournament.get();

        sender.sendMessage("");
        sender.sendMessage(Color.stringColor("&b&lTournament Information"));
        sender.sendMessage("");
        sender.sendMessage(Color.stringColor("&bIdentifier: &f" + tournament.getIdentifier()));
        sender.sendMessage(Color.stringColor("&bStatus: &f" + tournament.getStatus().toString()));
        sender.sendMessage(Color.stringColor("&bParticipants Amount: &f" + tournament.getParticipants().size()));
        sender.sendMessage(Color.stringColor("&bObjective: &f" + tournament.getObjective().getIdentifier()));
        sender.sendMessage(Color.stringColor("&bTimeline: &f" + tournament.getTimeline()));
        sender.sendMessage(Color.stringColor("&bTimezone: &f" + tournament.getZoneId().getId()));
        sender.sendMessage(TextUtil.color("&bStart Date: &f" + DateTimeFormatter.ofPattern("yyyy/MM/dd - hh:mm:ss").format(tournament.getStartDate())));
        sender.sendMessage(TextUtil.color("&bEnd Date: &f" + DateTimeFormatter.ofPattern("yyyy/MM/dd - hh:mm:ss").format(tournament.getEndDate())));
        sender.sendMessage(TextUtil.color("&bDisabled Worlds: &f" + tournament.getDisabledWorlds()));
        sender.sendMessage(TextUtil.color("&bDisabled Gamemodes: &f" + tournament.getDisabledGamemodes()));
        sender.sendMessage(TextUtil.color("&bAutomatic Participation: &f" + tournament.isAutomaticParticipation()));
        sender.sendMessage(TextUtil.color("&bParticipation Cost: &f" + tournament.getParticipationCost()));
        org.bukkit.permissions.Permission permission = tournament.getParticipationPermission();
        sender.sendMessage(TextUtil.color("&bParticipation Permission: &f" + (permission == null ? "N/A" : permission.getName())));
        sender.sendMessage(TextUtil.color("&bLeaderboard Refresh: &f" + tournament.getLeaderboardRefresh()));
        Set<String> metadata = tournament.getMeta().keySet();
        sender.sendMessage(TextUtil.color("&bMetadata: &f" + (metadata.isEmpty() ? "N/A" : metadata)));
        sender.sendMessage("");
    }

    @SubCommand("clear")
    @Permission({"tournaments.admin", "tournaments.command.clear"})
    @WrongUsage("&c/tournament clear <tournament>")
    @Completion("#tournaments")
    public void clearSubCommand(final CommandSender sender, final String input) {
        Optional<Tournament> optionalTournament = plugin.getTournamentManager().getTournament(input);
        if (optionalTournament.isEmpty()) {
            sender.sendMessage(TextUtil.color("&cCould not find tournament with that ID"));
            return;
        }

        Tournament tournament = optionalTournament.get();
        tournament.clearParticipants();
        Messages.TOURNAMENT_CLEARED.send(sender, "{TOURNAMENT}", tournament.getIdentifier());
    }

    @SubCommand("clearplayer")
    @Permission({"tournaments.admin", "tournaments.command.clearplayer"})
    @WrongUsage("&c/tournament clearplayer <player> <tournament>")
    @Completion({"#players", "#tournaments"})
    public void clearPlayerSubCommand(final CommandSender sender, final Player target, final String input) {

        if (target == null) {
            sender.sendMessage(TextUtil.color("&cPlayer is invalid or offline."));
            return;
        }

        Optional<Tournament> optionalTournament = plugin.getTournamentManager().getTournament(input);
        if (optionalTournament.isEmpty()) {
            sender.sendMessage(TextUtil.color("&cCould not find tournament with that ID"));
            return;
        }

        Tournament tournament = optionalTournament.get();
        tournament.clearParticipant(target.getUniqueId());
        Messages.TOURNAMENT_CLEARED_PLAYER.send(sender, "{TOURNAMENT}", tournament.getIdentifier(), "{PLAYER}", target.getName());
    }

    @SubCommand("list")
    @Permission({"tournaments.admin", "tournaments.command.list"})
    @WrongUsage("&c/tournament list")
    public void listSubCommand(final CommandSender sender) {
        Messages.LIST_TOURNAMENTS.send(sender, "{LIST}", plugin.getTournamentManager().getTournaments().stream().map(Tournament::getIdentifier).collect(Collectors.joining(", ")));
    }

    @SubCommand("end")
    @Permission({"tournaments.admin", "tournaments.command.end"})
    @WrongUsage("&c/tournament end <tournament>")
    @Completion("#tournaments")
    public void endSubCommand(final CommandSender sender, final String input) {
        Optional<Tournament> optionalTournament = plugin.getTournamentManager().getTournament(input);

        if (optionalTournament.isPresent()) {
            Tournament tournament = optionalTournament.get();

            if (tournament.getStatus() == TournamentStatus.ENDED) {
                Messages.ALREADY_STOPPED.send(sender);
            } else {
                tournament.stop();
                tournament.setStatus(TournamentStatus.ENDED);
                Messages.STOPPED_TOURNAMENT.send(sender, "{TOURNAMENT}", tournament.getIdentifier());
            }
        } else {
            sender.sendMessage(TextUtil.color("&cCould not find a tournament with that ID."));
        }
    }
}


