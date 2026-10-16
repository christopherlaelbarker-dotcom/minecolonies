package com.minecolonies.core.colony.buildings.workerbuildings;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AbstractCraftingBuildingModule;
import com.minecolonies.core.colony.buildings.modules.MinimumStockModule;
import com.minecolonies.core.colony.buildings.modules.settings.BoolSetting;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.minecolonies.api.util.constant.BuildingConstants.CONST_DEFAULT_MAX_BUILDING_LEVEL;
import static com.minecolonies.api.util.constant.Constants.STACKSIZE;
import static com.minecolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_WOOD_OR_GOLD;

public class BuildingNetherWorker extends AbstractBuilding
{

    /**
     * Settings
     */
    public static final ISettingKey<BoolSetting> CLOSE_PORTAL =
        new SettingKey<>(BoolSetting.class, new ResourceLocation(com.minecolonies.api.util.constant.Constants.MOD_ID, "closeportal"));

    /**
     * Constant name for the Netherworker building
     */
    private static final String NETHER_WORKER = "netherworker";

    /**
     * How many trips we've run this period
     */
    private static final String TAG_CURRENT_TRIPS = "current_trips";

    /**
     * Which day in the period is it?
     */
    private static final String TAG_CURRENT_DAY = "current_day";

    /**
     * Last world time accounted for by the trip-period counter.
     */
    private static final String TAG_LAST_DAY_SNAPSHOT = "last_day_snapshot";

    /**
     * Ticks in a Minecraft day.
     */
    private static final long TICKS_PER_DAY = 24000L;

    /**
     * How many trips we can make per period by default
     */
    private static final int MAX_PER_PERIOD = 1;

    /**
     * How many days are in a period by default
     */
    private static final int PERIOD_DAYS = 3;

    /**
     * Exclusion list id.
     */
    public static final String FOOD_EXCLUSION_LIST = "food";

    /**
     * Which day we're at in the current period
     */
    private int currentPeriodDay = 0;

    /**
     * How many trips we've done in the current period
     */
    private int currentTrips = 0;

    /**
     * ServerTime for the last 'day' snapshot, to track days when doDaylightCycle is not happening.
     */
    private long snapTime = Long.MIN_VALUE;

