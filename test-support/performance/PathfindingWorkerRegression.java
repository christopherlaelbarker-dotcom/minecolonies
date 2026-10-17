import com.minecolonies.core.MineColonies;
import com.minecolonies.core.entity.pathfinding.Pathfinding;
import com.minecolonies.core.entity.pathfinding.PathfindingThreadPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class PathfindingWorkerRegression {
    private static int assertions;

    private static void require(final boolean condition, final String message) {
        if (!condition) throw new AssertionError(message);
        assertions++;
    }

    private static void testPolicy() {
        final int[] cpus = {1, 2, 4, 5, 8, 9, 12, 13, 18, 24, 64};
        final int[] expected = {1, 1, 1, 2, 2, 3, 3, 4, 4, 4, 4};
        for (int i = 0; i < cpus.length; i++) {
            require(PathfindingThreadPolicy.chooseWorkers(cpus[i], 0) == expected[i],
                "auto count for " + cpus[i] + " processors");
        }
        require(PathfindingThreadPolicy.chooseWorkers(4, 1) == 1, "manual one");
        require(PathfindingThreadPolicy.chooseWorkers(18, 3) == 3, "manual three");
        require(PathfindingThreadPolicy.chooseWorkers(4, 8) == 8, "manual cap eight");
        require(PathfindingThreadPolicy.chooseWorkers(18, 200) == 8, "manual is bounded");
        require(PathfindingThreadPolicy.chooseWorkers(0, 0) == 1, "zero CPUs fallback");
        require(PathfindingThreadPolicy.chooseWorkers(-12, 0) == 1, "negative CPUs fallback");
    }

    private static void testRealExecutor() throws Exception {
        final ExecutorService callers = Executors.newFixedThreadPool(12);
        final CountDownLatch ready = new CountDownLatch(12);
        final CountDownLatch go = new CountDownLatch(1);
        final List<Future<ThreadPoolExecutor>> executorResults = new ArrayList<>();
        for (int n = 0; n < 12; n++) {
            executorResults.add(callers.submit(() -> {
                ready.countDown();
                go.await();
                return Pathfinding.getExecutor();
            }));
        }
        require(ready.await(5, TimeUnit.SECONDS), "all callers ready");
        go.countDown();

        final ThreadPoolExecutor actual = executorResults.get(0).get(5, TimeUnit.SECONDS);
        for (final Future<ThreadPoolExecutor> future : executorResults) {
            require(future.get(5, TimeUnit.SECONDS) == actual, "one singleton executor");
        }
        callers.shutdownNow();

        final int configured = MineColonies.getConfig().getServer().pathfindingThreads.get();
        final int desired = PathfindingThreadPolicy.chooseWorkers(
            Runtime.getRuntime().availableProcessors(), configured);
        require(actual.getCorePoolSize() == desired, "core threads match requested sizing");
        require(actual.getMaximumPoolSize() == desired, "max threads match requested sizing");

        final CountDownLatch executing = new CountDownLatch(desired);
        final CountDownLatch unblock = new CountDownLatch(1);
        try {
            for (int n = 0; n < desired; n++) {
                actual.submit(() -> {
                    executing.countDown();
                    try {
                        unblock.await(8, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            require(executing.await(6, TimeUnit.SECONDS), "all permitted workers process tasks");
            require(actual.getActiveCount() <= desired, "worker count bounded");

            final Future<?> stale = actual.submit(() -> {
                throw new AssertionError("cancelled navigation job should never execute");
            });
            require(actual.getQueue().contains(stale), "stale navigation job is queued");
            require(stale.cancel(true), "stale navigation is cancelled");
            Pathfinding.discardQueued((Runnable) stale);
            require(!actual.getQueue().contains(stale), "cancelled queued job evicted immediately");
            require(actual.getQueue().isEmpty(), "queue remains clear after cancellation");
        } finally {
            unblock.countDown();
            actual.shutdownNow();
            require(actual.awaitTermination(8, TimeUnit.SECONDS), "workers terminate");
        }
    }

    public static void main(String[] args) throws Exception {
        testPolicy();
        testRealExecutor();
        System.out.println("PASS: " + assertions + " source-backed worker-policy, concurrency and cancellation assertions"
            + ", JVM-visible CPUs=" + Runtime.getRuntime().availableProcessors()
            + ", override=" + MineColonies.getConfig().getServer().pathfindingThreads.get());
    }
}
