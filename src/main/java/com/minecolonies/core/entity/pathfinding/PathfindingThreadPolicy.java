package com.minecolonies.core.entity.pathfinding;

/**
 * Small, allocation-free worker-sizing policy. Deliberately keeps CPU capacity
 * available for Minecraft's main server thread, GC and other mod tasks.
 */
public final class PathfindingThreadPolicy
{
    /** Conservative automatic ceiling; a manual override may request more. */
    public static final int MAX_AUTO_WORKERS = 4;
    public static final int MAX_CONFIGURED_WORKERS = 8;

    private PathfindingThreadPolicy()
    {
    }

    /**
     * @param availableProcessors CPUs exposed to the JVM (respects affinity /
     *                            container limits reported by the JVM).
     * @param requestedWorkers 0 for automatic or a user-selected 1..8.
     * @return number of workers, never less than one.
     */
    public static int chooseWorkers(final int availableProcessors, final int requestedWorkers)
    {
        if (requestedWorkers > 0)
        {
            return Math.max(1, Math.min(MAX_CONFIGURED_WORKERS, requestedWorkers));
        }

        final int processors = Math.max(1, availableProcessors);
        // 1 worker on 1-4 logical CPUs, 2 on 5-8, 3 on 9-12,
        // and up to 4 on 13+. Never use every logical CPU automatically.
        return Math.min(MAX_AUTO_WORKERS, Math.max(1, (processors + 3) / 4));
    }
}