    public BuildingNetherWorker(@NotNull IColony colony, BlockPos pos)
    {
        super(colony, pos);

        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.axe.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.pickaxe.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.shovel.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.sword.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));

        keepX.put(itemStack -> itemStack.getItem() instanceof FlintAndSteelItem, new Tuple<>(1, true));

        keepX.put(itemStack -> !ItemStackUtils.isEmpty(itemStack)
            && itemStack.getItem() instanceof ArmorItem
            && ((ArmorItem) itemStack.getItem()).getEquipmentSlot() == EquipmentSlot.HEAD, new Tuple<>(1, true));
        keepX.put(itemStack -> !ItemStackUtils.isEmpty(itemStack)
            && itemStack.getItem() instanceof ArmorItem
            && ((ArmorItem) itemStack.getItem()).getEquipmentSlot() == EquipmentSlot.CHEST, new Tuple<>(1, true));
        keepX.put(itemStack -> !ItemStackUtils.isEmpty(itemStack)
            && itemStack.getItem() instanceof ArmorItem
            && ((ArmorItem) itemStack.getItem()).getEquipmentSlot() == EquipmentSlot.LEGS, new Tuple<>(1, true));
        keepX.put(itemStack -> !ItemStackUtils.isEmpty(itemStack)
            && itemStack.getItem() instanceof ArmorItem
            && ((ArmorItem) itemStack.getItem()).getEquipmentSlot() == EquipmentSlot.FEET, new Tuple<>(1, true));
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return NETHER_WORKER;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    /**
     * Should the portal be closed on return?
     */
    public boolean shallClosePortalOnReturn()
    {
        return getSetting(CLOSE_PORTAL).getValue();
    }

    @Override
    public void onWakeUp()
    {
        super.onWakeUp();
        updatePeriodDays(true);
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, final CompoundTag compound)
    {
        super.deserializeNBT(provider, compound);
        if (compound.contains(TAG_CURRENT_TRIPS))
        {
            this.currentTrips = compound.getInt(TAG_CURRENT_TRIPS);
        }

        if (compound.contains(TAG_CURRENT_DAY))
        {
            this.currentPeriodDay = compound.getInt(TAG_CURRENT_DAY);
        }

        // Old saves have no snapshot. Initialize it at the first time check.
        this.snapTime = compound.contains(TAG_LAST_DAY_SNAPSHOT)
            ? compound.getLong(TAG_LAST_DAY_SNAPSHOT)
            : Long.MIN_VALUE;
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider)
    {
        final CompoundTag compound = super.serializeNBT(provider);

        compound.putInt(TAG_CURRENT_TRIPS, this.currentTrips);
        compound.putInt(TAG_CURRENT_DAY, this.currentPeriodDay);
        if (snapTime != Long.MIN_VALUE)
        {
            compound.putLong(TAG_LAST_DAY_SNAPSHOT, snapTime);
        }

        return compound;
    }

    @Override
    public int buildingRequiresCertainAmountOfItem(final ItemStack stack, final List<ItemStorage> localAlreadyKept, final boolean inventory, final JobEntry jobEntry)
    {
        if (stack.isEmpty())
        {
            return 0;
        }

        if (inventory && getFirstModuleOccurance(MinimumStockModule.class).isStocked(stack))
        {
            return stack.getCount();
        }

        // Check for materials needed to go to the Nether: 
        IRecipeStorage rs = getFirstModuleOccurance(BuildingNetherWorker.CraftingModule.class).getFirstRecipe(ItemStack::isEmpty);
        if (rs != null)
        {
            final ItemStorage kept = new ItemStorage(stack);
            boolean containsItem = rs.getInput().contains(kept);
            int keptCount = localAlreadyKept.stream().filter(storage -> storage.equals(kept)).mapToInt(ItemStorage::getAmount).sum();
            if (containsItem && (keptCount < STACKSIZE || !inventory))
            {
                if (localAlreadyKept.contains(kept))
                {
                    kept.setAmount(localAlreadyKept.remove(localAlreadyKept.indexOf(kept)).getAmount());
                }
                localAlreadyKept.add(kept);
                return 0;
            }
        }

        return super.buildingRequiresCertainAmountOfItem(stack, localAlreadyKept, inventory, jobEntry);
    }

    /**
     * Check to see if it's valid to do a trip by checking how many done in this current period
     *
     * @return true if the worker can go to the nether
     */
    public boolean isReadyForTrip()
    {
        updatePeriodDays(false);
        return this.currentTrips < getMaxPerPeriod();
    }

    /**
     * Advance the trip period without counting the same in-game day more than once.
     * Wake-up events also advance the period when daylight cycling is disabled.
     *
     * @param wokeUp whether a new work day was signalled by the colony
     */
    private void updatePeriodDays(final boolean wokeUp)
    {
        final long now = colony.getWorld().getDayTime();

        // A world-time change backwards starts a new baseline; it is not elapsed time.
        if (snapTime == Long.MIN_VALUE || now < snapTime)
        {
            snapTime = now;
            if (wokeUp)
            {
                advancePeriodDays(1);
            }
            markDirty();
            return;
        }

        final long elapsedDays = (now - snapTime) / TICKS_PER_DAY;
        if (elapsedDays > 0)
        {
            // Retain the fractional day so multiple missed days are counted exactly once.
            snapTime += elapsedDays * TICKS_PER_DAY;
            advancePeriodDays(elapsedDays);
        }
        else if (wokeUp)
        {
            // Even with doDaylightCycle disabled, waking marks one new work day.
            snapTime = now;
            advancePeriodDays(1);
        }
    }

    /**
     * Count completed days and replenish the trip allowance at each period boundary.
     */
    private void advancePeriodDays(final long days)
    {
        final long totalDays = (long) currentPeriodDay + days;
        if (totalDays >= getPeriodDays())
        {
            currentTrips = 0;
        }
        currentPeriodDay = (int) (totalDays % getPeriodDays());
        markDirty();
    }

    /**
     * Let the building know we're doing a trip
     */
    public void recordTrip()
    {
        this.currentTrips++;
        markDirty();
    }

    /**
     * Get the tagged location that the worker should walk to in the portal.
     * This should be a 'air block' in the portal that can directly be checked to see if the portal is open
     *
     * @return the block above the tag, null if not available
     */
    public BlockPos getPortalLocation()
    {
        BlockPos portalLocation = getFirstLocationFromTag("portal");
        if (portalLocation != null)
        {
            return portalLocation.above();
        }
        return null;
    }

    /**
     * Get the max per period, potentially modified by research
     *
     * @return
     */
    public static int getMaxPerPeriod()
    {
        return MAX_PER_PERIOD;
    }

    /**
     * Get how many days are in a period, potentially modified by research.
     *
     * @return
     */
    public static int getPeriodDays()
    {
        return PERIOD_DAYS;
    }

    @Override
    public void onPlacement()
    {
        super.onPlacement();
        final Level world = colony.getWorld();
        if (WorldUtil.isNetherType(world))
        {
            final Block block = world.getBlockState(this.getPosition()).getBlock();
            block.destroy(world, getPosition(), world.getBlockState(getPosition()));
        }
    }

    public static class CraftingModule extends AbstractCraftingBuildingModule.Custom
    {
        /**
         * Create a new module.
         *
         * @param jobEntry the entry of the job.
         */
        public CraftingModule(final JobEntry jobEntry)
        {
            super(jobEntry);
        }
    }
}
