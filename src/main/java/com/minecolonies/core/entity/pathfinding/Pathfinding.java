package com.minecolonies.core.entity.pathfinding;

import com.minecolonies.api.util.Log;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.entity.pathfinding.pathjobs.AbstractPathJob;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Static class the handles all the Pathfinding.
 */
public final class Pathfinding
{
    private static final ArrayBlockingQueue<Runnable> jobQueue = new ArrayBlockingQueue<>(10000, false);
    /**
     * Lazily initialized executor. The thread count is intentionally fixed until
     * a server restart to avoid changing active path calculations mid-flight.
     */
    private static volatile ThreadPoolExecutor executor;

    /**
     * Minecolonies specific thread factory.
     */
    public static class MinecoloniesThreadFactory implements ThreadFactory
    {
        /**
         * Ongoing thread IDs.
         */
        public static int id;

        @Override
        public Thread newThread(@NotNull final Runnable runnable)
        {
            final Thread thread = new Thread(runnable, "Minecolonies Pathfinding Worker #" + (id++));
            thread.setDaemon(true);

            thread.setUncaughtExceptionHandler((thread1, throwable) -> Log.getLogger().error("Minecolonies Pathfinding Thread errored! ", throwable));
            return thread;
        }
    }

    /**
     * Creates a new thread pool for pathfinding jobs
     *
     * @return the threadpool executor.
     */
    public static ThreadPoolExecutor getExecutor()
    {
        ThreadPoolExecutor current = executor;
        if (current == null)
        {
            synchronized (Pathfinding.class)
            {
                current = executor;
                if (current == null)
                {
                    final int configuredWorkers = MineColonies.getConfig().getServer().pathfindingThreads.get();
                    final int workers = PathfindingThreadPolicy.chooseWorkers(
                        Runtime.getRuntime().availableProcessors(), configuredWorkers);
                    current = new ThreadPoolExecutor(workers, workers, 10, TimeUnit.SECONDS,
                        jobQueue, new MinecoloniesThreadFactory());
                    executor = current;
                    Log.getLogger().info("MineColonies pathfinding starting with {} workers (available processors: {}, configured: {}).",
                        workers, Runtime.getRuntime().availableProcessors(), configuredWorkers);
                }
            }
        }
        return current;
    }

    /**
     * Discard a cancelled task which has not started yet. Keeping cancelled
     * FutureTasks in the queue can otherwise hold citizens, chunks and path
     * search data alive until the worker reaches them.
     *
     * @param task cancelled FutureTask submitted to the pathfinding executor.
     */
    public static void discardQueued(@NotNull final Runnable task)
    {
        final ThreadPoolExecutor current = executor;
        if (current != null)
        {
            current.remove(task);
        }
    }

    /**
     * Stops all running threads in this thread pool
     */
    public static void shutdown()
    {
        jobQueue.clear();
    }

    private Pathfinding()
    {
        //Hides default constructor.
    }

    /**
     * Add a job to the queue for processing.
     *
     * @param job PathJob
     */
    public static void enqueue(@NotNull final AbstractPathJob job)
    {
        job.getResult().startJob(getExecutor());
    }
}
