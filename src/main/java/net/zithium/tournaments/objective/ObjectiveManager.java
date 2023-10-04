/*
 * XLTournaments Plugin
 * Copyright (c) 2023 Zithium Studios. All rights reserved.
 */

package net.zithium.tournaments.objective;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.objective.external.*;
import net.zithium.tournaments.objective.internal.*;

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
        registerObjective(new PotionBrewObjective());

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
        registerObjective(new BedWars1058BedBreakObjective(), "BedWars1058"); // FOR REMOVAL
        registerObjective(new BedWars1058KillsObjective(), "BedWars1058"); // FOR REMOVAL
        registerObjective(new BedWars1058FinalKillsObjective(), "BedWars1058"); // FOR REMOVAL
        registerObjective(new BedWars1058LevelUpObjective(), "BedWars1058"); // FOR REMOVAL
        registerObjective(new BedWars1058WinObjective(), "BedWars1058"); // FOR REMOVAL
        registerObjective(new PlaceholderAPIObjective(), "PlaceholderAPI");
        registerObjective(new GoldenCratesObjective(), "GoldenCrates"); // FOR REMOVAL
        registerObjective(new EssentialsBalanceReceiveObjective(), "Essentials");
        registerObjective(new EssentialsBalanceSpendObjective(), "Essentials");
        registerObjective(new ExcellentCratesObjective(), "ExcellentCrates");
        registerObjective(new XPrisonRankupObjective(), "X-Prison");

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
