import java.util.*;

/**
 * Deterministic Minecraft-world mocks. The two methods under test are injected
 * verbatim from the production Tree.java source by inject_forester_canopy.py.
 */
public class ForesterCanopyRegression
{
    @interface NotNull {}

    static final class Block
    {
        final String name;
        Block(final String name) { this.name = name; }
    }

    static final class BlockPos
    {
        final int x, y, z;
        BlockPos(int x, int y, int z) { this.x = x; this.y = y; this.z = z; }
        BlockPos offset(int x, int y, int z) { return new BlockPos(this.x + x, this.y + y, this.z + z); }
        @Override public boolean equals(Object obj)
        {
            return obj instanceof BlockPos p && x == p.x && y == p.y && z == p.z;
        }
        @Override public int hashCode() { return Objects.hash(x, y, z); }
    }

    static final class BlockState
    {
        final Block block;
        final boolean persistent;
        BlockState(String name, boolean persistent)
        {
            this.block = new Block(name);
            this.persistent = persistent;
        }
        Block getBlock() { return block; }
        boolean is(String tag)
        {
            return switch (tag)
            {
                case "leaves" -> block.name.startsWith("leaf-") || block.name.startsWith("dynamic-");
                case "mushroom" -> block.name.equals("mushroom");
                case "wart" -> block.name.equals("wart");
                case "mangrove" -> block.name.equals("mangrove-log");
                default -> false;
            };
        }
        Optional<Boolean> getOptionalValue(String property) { return Optional.of(persistent); }
    }

    static final class LevelReader
    {
        final Map<BlockPos, BlockState> blocks = new HashMap<>();
        BlockState getBlockState(BlockPos pos)
        {
            return blocks.getOrDefault(pos, new BlockState("air", false));
        }
        LevelReader leaf(int y, String name)
        {
            blocks.put(new BlockPos(-1, y, -1), new BlockState(name, false));
            return this;
        }
        LevelReader persistent(int y, String name)
        {
            blocks.put(new BlockPos(-1, y, -1), new BlockState(name, true));
            return this;
        }
    }

    static final class ModTags
    {
        static final String mangroveTree = "mangrove", hugeMushroomBlocks = "mushroom";
    }
    static final class BlockTags
    {
        static final String LEAVES = "leaves", WART_BLOCKS = "wart";
    }
    static final class LeavesBlock
    {
        static final String PERSISTENT = "persistent";
    }
    static final class Compatibility
    {
        static boolean isDynamicBlock(Block block) { return block.name.equals("dynamic-log"); }
        static boolean isDynamicLeaf(Block block) { return block.name.startsWith("dynamic-"); }
    }
    static final class ItemStack
    {
        final String name;
        ItemStack(String name) { this.name = name; }
    }
    static final class ItemStorage
    {
        final ItemStack stack;
        ItemStorage(String name) { stack = new ItemStack(name); }
        ItemStack getItemStack() { return stack; }
    }
    static final class ItemStackUtils
    {
        static boolean compareItemStacksIgnoreStackSize(ItemStack a, ItemStack b)
        {
            return a.name.equals(b.name);
        }
    }
    static final class CompatibilityManager
    {
        final Map<String, ItemStack> lookup = new HashMap<>();
        ItemStack getSaplingForLeaf(Block block) { return lookup.get(block.name); }
    }
    static final class IColonyManager
    {
        static final IColonyManager SINGLETON = new IColonyManager();
        final CompatibilityManager compatibility = new CompatibilityManager();
        static IColonyManager getInstance() { return SINGLETON; }
        CompatibilityManager getCompatibilityManager() { return compatibility; }
    }
    static final class Tree
    {
        static final int NUMBER_OF_LEAVES = 3;

        // PRODUCTION_LEAF_SCAN
        // PRODUCTION_SAPLING_CHECK
    }

    static int assertions = 0;

    static void expect(String scenario, boolean actual, boolean expected)
    {
        if (actual != expected)
        {
            throw new AssertionError(scenario + ": wanted " + expected + " but received " + actual);
        }
        assertions++;
    }

    static boolean passes(LevelReader world, String excluded)
    {
        List<ItemStorage> exclusions = excluded == null ? List.of() : List.of(new ItemStorage(excluded));
        return Tree.hasEnoughLeavesAndIsSupposedToCut(world, new BlockPos(0, 0, 0), exclusions);
    }

