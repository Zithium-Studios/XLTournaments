/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2026 Zithium Studios. All rights reserved.
 */

package net.zithium.tournaments.calendar;

import net.zithium.tournaments.utility.Timeline;

import java.util.List;

public class TournamentCalendar {

    private final String identifier;
    private final Timeline timeline;
    private final List<String> tournaments;
    private final boolean loop;
    private final boolean randomize;

    private int currentIndex;

    public TournamentCalendar(String identifier, Timeline timeline, List<String> tournaments, boolean loop, boolean randomize, int currentIndex) {
        this.identifier = identifier;
        this.timeline = timeline;
        this.tournaments = tournaments;
        this.loop = loop;
        this.randomize = randomize;
        this.currentIndex = currentIndex;
    }

    /**
     * Gets the identifier of the current tournament in the sequence.
     *
     * @return The tournament identifier at the current index.
     */
    public String getCurrentTournamentId() {
        return tournaments.get(currentIndex);
    }

    /**
     * Advances the calendar to the next tournament in the sequence.
     * Respects loop and randomize settings.
     *
     * @return The identifier of the next tournament, or null if the calendar
     *         has reached the end and loop is disabled.
     */
    public String advance() {
        if (randomize) {
            // Pick a random index, avoiding repeating the same tournament if possible
            if (tournaments.size() > 1) {
                int next;
                do {
                    next = (int) (Math.random() * tournaments.size());
                } while (next == currentIndex);
                currentIndex = next;
            }
            return tournaments.get(currentIndex);
        }

        int nextIndex = currentIndex + 1;

        if (nextIndex >= tournaments.size()) {
            if (!loop) return null; // Calendar has finished
            nextIndex = 0;
        }

        currentIndex = nextIndex;
        return tournaments.get(currentIndex);
    }

    public String getIdentifier() {
        return identifier;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public List<String> getTournaments() {
        return tournaments;
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isRandomize() {
        return randomize;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}