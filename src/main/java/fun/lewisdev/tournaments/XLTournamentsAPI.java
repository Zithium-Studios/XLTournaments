/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package fun.lewisdev.tournaments;

import fun.lewisdev.tournaments.objective.XLObjective;
import fun.lewisdev.tournaments.tournament.Tournament;

import java.util.Optional;

public interface XLTournamentsAPI {

    /**
     * Register an objective
     *
     * @param objective The objective to register
     */
    void registerObjective(XLObjective objective);

    /**
     * Register an objective with a required dependency
     *
     * @param objective The objective to register
     * @param requiredPlugin The required plugin in order for the objective to function
     */
    void registerObjective(XLObjective objective, String requiredPlugin);

    /**
     * Get a tournament
     *
     * @param identifier The ID of the tournament (file name)
     *
     * @return The Tournament object
     */
    Optional<Tournament> getTournament(String identifier);

}