    public static void main(String[] args)
    {
        var cache = IColonyManager.getInstance().getCompatibilityManager().lookup;
        cache.put("leaf-oak", new ItemStack("oak_sapling"));
        cache.put("leaf-birch", new ItemStack("birch_sapling"));
        cache.put("leaf-spruce", new ItemStack("spruce_sapling"));
        cache.put("dynamic-birch", new ItemStack("birch_sapling"));
        cache.put("dynamic-oak", new ItemStack("oak_sapling"));

        expect("mixed canopy must reject excluded birch after oak",
            passes(new LevelReader().leaf(0, "leaf-oak").leaf(1, "leaf-birch")
                .leaf(2, "leaf-oak"), "birch_sapling"), false);
        expect("reordering the same mixed canopy must also reject birch",
            passes(new LevelReader().leaf(0, "leaf-birch").leaf(1, "leaf-oak")
                .leaf(2, "leaf-oak"), "birch_sapling"), false);
        expect("late excluded fourth leaf must be checked",
            passes(new LevelReader().leaf(0, "leaf-oak").leaf(1, "leaf-oak")
                .leaf(2, "leaf-oak").leaf(3, "leaf-birch"), "birch_sapling"), false);
        expect("persistent first leaf cannot veto three natural leaves",
            passes(new LevelReader().persistent(0, "leaf-birch").leaf(1, "leaf-oak")
                .leaf(2, "leaf-oak").leaf(3, "leaf-oak"), "birch_sapling"), true);
        expect("persistent late leaves cannot count toward required number",
            passes(new LevelReader().leaf(0, "leaf-oak").persistent(1, "leaf-oak")
                .persistent(2, "leaf-oak"), null), false);
        expect("all persistent leaves do not establish a natural tree",
            passes(new LevelReader().persistent(0, "leaf-oak").persistent(1, "leaf-oak")
                .persistent(2, "leaf-oak"), null), false);
        expect("persistent birch ignored if only oak is natural",
            passes(new LevelReader().leaf(0, "leaf-oak").leaf(1, "leaf-oak")
                .leaf(2, "leaf-oak").persistent(3, "leaf-birch"), "birch_sapling"), true);
        expect("excluded natural birch after persistent leaf vetoes",
            passes(new LevelReader().persistent(0, "leaf-oak").leaf(1, "leaf-birch")
                .leaf(2, "leaf-oak").leaf(3, "leaf-oak"), "birch_sapling"), false);
        expect("ordinary allowed tree",
            passes(new LevelReader().leaf(0, "leaf-oak").leaf(1, "leaf-oak")
                .leaf(2, "leaf-oak"), "birch_sapling"), true);
        expect("ordinary excluded tree",
            passes(new LevelReader().leaf(0, "leaf-birch").leaf(1, "leaf-birch")
                .leaf(2, "leaf-birch"), "birch_sapling"), false);
        expect("two leaves are insufficient",
            passes(new LevelReader().leaf(0, "leaf-oak").leaf(1, "leaf-oak"), null), false);
        expect("zero leaves are insufficient", passes(new LevelReader(), null), false);
        expect("one dynamic oak leaf qualifies through radius behavior",
            passes(new LevelReader().leaf(0, "dynamic-oak"), "birch_sapling"), true);
        expect("later excluded leaf can veto even a qualifying dynamic leaf",
            passes(new LevelReader().leaf(0, "dynamic-oak").leaf(1, "dynamic-birch"),
                "birch_sapling"), false);
        expect("first dynamic leaf being excluded rejects",
            passes(new LevelReader().leaf(0, "dynamic-birch"), "birch_sapling"), false);
        cache.remove("leaf-spruce");
        expect("uncached leaf is permitted by off-thread checker pending synchronous verification",
            passes(new LevelReader().leaf(0, "leaf-spruce").leaf(1, "leaf-spruce")
                .leaf(2, "leaf-spruce"), "spruce_sapling"), true);
        cache.put("leaf-spruce", new ItemStack("spruce_sapling"));
        expect("once cache is populated spruce is excluded",
            passes(new LevelReader().leaf(0, "leaf-spruce").leaf(1, "leaf-spruce")
                .leaf(2, "leaf-spruce"), "spruce_sapling"), false);
        System.out.println("PASS: " + assertions + " Forester canopy tests using production Tree.java leaf methods");
    }
}
