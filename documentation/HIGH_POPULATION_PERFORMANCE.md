# High-population colony performance (Minecraft 1.21.1 / NeoForge)

This fork is designed to support **opt-in populations above 500**, up to 2,000.
The stock default stays **250** citizens. High population is **not a guarantee**
of acceptable TPS, RAM usage, or AI/pathfinding performance.

## Opting in

1. Back up the world.
2. In the MineColonies server configuration, set `maxcitizenpercolony` to
   the desired limit (e.g. 500, 750, 1000; hard ceiling 2000).
3. Complete the normal final colony-population research. Its final cap has
   been raised to 2000 so the server-configured ceiling can take effect.
4. Provide enough housing, loaded chunks, and server memory; raise the population
   gradually and watch tick timings before adding more citizens.

Changing the configuration ceiling does **not** automatically spawn citizens,
raise world simulation distance, or make the 500+ case safe on all hardware.

## Allocation changes

- Citizen and animal tick managers reuse their mutation-safe snapshots;
  direct element iteration when filling the buffers avoids the hidden temporary
  array allocation in `ArrayList.addAll(Collection)`.
- Buffers are cleared in `finally` and never hold references to retired entities
  across ticks.
- Ordinary citizen chat-option checks avoid creating an empty removal list.
- Random citizen selection avoids allocating a population-sized Object[].
- Initial-spawn checks and happiness aggregation avoid redundant population copies.
- Empty colony-connection maps do not generate temporary traversal snapshots.

The public `getCitizens()` and `getAnimals()` APIs still return independent
snapshots to preserve caller behavior. Existing building inventory capability
invalidation is **not throttled**: doing so without correct rack-change events
could lead to stale/invalid inventories.

## Testing and interpreting the results

`.github/workflows/colony-500-allocation-tests.yml` injects selected actual
production methods into deterministic Java-world mocks and tests populations
up to 2,000, including a removal during the tick. It also reports synthetic
JVM allocation counts for one citizen-snapshot method at population 500.

A synthetic allocation probe is **not** a 500-citizen Minecraft benchmark.
The headless NeoForge server smoke workflow only checks startup.

To make a defensible live-world performance claim, compare the same world save,
tick simulation radius, citizens, work assignments and loaded chunks with and
without this commit. Record:

- Live heap after garbage collection, heap high-water mark, and GC pauses
- Java Flight Recorder allocation profiles (top allocation sites)
- Spark profiler MSPT distribution and tick flamegraph
- TPS, pathfinding queue lengths, and citizen counts/active chunks
- Durations after initial loading/warm-up (avoid first-launch bias)

Test 250, 500, 750, 1000+ populations separately. Do not infer RAM use from
JAR disk size or from allocated bytes of one code path. The `server` should target <50ms per tick when 20 TPS is important.
