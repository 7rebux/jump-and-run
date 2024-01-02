package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.item.ItemRegistry
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
    private val parkoursPerPage = plugin.config.getInt("parkoursPerPage")
    private val inventorySize = parkoursPerPage + 9

    override fun createItemStack(): ItemStack {
        return Builder()
            .material(Material.PAPER)
            .displayName(plugin.config.getString("items.menu"))
            .build()
    }

    override fun onInteract(player: Player) {
        openInventory(player, 0)
    }

    private fun countPersonalBest(player: Player) = plugin.parkourManager.parkours.count {
        it.times.any { time -> time.uuid == player.uniqueId }
    }

    private fun countGlobalBest(player: Player) = plugin.parkourManager.parkours.count { parkour ->
        (parkour.times.firstOrNull { it.uuid == player.uniqueId }?.time ?: Int.MAX_VALUE) == parkour.times.minOfOrNull { it.time }
    }

    /**
     * Opens the parkour menu inventory for the given [player] on the given [page]
     *
     * @param player the player to open the inventory
     * @param page the initial page
     */
    fun openInventory(player: Player, page: Int) {
        showMenu(
            Bukkit.createInventory(
                null,
                inventorySize,
                template("menu.title", mapOf(
                    "completed" to countPersonalBest(player),
                    "quantity" to plugin.parkourManager.parkours.size,
                    "records" to countGlobalBest(player)
                ))
            ), player, page
        )
    }

    private fun showMenu(inventory: Inventory, player: Player, page: Int) {
        val parkours = plugin.parkourManager.parkours.sortedBy { it.difficulty }
        val parkourCount = plugin.parkourManager.parkours.size

        // clear inventory
        inventory.clear()

        // add parkours
        for (slot in 0 until parkoursPerPage) {
            val index = slot + page * parkoursPerPage

            if (index >= parkourCount)
                break

            val parkour = parkours[index]
            val personalBest = parkour.times.filter { it.uuid == player.uniqueId }.map { it.time }.singleOrNull()
            val globalBest = parkour.times.minOfOrNull { it.time }
            val lore = buildList {
                add(template("menu.difficulty", mapOf("difficulty" to parkour.difficulty)))
                add(template("menu.builder", mapOf("builder" to parkour.builder)))
                add("")
                add(template("menu.personalBest.title"))
                if (personalBest != null)
                    add(template("menu.personalBest.time", mapOf("time" to TimeUtil.ticksToTime(personalBest))))
                else
                    add(template("menu.noTime"))
                add("")
                add(template("menu.globalBest.title"))
                if (globalBest != null) {
                    add(template("menu.globalBest.time", mapOf("time" to TimeUtil.ticksToTime(globalBest))))
                    add(template("menu.globalBest.subtitle"))
                    parkour.times
                        .filter { it.time == globalBest }
                        .forEach { add(template("menu.globalBest.player",
                            mapOf("player" to Bukkit.getOfflinePlayer(it.uuid).name)))
                    }
                }
                else
                    add(template("menu.noTime"))
            }
            val displayName = "${ChatColor.DARK_AQUA}${parkour.name} " +
                    if (personalBest != null)
                        if (personalBest == globalBest) "${ChatColor.GOLD}✫" else "${ChatColor.GREEN}✔"
                    else
                        "${ChatColor.RED}✘"

            // create item
            val itemStack = Builder()
                .material(parkour.material)
                .displayName(displayName)
                .lore(lore)
                .build()

            // enchant item if player has personal best
            personalBest?.run {
                val itemMeta = itemStack.itemMeta

                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                itemStack.itemMeta = itemMeta
                itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10)
            }

            val nmsCopy = CraftItemStack.asNMSCopy(itemStack)
            nmsCopy.tag.setInt(Plugin.PARKOUR_TAG, parkour.id)

            // add item to inventory
            inventory.setItem(slot, CraftItemStack.asCraftMirror(nmsCopy))
        }

        // add navigation items
        if (parkourCount > parkoursPerPage * (page + 1)) {
            val itemStack = SkullBuilder()
                .displayName(template("items.nextPage"))
                .username("MHF_ArrowRight")
                .build()

            val nmsCopy = CraftItemStack.asNMSCopy(itemStack)
            nmsCopy.tag.setInt(Plugin.PAGE_TAG, page)

            inventory.setItem(inventorySize - 1, CraftItemStack.asCraftMirror(nmsCopy))
        }
        if (page > 0) {
            val itemStack = SkullBuilder()
                .displayName(template("items.previousPage"))
                .username("MHF_ArrowLeft")
                .build()

            val nmsCopy = CraftItemStack.asNMSCopy(itemStack)
            nmsCopy.tag.setInt(Plugin.PAGE_TAG, page * -1)

            inventory.setItem(inventorySize - 9, CraftItemStack.asCraftMirror(nmsCopy))
        }

        // open inventory
        player.openInventory(inventory)
    }
}
