package net.rebux.jumpandrun.item.impl

import de.tr7zw.nbtapi.NBT
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.MenuCategory
import net.rebux.jumpandrun.api.MenuSorting
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MenuConfig
import net.rebux.jumpandrun.item.Item
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

/** 0, 1, 2, 3, 4, 5, 6, 7, 8
  * P, A, E, M, H, U, S, L, P */
object MenuItem : Item("menu") {

    private val config = MenuConfig

    override fun onInteract(player: Player) {
        this.openInventory(player)
    }

    fun openInventory(player: Player) {
        val title =
            MessageBuilder(MenuConfig.inventoryTitle)
                .values(
                    mapOf(
                        "completed" to ParkourManager.countParkoursPlayed(player),
                        "records" to ParkourManager.countParkourRecords(player),
                        "quantity" to ParkourManager.parkours.size))
                .prefix(false)
                .buildSingle()
        val size = MenuConfig.parkoursPerPage + 9
        val inventory = Bukkit.createInventory(null, size, title)

        player.openParkourMenu(inventory)
    }

    private fun Player.openParkourMenu(inventory: Inventory) {
        val category = this.data.menuState.category
        val sorting = this.data.menuState.sorting
        val page = this.data.menuState.page
        val parkours =
            ParkourManager.parkours.values
                .filter { parkour ->
                    when (category) {
                        MenuCategory.Easy -> parkour.difficulty == ParkourDifficulty.EASY
                        MenuCategory.Normal -> parkour.difficulty == ParkourDifficulty.NORMAL
                        MenuCategory.Hard -> parkour.difficulty == ParkourDifficulty.HARD
                        MenuCategory.Ultra -> parkour.difficulty == ParkourDifficulty.ULTRA
                        else -> true
                    }
                }
                .sortedWith(sorting.comparator)

        // Parkour items
        for (slot in 0 until config.parkoursPerPage) {
            val index = slot + page * MenuConfig.parkoursPerPage

            if (index == parkours.size) {
                break
            }

            inventory.setItem(slot, parkours[index].buildItem(this))
        }

        // Difficulty item
        if (config.categoryItem) {
            inventory.setItem(inventory.size - 7, buildCategoryItem(this))
        }

        // Sorting item
        if (config.sortingItem) {
            inventory.setItem(inventory.size - 6, buildSortingItem(this))
        }

        // Leaderboard item
        if (config.leaderboardItem) {
            inventory.setItem(inventory.size - 2, buildLeaderboardItem(this))
        }

        // Previous page item
        if (page > 0) {
            inventory.setItem(inventory.size - 9, buildPaginationItem(PaginationType.Previous))
        }

        // Next page item
        if (parkours.size > MenuConfig.parkoursPerPage * (page + 1)) {
            inventory.setItem(inventory.size - 1, buildPaginationItem(PaginationType.Next))
        }

        this.openInventory(inventory)
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
            Builder().material(this.material).displayName(displayName).lore(lore).build()

        if (playerTime != null) {
            val itemMeta = itemStack.itemMeta

            itemMeta!!.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10)

            itemStack.itemMeta = itemMeta
        }

        NBT.modify(itemStack) { nbt -> nbt.setInteger(Plugin.PARKOUR_TAG, this.id) }

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
        val itemStack =
            SkullBuilder().displayName(type.displayName).username(type.skullName).build()

        NBT.modify(itemStack) { nbt ->
            nbt.setInteger(Plugin.PAGE_STEP_TAG, type.step)
        }

        return itemStack
    }

    private fun buildSortingItem(player: Player): ItemStack {
        val selected = player.data.menuState.sorting
        val itemStack = Builder()
            .material(Material.COMPARATOR)
            .displayName("${ChatColor.GREEN}Sorting")
            .lore(
                buildList {
                    MenuSorting.entries.forEach { sorting ->
                        val color = if (sorting == selected) ChatColor.WHITE else ChatColor.GRAY
                        this.add("$color> ${sorting.name}")
                    }
                }
            )
            .build()

        // TODO: Bad way of identifying item without extra data
        NBT.modify(itemStack) { nbt ->
            nbt.setInteger(Plugin.SORTING_TAG, 0)
        }

        return itemStack
    }

    private fun buildLeaderboardItem(player: Player): ItemStack {
        val recordsByPlayer = ParkourManager.recordsByPlayer()
        val itemStack = Builder()
            .material(Material.NETHER_STAR)
            .displayName("${ChatColor.AQUA}Leaderboard")
            .lore(
                buildList {
                    recordsByPlayer.entries
                        .take(5)
                        .forEach {
                            this.add("${ChatColor.GOLD}${it.value} ${ChatColor.WHITE}${Bukkit.getOfflinePlayer(it.key).name}")
                        }

                    add("")
                    add("${ChatColor.GRAY}Your Records: ${ChatColor.GOLD}${recordsByPlayer[player.uniqueId] ?: 0}")
                    add("${ChatColor.GRAY}Your Rank: ${ChatColor.GOLD}${recordsByPlayer.entries.indexOfFirst { it.key == player.uniqueId } + 1}")
                }
            )
            .build()

        // Prevent inventory interactions
        NBT.modify(itemStack) { nbt ->
            nbt.setInteger(Plugin.ID_TAG, -1)
        }

        return itemStack
    }

    private fun buildCategoryItem(player: Player): ItemStack {
        val selected = player.data.menuState.category
        val itemStack = Builder()
            .material(selected.material)
            .displayName("${selected.color}Category")
            .lore(
                buildList {
                    MenuCategory.entries.forEach { category ->
                        if (category == selected) {
                            this.add("${category.color}> ${category.name}")
                        } else {
                            this.add("${ChatColor.GRAY}> ${category.name}")
                        }
                    }
                }
            )
            .build()

        NBT.modify(itemStack) { nbt ->
            nbt.setInteger(Plugin.CATEGORY_TAG, 0)
        }

        return itemStack
    }
}
