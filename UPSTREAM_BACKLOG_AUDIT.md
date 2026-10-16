# MineColonies upstream open issue and PR inventory

Snapshot date: **2026-10-15**. Source: [ldtteam/minecolonies](https://github.com/ldtteam/minecolonies). This is an inventory of **currently open** items, not evidence that they are all reproducible or fixable.

## Status of this fork

- The fork matched upstream on `version/main` and `version/1.21` before these changes.
- Implemented parts of upstream [PR #11814](https://github.com/ldtteam/minecolonies/pull/11814): nearby building and mirrored-structure preview rotations.
- Implemented upstream [PR #11827](https://github.com/ldtteam/minecolonies/pull/11827): dining-hall food-history deduplication, empty history handling, and operator reset command.
- A backup of the original 1.21 branch exists at `backup/version-1.21-before-pr-fixes-2026-10-15`.
- The other open PRs and issues remain triage work; several target different Minecraft versions, require conflict resolution, or are drafts.
- GitHub Java 21 Gradle builds for the initial fixes and merged PR #1 passed. No in-game test has yet verified the affected gameplay cases.

## Integration update — 2026-10-16

The 107 issue / 37 PR counts below are the **original snapshot**, not a count of outstanding fixes in this fork. An upstream issue remaining open is not proof that its reported behavior persists in current code.

- **Merged into `version/1.21`:** rotation previews (upstream #11814), dining-hall food history (upstream #11827), format specifiers (upstream #11493), miner ladder block assumptions (upstream #11573), and colony-sign safeguards (upstream issue #11849). GitHub's Java 21 build completed successfully.
- **Fork PR #2, pending validation:** new-tree sapling exclusions (#11855) and jungle trunks with substituted azalea leaves (#11459).
- **Fork PR #3, pending validation:** isolated waterlogged fence/wall blueprint placement from upstream #11730. Its work-order stage changes are intentionally omitted.
- **Fork PR #4, pending validation:** retrying a different pond after repeated grounded fishing hooks (#11267).
- **Already handled in current source, subject to gameplay confirmation:** two-serving bowl-based food recipes in `DefaultRecipeProvider` already require two bowls, consistent with the remedy proposed by issue #11323. No duplicate change was made.
- **Deferred for compatibility or scope:** 26.2-only ports, 1.20.x-only PRs, infrastructure PRs, drafts, asset placeholders, and changes requiring migration of existing construction work orders.

Do not treat a green build as proof that a reported gameplay bug is fully fixed. The individual regression reproductions must still be tested.

## 37 upstream open pull requests

| PR | Base branch | Draft | Title | Fork integration |
| --- | --- | --- | --- | --- |
| [#11854](https://github.com/ldtteam/minecolonies/pull/11854) | `version/main` | No | Blocks Rework | Not integrated |
| [#11851](https://github.com/ldtteam/minecolonies/pull/11851) | `version/1.21` | No | Sliced pizza | Not integrated |
| [#11850](https://github.com/ldtteam/minecolonies/pull/11850) | `version/1.21` | No | Display startup warnings when using unsupported addons | Not integrated |
| [#11842](https://github.com/ldtteam/minecolonies/pull/11842) | `version/main` | No | Nuanced danger traversal | Not integrated |
| [#11837](https://github.com/ldtteam/minecolonies/pull/11837) | `version/1.21` | Yes | Auto close PR's when description doesn't match template | Not integrated |
| [#11827](https://github.com/ldtteam/minecolonies/pull/11827) | `version/1.21` | No | Fix/food history double record | Merged in fork; CI build passed, gameplay untested |
| [#11814](https://github.com/ldtteam/minecolonies/pull/11814) | `version/1.21` | No | Fix nearby building previews rotating differently from the goggles | Merged in fork; CI build passed, gameplay untested |
| [#11788](https://github.com/ldtteam/minecolonies/pull/11788) | `version/main` | No | Migration of claims data | Not integrated |
| [#11766](https://github.com/ldtteam/minecolonies/pull/11766) | `release/1.21` | No | Block tier change/addition proposal from discord convos | Not integrated |
| [#11759](https://github.com/ldtteam/minecolonies/pull/11759) | `version/main` | No | Add waiter to JEI | Not integrated |
| [#11730](https://github.com/ldtteam/minecolonies/pull/11730) | `version/1.21` | No | Fix/water stuff | Blockstate/waterlogging correction in fork PR #3; stage rewrite deferred |
| [#11718](https://github.com/ldtteam/minecolonies/pull/11718) | `version/main` | No | [ImgBot] Optimize images | Not integrated |
| [#11706](https://github.com/ldtteam/minecolonies/pull/11706) | `version/main` | No | Reduce unnecessary complexity to equipment system refactor | Not integrated |
| [#11702](https://github.com/ldtteam/minecolonies/pull/11702) | `version/1.21` | No | Abandoned colony improvements [1.21] | Not integrated |
| [#11697](https://github.com/ldtteam/minecolonies/pull/11697) | `version/main` | No | Abandoned colony improvements | Not integrated |
| [#11640](https://github.com/ldtteam/minecolonies/pull/11640) | `version/main` | No | Add optional bottom-up clearing for builders | Not integrated |
| [#11620](https://github.com/ldtteam/minecolonies/pull/11620) | `version/1.21` | Yes | Fixes for the slot rework coming in BlockUI | Not integrated |
| [#11573](https://github.com/ldtteam/minecolonies/pull/11573) | `version/1.21` | No | Fix/hardcoded miner ladder block 1.21 | Merged in fork PR #1; build passed |
| [#11571](https://github.com/ldtteam/minecolonies/pull/11571) | `version/main` | No | fix(miner): solving #965 | Not integrated |
| [#11493](https://github.com/ldtteam/minecolonies/pull/11493) | `version/1.21` | No | Fix format specifiers in translations [1.21] | Merged in fork PR #1; build passed |
| [#11492](https://github.com/ldtteam/minecolonies/pull/11492) | `version/main` | No | Fix format specifiers in translations [1.20] | Not integrated |
| [#11466](https://github.com/ldtteam/minecolonies/pull/11466) | `version/main` | No | Caching | Not integrated |
| [#11339](https://github.com/ldtteam/minecolonies/pull/11339) | `version/main` | No | Fix building names in research unlocks | Not integrated |
| [#11326](https://github.com/ldtteam/minecolonies/pull/11326) | `version/1.21` | No | Large block and item registry cleanup | Not integrated |
| [#11306](https://github.com/ldtteam/minecolonies/pull/11306) | `version/main` | Yes | feat: Add instant name regeneration with nametag cost | Not integrated |
| [#11205](https://github.com/ldtteam/minecolonies/pull/11205) | `version/1.21` | No | Reusable module templates | Not integrated |
| [#11115](https://github.com/ldtteam/minecolonies/pull/11115) | `version/1.21` | No | Set up standardized formatting for JSON and XML files [1.21] | Not integrated |
| [#11114](https://github.com/ldtteam/minecolonies/pull/11114) | `version/main` | No | Set up standardized formatting for JSON and XML files | Not integrated |
| [#11036](https://github.com/ldtteam/minecolonies/pull/11036) | `version/main` | No | feat(commands): introduce /mc query command with initial 'idlebuilders' query | Not integrated |
| [#11023](https://github.com/ldtteam/minecolonies/pull/11023) | `version/main` | No | Introduce the initial infrastructure for the Archeologist. | Not integrated |
| [#10883](https://github.com/ldtteam/minecolonies/pull/10883) | `version/main` | No | Update JDK version in README.md | Not integrated |
| [#10677](https://github.com/ldtteam/minecolonies/pull/10677) | `version/1.21` | No | Fix unnecessary item handler invalidation | Not integrated |
| [#10599](https://github.com/ldtteam/minecolonies/pull/10599) | `version/main` | No | Improve permission handling around interaction events | Not integrated |
| [#10496](https://github.com/ldtteam/minecolonies/pull/10496) | `version/main` | No | Add option to enable/disable zombie villager converting to visitor | Not integrated |
| [#10286](https://github.com/ldtteam/minecolonies/pull/10286) | `version/main` | No | Colony expedition system | Not integrated |
| [#10087](https://github.com/ldtteam/minecolonies/pull/10087) | `version/main` | No | Add recipe for water bucket | Not integrated |
| [#9942](https://github.com/ldtteam/minecolonies/pull/9942) | `version/main` | No | Window to preselect job at level 0 | Not integrated |

## 107 upstream open issues

| Issue | Title |
| --- | --- |
| [#11855](https://github.com/ldtteam/minecolonies/issues/11855) | Saplings Turned Off Do Not Stop the Forrester Cutting Those Trees |
| [#11849](https://github.com/ldtteam/minecolonies/issues/11849) | Breaking colony sign causes distance to be calculated wrong |
| [#11838](https://github.com/ldtteam/minecolonies/issues/11838) | Build tool menu is blank |
| [#11826](https://github.com/ldtteam/minecolonies/issues/11826) | [1.21.1] Food history still wrong after #11726: dining hall meals are recorded twice, and an empty history never fills |
| [#11818](https://github.com/ldtteam/minecolonies/issues/11818) | [1.20.1] Colony corruption due to unusual interaction with Timeless and Classics Zero |
| [#11815](https://github.com/ldtteam/minecolonies/issues/11815) | [1.20.1] Client Errors when Entering/Loading Another Player's Colony; "colony is null" |
| [#11803](https://github.com/ldtteam/minecolonies/issues/11803) | AI audit of version/1.21: 28 potential correctness and efficiency defects |
| [#11795](https://github.com/ldtteam/minecolonies/issues/11795) | AI log upload \| Mine NPC AI |
| [#11787](https://github.com/ldtteam/minecolonies/issues/11787) | Civs are waiting on Mins to be delivered before going back to work |
| [#11783](https://github.com/ldtteam/minecolonies/issues/11783) | Nether Mine stuck indefinitely in "On an Adventure" status |
| [#11776](https://github.com/ldtteam/minecolonies/issues/11776) | Mistletoe production is too low |
| [#11763](https://github.com/ldtteam/minecolonies/issues/11763) | Builder eating food items that are building materials |
| [#11745](https://github.com/ldtteam/minecolonies/issues/11745) | Ships spawned from worldgen are flooded |
| [#11740](https://github.com/ldtteam/minecolonies/issues/11740) | Nonexistent person |
| [#11734](https://github.com/ldtteam/minecolonies/issues/11734) | Builder requests biome specific blocks to be placed under blocks that are affected by gravity |
| [#11715](https://github.com/ldtteam/minecolonies/issues/11715) | Using the standard smithing upgrade/equipement upgrade template, the allthemodium upgrades cannot be fulfilled |
| [#11709](https://github.com/ldtteam/minecolonies/issues/11709) | Some stonemason ingredient entries are overbroad causing JEI to show wooden slabs as things they work with |
| [#11684](https://github.com/ldtteam/minecolonies/issues/11684) | When creating a crafting recipe using a durable item (like an ore hammer) the recipe subtracts one from the durability of the hammer, messing up the recipe |
| [#11668](https://github.com/ldtteam/minecolonies/issues/11668) | Quarrier gets boggled by big empty areas at the top of the quarry |
| [#11667](https://github.com/ldtteam/minecolonies/issues/11667) | Quarriers often mine directly underneath themselves. |
| [#11665](https://github.com/ldtteam/minecolonies/issues/11665) | Hospital worker sits in Dining Hall, but doesn't eat. |
| [#11663](https://github.com/ldtteam/minecolonies/issues/11663) | schematic scanning includes mobs but excludes entities it should scan and doesn't always show what it is scanning |
| [#11662](https://github.com/ldtteam/minecolonies/issues/11662) | MineColonies workstation recipes disappear from JEI after saving and rejoining a singleplayer world |
| [#11633](https://github.com/ldtteam/minecolonies/issues/11633) | Spawning L5 Urban Birch barracks tower exits game |
| [#11627](https://github.com/ldtteam/minecolonies/issues/11627) | Failed to create block entity domum_ornamentum:materially_retexturable |
| [#11625](https://github.com/ldtteam/minecolonies/issues/11625) | empty buckets do not get returned to cowhand no matter what |
| [#11611](https://github.com/ldtteam/minecolonies/issues/11611) | REsource scroll items overlap when leaving the scroll open |
| [#11597](https://github.com/ldtteam/minecolonies/issues/11597) | Ill guards do not go to hospital |
| [#11533](https://github.com/ldtteam/minecolonies/issues/11533) | Random Ladder by Raider confuses Miner |
| [#11519](https://github.com/ldtteam/minecolonies/issues/11519) | Gatehouse Signs could not be placed. |
| [#11506](https://github.com/ldtteam/minecolonies/issues/11506) | Empty Chest in Raider Caravel |
| [#11491](https://github.com/ldtteam/minecolonies/issues/11491) | Some sentences in the Japanese language file display elements like "%1$d" as-is. |
| [#11486](https://github.com/ldtteam/minecolonies/issues/11486) | Door Spinning II : The Re Spinnening |
| [#11485](https://github.com/ldtteam/minecolonies/issues/11485) | Colonists getting stuck in their workstations |
| [#11459](https://github.com/ldtteam/minecolonies/issues/11459) | Forester replanting wrong saplings |
| [#11449](https://github.com/ldtteam/minecolonies/issues/11449) | Waiters pick up food from restaurant but don't eat, and food just stay in their inventory |
| [#11442](https://github.com/ldtteam/minecolonies/issues/11442) | Waiter starves themselves and becomes a bottleneck to automations |
| [#11441](https://github.com/ldtteam/minecolonies/issues/11441) | AI log upload |
| [#11426](https://github.com/ldtteam/minecolonies/issues/11426) | Possibly missing or forgotten items in the reduceable_ingredient list for crafters. |
| [#11421](https://github.com/ldtteam/minecolonies/issues/11421) | [Bug]: Couriers continuously move Restaurant food to Warehouse resulting in starvation |
| [#11418](https://github.com/ldtteam/minecolonies/issues/11418) | builder does not swap out dynamic blocks when upgrading bulding |
| [#11403](https://github.com/ldtteam/minecolonies/issues/11403) | Farmers AI log upload |
| [#11394](https://github.com/ldtteam/minecolonies/issues/11394) | leisure missing from tagtool list |
| [#11391](https://github.com/ldtteam/minecolonies/issues/11391) | Dining Hall keeps requesting food |
| [#11381](https://github.com/ldtteam/minecolonies/issues/11381) | Racks Not Accepted By Builder |
| [#11380](https://github.com/ldtteam/minecolonies/issues/11380) | Minecolonies crop drops missing from JEI on 1.21 |
| [#11376](https://github.com/ldtteam/minecolonies/issues/11376) | Shire townhall building oasis townhall isntead |
| [#11373](https://github.com/ldtteam/minecolonies/issues/11373) | Missing Translations |
| [#11364](https://github.com/ldtteam/minecolonies/issues/11364) | Dinning hall getting steal by courrier |
| [#11342](https://github.com/ldtteam/minecolonies/issues/11342) | Builder wont take chainmail chest plate for lvl 2 university upgrade |
| [#11328](https://github.com/ldtteam/minecolonies/issues/11328) | Brickyard is still refered to as stone smeltery in research |
| [#11323](https://github.com/ldtteam/minecolonies/issues/11323) | Infinite bowl mitosis |
| [#11319](https://github.com/ldtteam/minecolonies/issues/11319) | AI log upload |
| [#11284](https://github.com/ldtteam/minecolonies/issues/11284) | colony signs are bugged |
| [#11272](https://github.com/ldtteam/minecolonies/issues/11272) | Spam logs when player join LAN server |
| [#11267](https://github.com/ldtteam/minecolonies/issues/11267) | Fisher attempts and fails to cast over fences |
| [#11218](https://github.com/ldtteam/minecolonies/issues/11218) | some error in logs , just reporting to check if its an issue |
| [#11217](https://github.com/ldtteam/minecolonies/issues/11217) | Style packs dont show up for me, but do for others |
| [#11213](https://github.com/ldtteam/minecolonies/issues/11213) | Create mod filter and clipboard data is erased by colonists |
| [#11211](https://github.com/ldtteam/minecolonies/issues/11211) | Food Requests Not Getting From Cookery to Cook??? Everyone Wants Better Menu. |
| [#11192](https://github.com/ldtteam/minecolonies/issues/11192) | Hurt Visitors settings doesn't work by its own |
| [#11138](https://github.com/ldtteam/minecolonies/issues/11138) | Foresters don't replant properly when shearing/breaking leaves is on. |
| [#11137](https://github.com/ldtteam/minecolonies/issues/11137) | Builders often mess up fence blockstates on upgrades |
| [#11136](https://github.com/ldtteam/minecolonies/issues/11136) | Forester does not obey zone for some ops |
| [#11134](https://github.com/ldtteam/minecolonies/issues/11134) | JEI not showing hut recipe tabs |
| [#11128](https://github.com/ldtteam/minecolonies/issues/11128) | Pathfinding: Colonists too happy to take dropping damage |
| [#11119](https://github.com/ldtteam/minecolonies/issues/11119) | Smelter duplicates Regions Unexplored "Raw Redstone Block" infinitely |
| [#11103](https://github.com/ldtteam/minecolonies/issues/11103) | Build outlines have changed somehow and make it really hard to place things |
| [#11055](https://github.com/ldtteam/minecolonies/issues/11055) | Shepherd's Hut does not maintain sheep population at level 5 (drops below expected 10) |
| [#10941](https://github.com/ldtteam/minecolonies/issues/10941) | Quest givers stop accepting / giving quest |
| [#10919](https://github.com/ldtteam/minecolonies/issues/10919) | Custom schematics can't use reflected children |
| [#10916](https://github.com/ldtteam/minecolonies/issues/10916) | Game freezing when viewing Schematics with Render placeholders as target blocks On |
| [#10890](https://github.com/ldtteam/minecolonies/issues/10890) | Citizens hoarding bowls |
| [#10822](https://github.com/ldtteam/minecolonies/issues/10822) | Builder spins in place endlessly when pathfinding |
| [#10818](https://github.com/ldtteam/minecolonies/issues/10818) | Initial citizens spawning with 0 happiness |
| [#10816](https://github.com/ldtteam/minecolonies/issues/10816) | Stonesmelter requests a ridiculous number of lava buckets as fuel |
| [#10797](https://github.com/ldtteam/minecolonies/issues/10797) | No courier's hut in the Colonial pack |
| [#10769](https://github.com/ldtteam/minecolonies/issues/10769) | Friend rank is counting as officer for last contact |
| [#10693](https://github.com/ldtteam/minecolonies/issues/10693) | Deserialized bad stack / Created Empty Stack errors |
| [#10686](https://github.com/ldtteam/minecolonies/issues/10686) | Citizens can't find right path from 3th floor of house Urban Birch style. |
| [#10670](https://github.com/ldtteam/minecolonies/issues/10670) | Warehouse item capability is invalidated every tick leading to compatibility issues |
| [#10647](https://github.com/ldtteam/minecolonies/issues/10647) | The citizens start shaking irregularly |
| [#10563](https://github.com/ldtteam/minecolonies/issues/10563) | Placeholders in supply ships erros in the End when trying to place |
| [#10540](https://github.com/ldtteam/minecolonies/issues/10540) | Nether skin style: missing/bugged skins |
| [#10536](https://github.com/ldtteam/minecolonies/issues/10536) | Christmas Colony sets player hostile with inappropriate permissions |
| [#10485](https://github.com/ldtteam/minecolonies/issues/10485) | MineColonies HUD French translation |
| [#10429](https://github.com/ldtteam/minecolonies/issues/10429) | 1.21.1 blocks seem to not be assessed for pathblocks |
| [#10377](https://github.com/ldtteam/minecolonies/issues/10377) | Disabling recipes not "sticking" on upgrade |
| [#10219](https://github.com/ldtteam/minecolonies/issues/10219) | Decoration Controller overwritten as a Child when Parent upgraded |
| [#10203](https://github.com/ldtteam/minecolonies/issues/10203) | [BUG] Minecolonies Builder allows you to smuggle items into servers (was previously fixed, however in a new iteration). |
| [#10172](https://github.com/ldtteam/minecolonies/issues/10172) | [BUG] Game freezes while selecting a building |
| [#10095](https://github.com/ldtteam/minecolonies/issues/10095) | Sawmill wont learn Cactus block recipes |
| [#10062](https://github.com/ldtteam/minecolonies/issues/10062) | [BUG] Colonists that are hurt, when no hospital is present, go into a weird "being sick" cycle |
| [#10029](https://github.com/ldtteam/minecolonies/issues/10029) | [BUG] Plate armor doesn't render properly |
| [#9817](https://github.com/ldtteam/minecolonies/issues/9817) | [BUG] Doors/gates that are open are replaced when repaired |
| [#9812](https://github.com/ldtteam/minecolonies/issues/9812) | [BUG] Some strings don't have their translation key |
| [#9785](https://github.com/ldtteam/minecolonies/issues/9785) | [BUG] Multi Racks still glitching |
| [#9771](https://github.com/ldtteam/minecolonies/issues/9771) | Debts found during porting |
| [#9638](https://github.com/ldtteam/minecolonies/issues/9638) | [BUG] Overlevelled Enchanter Books don't retain extra level when used |
| [#8821](https://github.com/ldtteam/minecolonies/issues/8821) | [BUG] Builders don't break kelp properly |
| [#8567](https://github.com/ldtteam/minecolonies/issues/8567) | [BUG] Wrong error msg in other dimensions. |
| [#8558](https://github.com/ldtteam/minecolonies/issues/8558) | Empty map, isn't an item that has been accounted for in Hut's. |
| [#8215](https://github.com/ldtteam/minecolonies/issues/8215) | When Miner repairs levels the signs done work |
| [#7776](https://github.com/ldtteam/minecolonies/issues/7776) | Miner refuses to mine after encountering un-mineable block even after changing floor |
| [#7403](https://github.com/ldtteam/minecolonies/issues/7403) | Female raiders use male voices when attacking the Colony |
| [#7180](https://github.com/ldtteam/minecolonies/issues/7180) | When builder dies while doing a job, the next builder begins to dismantle rather than continue. |
| [#5006](https://github.com/ldtteam/minecolonies/issues/5006) | Schematic Issues |

## Next validation work

1. Run a successful NeoForge / Java 21 Gradle build on `version/1.21`.
2. Test mirrored and non-mirrored blueprint previews with building goggles and the build tool.
3. Test dining hall multi-bite meals, empty food-history operator queries, and the new clear-history command.
4. Revisit remaining pull requests one at a time, resolving base-version compatibility and test failures. Do not blindly merge all unreviewed PRs.
5. Reproduce and triage open issues against their respective game versions before changing code.

Inventory intentionally excludes closed issues/PRs and does not claim the two applied changes resolve all reported bugs.
