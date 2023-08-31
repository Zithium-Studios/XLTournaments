/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments.objective;

import fun.lewisdev.tournaments.XLTournamentsPlugin;
import fun.lewisdev.tournaments.objective.external.*;
import fun.lewisdev.tournaments.objective.internal.*;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ObjectiveManager {

    private final XLTournamentsPlugin plugin;
    private final Map<String, XLObjective> objectives;

    public ObjectiveManager(XLTournamentsPlugin plugin) {
        objectives = new HashMap<>();
        this.plugin = plugin;
    }

    public void onEnable() {
        // Register Internal Objectives
        registerObjective(new BreakObjective(plugin));
        registerObjective(new PlaceObjective());
        registerObjective(new CraftTournament());
        registerObjective(new PlayerKillsObjective());
        registerObjective(new MobKillsObjective());
        registerObjective(new PlayerFishObjective());
        registerObjective(new PlaytimeObjective());

        // Register External Objectives
        registerObjective(new ChatReactionObjective(), "ChatReaction");
        registerObjective(new EZPrestigeObjective(), "EZPrestige");
        registerObjective(new CrazyCratesObjective(), "CrazyCrates");
        registerObjective(new NuVotifierObjective(), "Votifier");
        registerObjective(new CrackShotDamageObjective(), "CrackShot");
        registerObjective(new CrazyEnvoyObjective(), "CrazyEnvoy");
        registerObjective(new ClueScrollsClueCompleteObjective(), "ClueScrolls");
        registerObjective(new ClueScrollsScrollCompleteObjective(), "ClueScrolls");
        registerObjective(new DuelsObjective(), "Duels");
        registerObjective(new MythicMobsObjective(), "MythicMobs");
        registerObjective(new RandomEventsWinObjective(), "RandomEvents");
        registerObjective(new BedWars1058BedBreakObjective(), "BedWars1058");
        registerObjective(new BedWars1058KillsObjective(), "BedWars1058");
        registerObjective(new BedWars1058FinalKillsObjective(), "BedWars1058");
        registerObjective(new BedWars1058LevelUpObjective(), "BedWars1058");
        registerObjective(new BedWars1058WinObjective(), "BedWars1058");
        registerObjective(new PlaceholderAPIObjective(), "PlaceholderAPI");
        registerObjective(new GoldenCratesObjective(), "GoldenCrates");
        registerObjective(new EssentialsBalanceReceiveObjective(), "Essentials");
        registerObjective(new EssentialsBalanceSpendObjective(), "Essentials");

        plugin.getLogger().info("Loaded " + objectives.size() + " tournament objectives (" + String.join(", ", objectives.keySet()) + ").");
    }

    public void registerObjective(XLObjective objective) {
        registerObjective(objective, null);
    }

    public void registerObjective(XLObjective objective, String requiredPlugin) {
        if(requiredPlugin != null) {
            if (plugin.getServer().getPluginManager().isPluginEnabled(requiredPlugin)) {
                objectives.put(objective.getIdentifier(), objective);
                plugin.getLogger().info("Registered external objective " + objective.getIdentifier() + " using " + requiredPlugin + " plugin.");
            }
        }else{
            objectives.put(objective.getIdentifier(), objective);
        }

        if(!objective.getClass().getPackage().getName().startsWith("fun.lewisdev.tournaments.objective")) {
            plugin.getLogger().info("Registered external objective " + objective.getIdentifier() + ".");
        }

    }

    public XLObjective getObjective(String identifier) {
        return objectives.get(identifier);
    }

    private boolean hasEmptyConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 0) return true;
        }
        return false;
    }

}
