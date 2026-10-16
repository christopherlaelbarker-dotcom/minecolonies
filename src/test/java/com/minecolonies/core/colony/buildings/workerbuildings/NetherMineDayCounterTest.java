package com.minecolonies.core.colony.buildings.workerbuildings;

/**
 * Dependency-free regression suite for the production Nether Mine day counter.
 * Invoked by .github/workflows/nether-mine-day-tests.yml using javac/java 21.
 */
public final class NetherMineDayCounterTest
{
    private static final int PERIOD = 3;
    private static int scenarios;

    private NetherMineDayCounterTest()
    {
    }

    public static void main(final String[] args)
    {
        repeatsDoNotCountAgain();
        wakingAfterTimedAdvanceDoesNotDoubleCount();
        periodResetsAfterExactlyThreeDays();
        skippedDaysCountOnce();
        frozenDaylightWakeUpsAdvance();
        rollingTimeBackDoesNotEarnNewTrip();
        worldTimeZeroIsValid();
        legacySaveInitializesSafely();
        legacyCorruptedDayCountIsNormalized();
        savedDayMarkerPreventsReloadDuplicate();
        oneDayCrossingBoundaryResetsTripLimit();
        noWakeUpMeansNoAdvanceWhenFrozen();
        durationUsesFullWorldDayRatherThanClockTime();
        invalidPeriodIsRejected();
        System.out.println("PASS: " + scenarios + " Nether Mine day-counter regression scenarios");
    }

    private static NetherMineDayCounter.State check(
        final NetherMineDayCounter.State old, final long ticks, final boolean wake, final boolean cycle)
    {
        return NetherMineDayCounter.update(old, ticks, wake, cycle, PERIOD);
    }

    private static void equalsState(final NetherMineDayCounter.State state,
                                    final int day, final int trips, final long marker, final String label)
    {
        final var expected = new NetherMineDayCounter.State(day, trips, marker);
        if (!expected.equals(state))
        {
            throw new AssertionError(label + ": expected " + expected + ", found " + state);
        }
    }

    private static void repeatsDoNotCountAgain()
    {
        var state = new NetherMineDayCounter.State(0, 1, 0);
        for (int n = 0; n < 100; n++)
        {
            state = check(state, 24000, false, true);
        }
        equalsState(state, 1, 1, 1, "repeated polling");
        scenarios++;
    }

    private static void wakingAfterTimedAdvanceDoesNotDoubleCount()
    {
        final var counted = check(new NetherMineDayCounter.State(0, 1, 0), 24000, false, true);
        equalsState(check(counted, 24000, true, true), 1, 1, 1, "wake after calendar day");
        scenarios++;
    }

    private static void periodResetsAfterExactlyThreeDays()
    {
        var state = new NetherMineDayCounter.State(0, 1, 0);
        state = check(state, 24000, true, true);
        state = check(state, 48000, true, true);
        equalsState(state, 2, 1, 2, "two days no reset");
        state = check(state, 72000, true, true);
        equalsState(state, 0, 0, 3, "third day reset");
        scenarios++;
    }

    private static void skippedDaysCountOnce()
    {
        final var state = check(new NetherMineDayCounter.State(1, 1, 2), 120000, false, true);
        equalsState(state, 1, 0, 5, "three missed days");
        equalsState(check(state, 120000, false, true), 1, 0, 5, "no repeat after skip");
        scenarios++;
    }

    private static void frozenDaylightWakeUpsAdvance()
    {
        var state = new NetherMineDayCounter.State(0, 1, 17);
        for (int n = 0; n < 3; n++)
        {
            state = check(state, 17 * 24000L, true, false);
        }
        equalsState(state, 0, 0, 17, "frozen day wake");
        scenarios++;
    }

    private static void rollingTimeBackDoesNotEarnNewTrip()
    {
        final var state = check(new NetherMineDayCounter.State(2, 1, 4), 5000, false, true);
        equalsState(state, 2, 1, 0, "backward time");
        scenarios++;
    }

    private static void worldTimeZeroIsValid()
    {
        var state = check(new NetherMineDayCounter.State(2, 1, Long.MIN_VALUE), 0, false, true);
        equalsState(state, 2, 1, 0, "time zero init");
        state = check(state, 24000, false, true);
        equalsState(state, 0, 0, 1, "time zero rollover");
        scenarios++;
    }

    private static void legacySaveInitializesSafely()
    {
        final var state = check(new NetherMineDayCounter.State(0, 1, Long.MIN_VALUE), 10000, true, true);
        equalsState(state, 1, 1, 0, "legacy saved wake-up");
        scenarios++;
    }

    private static void legacyCorruptedDayCountIsNormalized()
    {
        final var state = check(new NetherMineDayCounter.State(999, 1, Long.MIN_VALUE), 0, false, true);
        equalsState(state, 0, 0, 0, "old inflated counter");
        scenarios++;
    }

    private static void savedDayMarkerPreventsReloadDuplicate()
    {
        final var state = check(new NetherMineDayCounter.State(1, 1, 350), 350 * 24000L + 100, false, true);
        equalsState(state, 1, 1, 350, "save reload");
        scenarios++;
    }

    private static void oneDayCrossingBoundaryResetsTripLimit()
    {
        final var state = check(new NetherMineDayCounter.State(2, 1, 2), 72000, false, true);
        equalsState(state, 0, 0, 3, "one-day rollover");
        scenarios++;
    }

    private static void noWakeUpMeansNoAdvanceWhenFrozen()
    {
        final var state = check(new NetherMineDayCounter.State(2, 1, 2), 48000, false, false);
        equalsState(state, 2, 1, 2, "frozen no wake");
        scenarios++;
    }

    private static void durationUsesFullWorldDayRatherThanClockTime()
    {
        final var state = check(new NetherMineDayCounter.State(0, 1, 12), 13 * 24000L + 1000, true, true);
        equalsState(state, 1, 1, 13, "world time absolute day index");
        scenarios++;
    }

    private static void invalidPeriodIsRejected()
    {
        try
        {
            NetherMineDayCounter.update(new NetherMineDayCounter.State(0, 0, 0), 0, false, true, 0);
            throw new AssertionError("Expected rejection of zero-length period");
        }
        catch (IllegalArgumentException expected)
        {
            scenarios++;
        }
    }
}
