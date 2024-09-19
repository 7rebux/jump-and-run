package net.rebux.jumpandrun.inventory.menu

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MenuConfig
import net.rebux.jumpandrun.item.Item.Builder
import net.rebux.jumpandrun.item.Item.SkullBuilder
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.parkour.ParkourDifficulty
import net.rebux.jumpandrun.parkour.ParkourManager
import net.rebux.jumpandrun.utils.MessageBuilder
import net.rebux.jumpandrun.utils.TickFormatter
import net.rebux.jumpandrun.utils.TickFormatter.toMessageValue
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object MenuInventory {

    private val config = MenuConfig

    fun open(player: Player) {
        val title =
            MessageBuilder(MenuConfig.inventoryTitle)
                .values(
                    mapOf(
                        "completed" to ParkourManager.countParkoursPlayed(player),
                        "records" to ParkourManager.countParkourRecords(player),
                        "quantity" to ParkourManager.parkours.size
                    )
                )
                .prefix(false)
                .buildSingle()
        val size = config.parkoursPerPage + 9
        val inventory = Bukkit.createInventory(null, size, title)

        player.buildParkourMenu(inventory)
        player.openInventory(inventory)
    }

    private fun Player.buildParkourMenu(inventory: Inventory) {
        val selectedCategory = this.data.menuState.category
        val selectedSorting = this.data.menuState.sorting
        val selectedFilter = this.data.menuState.filter
        val currentPage = this.data.menuState.page

        val parkours =
            ParkourManager.parkours.values
                .filter { parkour ->
                    when (selectedCategory) {
                        MenuCategory.Easy -> parkour.difficulty == ParkourDifficulty.EASY
                        MenuCategory.Normal -> parkour.difficulty == ParkourDifficulty.NORMAL
                        MenuCategory.Hard -> parkour.difficulty == ParkourDifficulty.HARD
                        MenuCategory.Ultra -> parkour.difficulty == ParkourDifficulty.ULTRA
                        else -> true
                    }
                }
                .filter { parkour -> selectedFilter.predicate(parkour, this) }
                .sortedWith(selectedSorting.comparator)

        for (slot in 0 until config.parkoursPerPage) {
            val index = slot + currentPage * MenuConfig.parkoursPerPage

            if (index == parkours.size) {
                break
            }

            inventory.setItem(slot, parkours[index].buildItem(this))
        }

        // Special items
        if (config.categoryItem) {
            inventory.setItem(inventory.size - 7, buildCategoryItem(data.menuState.category))
        }
        if (config.sortingItem) {
            inventory.setItem(inventory.size - 6, buildSortingItem(data.menuState.sorting))
        }
        if (config.filterItem) {
            inventory.setItem(inventory.size - 5, buildFilterItem(selectedFilter))
        }
        if (config.leaderboardItem) {
            inventory.setItem(inventory.size - 2, buildLeaderboardItem(this))
        }

        // Pagination items
        if (currentPage > 0) {
            inventory.setItem(inventory.size - 9, buildPaginationItem(PaginationType.Previous))
        }
        if (parkours.size > MenuConfig.parkoursPerPage * (currentPage + 1)) {
            inventory.setItem(inventory.size - 1, buildPaginationItem(PaginationType.Next))
        }
    }

    private fun Parkour.buildItem(player: Player): ItemStack {
        val playerTime = this.times[player.uniqueId]
        val bestTime = this.times.values.minOrNull()
        val playersWithBestTime =
            this.times.entries
                .filter { it.value == bestTime }
                .sortedWith(compareBy { it.key != player.uniqueId }) // Self at top
                .map { Bukkit.getOfflinePlayer(it.key) }

        val displayName =
            "${ChatColor.DARK_AQUA}${this.name} %s"
                .format(
                    if (playerTime != null) {
                        if (playerTime == bestTime) {
                            "${ChatColor.GOLD}✫"
                        } else {
                            "${ChatColor.GREEN}✔"
                        }
                    } else {
                        "${ChatColor.RED}✘"
                    })

        val lore = buildList {
            val parkour = this@buildItem

            addAll(
                MessageBuilder(MenuConfig.Entry.difficulty)
                    .values(mapOf("difficulty" to parkour.difficulty.coloredName))
                    .prefix(false)
                    .build())

            addAll(
                MessageBuilder(MenuConfig.Entry.builder)
                    .values(mapOf("builder" to parkour.builder))
                    .prefix(false)
                    .build())

            add("")

            addAll(MessageBuilder(MenuConfig.Entry.PersonalBest.title).prefix(false).build())

            if (playerTime != null) {
                val (time, unit) = TickFormatter.format(playerTime)

                addAll(
                    MessageBuilder(MenuConfig.Entry.PersonalBest.time)
                        .values(mapOf("time" to time, "unit" to unit.toMessageValue()))
                        .prefix(false)
                        .build())
            } else {
                addAll(MessageBuilder(MenuConfig.Entry.noTime).prefix(false).build())
            }

            add("")

            addAll(MessageBuilder(MenuConfig.Entry.GlobalBest.title).prefix(false).build())

            if (bestTime != null) {
                val (time, unit) = TickFormatter.format(bestTime)

                addAll(
                    MessageBuilder(MenuConfig.Entry.GlobalBest.time)
                        .values(mapOf("time" to time, "unit" to unit.toMessageValue()))
                        .prefix(false)
                        .build())

                addAll(MessageBuilder(MenuConfig.Entry.GlobalBest.subtitle).prefix(false).build())

                playersWithBestTime.forEach { player ->
                    addAll(
                        MessageBuilder(MenuConfig.Entry.GlobalBest.player)
                            .values(mapOf("player" to (player.name ?: player.uniqueId)))
                            .prefix(false)
                            .build())
                }
            } else {
                addAll(MessageBuilder(MenuConfig.Entry.noTime).prefix(false).build())
            }
        }

        val itemStack =
            Builder()
                .material(this.material)
                .displayName(displayName)
                .lore(lore)
                .nbt { it.setInteger(Plugin.PARKOUR_TAG, this.id) }
                .build()

        // TODO: This is not working for 1.20
        if (playerTime != null) {
            val itemMeta = itemStack.itemMeta

            itemMeta!!.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10)

            itemStack.itemMeta = itemMeta
        }

        return itemStack
    }

    private enum class PaginationType(
        val skullName: String,
        val displayName: String,
        val step: Int
    ) {
        Next("MHF_ArrowRight", MenuConfig.nextPageTitle, 1),
        Previous("MHF_ArrowLeft", MenuConfig.previousPageTitle, -1),
    }

    private fun buildPaginationItem(type: PaginationType): ItemStack {
        return SkullBuilder()
            .displayName(type.displayName)
            .username(type.skullName)
            .nbt { it.setInteger(Plugin.PAGE_STEP_TAG, type.step) }
            .build()
    }

    private fun buildCategoryItem(selected: MenuCategory): ItemStack {
        return Builder()
            .material(selected.material)
            .displayName("${selected.color}Category")
            .lore(
                buildList {
                    MenuCategory.entries.forEach { category ->
                        if (category == selected) {
                            add("${category.color}> ${category.name}")
                        } else {
                            add("${ChatColor.GRAY}> ${category.name}")
                        }
                    }
                }
            )
            .nbt { it.setByte(Plugin.CATEGORY_TAG, 0) }
            .build()
    }

    private fun buildSortingItem(selected: MenuSorting): ItemStack {
        return Builder()
            .material(Material.COMPARATOR)
            .displayName("${ChatColor.GREEN}Sorting")
            .lore(
                buildList {
                    MenuSorting.entries.forEach { sorting ->
                        val color = if (sorting == selected) ChatColor.WHITE else ChatColor.GRAY
                        add("$color> ${sorting.name}")
                    }
                }
            )
            .nbt { it.setByte(Plugin.SORTING_TAG, 0) }
            .build()
    }

    private fun buildFilterItem(selected: MenuFilter): ItemStack {
        return Builder()
            .material(Material.HOPPER)
            .displayName("${ChatColor.LIGHT_PURPLE}Filter")
            .lore(
                buildList {
                    MenuFilter.entries.forEach { filter ->
                        val color = if (filter == selected) ChatColor.WHITE else ChatColor.GRAY
                        add("$color> ${filter.name}")
                    }
                }
            )
            .nbt { it.setByte(Plugin.FILTER_TAG, 0) }
            .build()
    }

    private fun buildLeaderboardItem(player: Player): ItemStack {
        val recordsByPlayer = ParkourManager.recordsByPlayer()
        val recordCount = recordsByPlayer[player.uniqueId] ?: 0
        val rank = recordsByPlayer.entries.indexOfFirst { it.key == player.uniqueId } + 1

        return Builder()
            .material(Material.NETHER_STAR)
            .displayName("${ChatColor.AQUA}Leaderboard")
            .lore(
                buildList {
                    recordsByPlayer.entries
                        .take(5)
                        .forEach {
                            add("${ChatColor.GOLD}${it.value} ${ChatColor.WHITE}${Bukkit.getOfflinePlayer(it.key).name}")
                        }

                    add("")
                    add("${ChatColor.GRAY}Your Records: ${ChatColor.GOLD}${recordCount}")
                    add("${ChatColor.GRAY}Your Rank: ${ChatColor.GOLD}${rank}")
                }
            )
            .nbt { it.setByte(Plugin.ID_TAG, 0) } // Prevent inventory interactions
            .build()
    }
}
