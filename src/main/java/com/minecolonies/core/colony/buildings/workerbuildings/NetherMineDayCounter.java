package com.minecolonies.core.colony.buildings.workerbuildings;

/**
 * Pure, deterministic period tracking for the Nether Mine. No world or mod state
 * is needed so that regressions can be exercised by a regular Java test runner.
 */
final class NetherMineDayCounter
{
    private static final long TICKS_PER_DAY = 24000L;

    private NetherMineDayCounter()
    {
    }

    record State(int currentPeriodDay, int currentTrips, long lastTrackedDay)
    {
    }

    /**
     * Advance the trip counter at most once per world-calendar day.
     * A wake-up counts as a new day when daylight cycling is disabled.
     */
    static State update(
        final State old,
        final long dayTime,
        final boolean wokeUp,
        final boolean daylightCycle,
        final int periodDays)
    {
        if (periodDays <= 0)
        {
            throw new IllegalArgumentException("A Nether Mine trip period must have at least one day");
        }

        // Old saves can contain counters inflated by repeated readiness checks.
        final int validDay = Math.max(0, old.currentPeriodDay());
        final State state = new State(validDay % periodDays,
            validDay >= periodDays ? 0 : old.currentTrips(), old.lastTrackedDay());

        final long today = Math.floorDiv(dayTime, TICKS_PER_DAY);
        if (state.lastTrackedDay() == Long.MIN_VALUE)
        {
            // Older saves did not store this marker. First contact sets a baseline.
            final State initialized = new State(state.currentPeriodDay(), state.currentTrips(), today);
            return wokeUp ? addDays(initialized, 1, periodDays) : initialized;
        }

        if (today > state.lastTrackedDay())
        {
            // Count missed days once, including nights skipped by sleeping.
            final State counted = addDays(state, today - state.lastTrackedDay(), periodDays);
            return new State(counted.currentPeriodDay(), counted.currentTrips(), today);
        }
        if (today < state.lastTrackedDay())
        {
            // /time set or a restored world must not generate negative periods.
            return new State(state.currentPeriodDay(), state.currentTrips(), today);
        }
        if (wokeUp && !daylightCycle)
        {
            return addDays(state, 1, periodDays);
        }
        return state;
    }

    private static State addDays(final State state, final long days, final int periodDays)
    {
        final long elapsed = (long) state.currentPeriodDay() + days;
        final int remainingTrips = elapsed >= periodDays ? 0 : state.currentTrips();
        return new State((int) (elapsed % periodDays), remainingTrips, state.lastTrackedDay());
    }
}
