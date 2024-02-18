/*
 * XLTournaments Plugin
 * Copyright (c) 2023 Zithium Studios. All rights reserved.
 */

package net.zithium.tournaments;

import net.zithium.tournaments.objective.XLObjective;
import net.zithium.tournaments.tournament.Tournament;

import java.util.List;
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

    /**
     * Gets a list of all tournaments
     *
     * @return A list of all tournaments.
     * @since 3.16.0
     */
    List<Tournament> getTournaments();

}
