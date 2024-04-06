package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.template
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/**
 * An [Item] implementation that lists every parkour in an inventory
 */
object MenuItem : Item() {

    val id = ItemRegistry.register(this)

    private val plugin = Instance.plugin
    private val parkours = plugin.parkourManager.parkours.values.sortedBy(Parkour::difficulty)

    override fun createItemStack(): ItemStack {
        return Builder()
            .material(Material.PAPER)
            .displayName(plugin.config.getString("items.menu"))
            .build()
    }

    override fun onInteract(player: Player) {
        openInventory(player, 0)
    }

    fun openInventory(player: Player, page: Int) {
        val inventory = Bukkit.createInventory(
            null,
            plugin.config.getInt("parkoursPerPage") + 9,
            template("menu.title", mapOf(
                "completed" to countParkoursPlayed(player),
                "records" to countParkourRecords(player),
                "quantity" to plugin.parkourManager.parkours.size
            ))
        )

        openMenu(inventory, player, page)
    }

    private fun openMenu(inventory: Inventory, player: Player, page: Int) {
        val parkoursPerPage = inventory.size - 9

        // TODO: Is this necessary?
        inventory.clear()

        for (slot in 0 until parkoursPerPage) {
            val index = slot + page * parkoursPerPage

            if (index == parkours.size) {
                break
            }

            inventory.setItem(slot, parkours[index].buildItem(player))
        }

        if (parkours.size > parkoursPerPage * (page + 1)) {
            inventory.setItem(
                inventory.size - 1,
                buildPaginationItem(PaginationType.Next, page)
            )
        }

        if (page > 0) {
            inventory.setItem(
                inventory.size - 9,
                buildPaginationItem(PaginationType.Previous, page)
            )
        }

        player.openInventory(inventory)
    }

    private fun Parkour.buildItem(player: Player): CraftItemStack {
        val playerTime = this.times.firstOrNull { time ->
            time.uuid == player.uniqueId
        }?.ticks
        val bestTime = this.times.minOfOrNull(Parkour.Time::ticks)
        val playersWithBestTime = this.times.mapNotNull { time ->
            if (time.ticks == bestTime) Bukkit.getOfflinePlayer(time.uuid) else null
        }

        val displayName = "${ChatColor.DARK_AQUA}${this.name} %s".format(
            if (playerTime != null) {
                if (playerTime == bestTime) {
                    "${ChatColor.GOLD}✫"
                } else {
                    "${ChatColor.GREEN}✔"
                }
            } else {
                "${ChatColor.RED}✘"
            }
        )

        val lore = buildList {
            val parkour = this@buildItem

            add(template("menu.difficulty", mapOf("difficulty" to parkour.difficulty)))
            add(template("menu.builder", mapOf("builder" to parkour.builder)))
            add("")

            add(template("menu.personalBest.title"))

            if (playerTime != null) {
                add(template("menu.personalBest.time", mapOf("time" to TimeUtil.ticksToTime(playerTime))))
            } else {
                add(template("menu.noTime"))
            }

            add("")

            add(template("menu.globalBest.title"))

            if (bestTime != null) {
                add(template("menu.globalBest.time", mapOf("time" to TimeUtil.ticksToTime(bestTime))))
                add(template("menu.globalBest.subtitle"))
                playersWithBestTime.forEach { player ->
                    add(template("menu.globalBest.player", mapOf("player" to player.name)))
                }
            } else {
                add(template("menu.noTime"))
            }
        }

        val itemStack = Builder()
            .material(this.material)
            .displayName(displayName)
            .lore(lore)
            .build()

        if (playerTime != null) {
            val itemMeta = itemStack.itemMeta

            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10)

            // TODO: Has this be to reassigned?
            itemStack.itemMeta = itemMeta
        }

        val nmsCopy = CraftItemStack.asNMSCopy(itemStack)

        nmsCopy.tag.setInt(Plugin.PARKOUR_TAG, this.id)

        return CraftItemStack.asCraftMirror(nmsCopy)
    }

    private enum class PaginationType(
        val skullName: String,
        val displayName: String,
        val step: Int
    ) {
        Next("MHF_ArrowRight", template("items.previousPage"), 1),
        Previous("MHF_ArrowLeft", template("items.nextPage"), -1),
    }

    private fun buildPaginationItem(type: PaginationType, page: Int): CraftItemStack {
        val itemStack = SkullBuilder()
            .displayName(type.displayName)
            .username(type.skullName)
            .build()
        val nmsCopy = CraftItemStack.asNMSCopy(itemStack)

        nmsCopy.tag.setInt(Plugin.PAGE_TAG, page)
        nmsCopy.tag.setInt(Plugin.PAGE_STEP_TAG, type.step)

        return CraftItemStack.asCraftMirror(nmsCopy)
    }

    private fun countParkoursPlayed(player: Player): Int {
        return parkours.count { parkour ->
            parkour.times.any { time ->
                time.uuid == player.uniqueId
            }
        }
    }

    private fun countParkourRecords(player: Player): Int {
        return parkours.count { parkour ->
            val recordTime = parkour.times.minOfOrNull(Parkour.Time::ticks)
            val playerTime = parkour.times.firstOrNull { time ->
                time.uuid == player.uniqueId
            }?.ticks ?: Int.MAX_VALUE

            return@count recordTime == playerTime
        }
    }
}
