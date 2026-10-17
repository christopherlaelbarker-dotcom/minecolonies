# CPU-aware pathfinding — MineColonies custom fork

The pathfinding executor defaults to CPU-aware sizing rather than a fixed single worker.
Set `pathfindingthreads = 0` in the MineColonies server config for automatic sizing
(restart required). To override, use 1 through 8; use 1 for troubleshooting.

| JVM-visible logical processors | Automatic workers |
| --- | --- |
| 1–4 (including an i5-4590) | 1 |
| 5–8 | 2 |
| 9–12 | 3 |
| 13+ (including an 18-core CPU) | 4 |

The JVM's processor availability can differ from a CPU's physical core count,
especially inside a container or with a process affinity limit.
This is a conservative heuristic, **not a measured optimal worker count**.
Minecraft simulation still relies heavily on the main server thread, and
parallel pathfinding consumes memory bandwidth and can increase chunk-cache
contention. Increasing workers above the automatic limit may lower performance.
Do not assume high processor utilization means high TPS.

Cancelled navigation tasks are removed from the pending executor queue as
soon as possible. Already-running cancelled tasks still use normal Java
interrupt cancellation. The queue remains bounded to its previous size.

The standalone Java worker tests compile the production executor and
policy against minimal Minecraft stubs, then validate real ThreadPoolExecutor
concurrency, worker bounds, and cancelled-task queue eviction. They are
not live Minecraft benchmarks. CI additionally builds the mod and starts
a headless NeoForge server. For a real 500+ citizen world, profile MSPT,
TPS, average active pathfinding jobs, GC/heap, and contention before
adjusting the thread count.
