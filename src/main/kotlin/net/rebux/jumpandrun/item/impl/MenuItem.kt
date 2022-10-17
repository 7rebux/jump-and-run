package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.template
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class MenuItem : Item() {

    private val plugin = Instance.plugin

    override fun getItemStack(): ItemStack {
        return Builder()
            .material(Material.PAPER)
            .displayName(plugin.config.getString("items.menu"))
            .build()
    }

    override fun onInteract(player: Player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            showMenu(
                Bukkit.createInventory(null, 54, getItemStack().itemMeta.displayName),
                player,
                0
            )
        }
    }

    private fun showMenu(inventory: Inventory, player: Player, page: Int) {
        val parkoursPerPage = 45

        for (slot in 0..parkoursPerPage) {
            val index = slot + page * parkoursPerPage

            if (index >= plugin.parkourManager.parkours.size)
                break

            val parkour = plugin.parkourManager.parkours[index]
            val bestTime = parkour.times.map { it.value }.minOrNull()

            val lore = buildList {
                add(template("menu.difficulty", mapOf("difficulty" to parkour.difficulty)))
                add(template("menu.builder", mapOf("builder" to parkour.builder)))
                add("")
                add(template("menu.personalBest.title"))
                if (parkour.times.contains(player))
                    add(template("menu.personalBest.time", mapOf("time" to TimeUtil.ticksToTime(parkour.times[player]!!))))
                else
                    add(template("menu.noTime"))
                add("")
                add(template("menu.globalBest.title"))
                if (bestTime != null) {
                    add(template("menu.globalBest.time", mapOf("time" to TimeUtil.ticksToTime(bestTime))))
                    add(template("menu.globalBest.subtitle"))
                    parkour.times
                        .filter { it.value == bestTime }
                        .forEach { add(template("menu.globalBest.player", mapOf("player" to it.key.name)))
                    }
                }
                else
                    add(template("menu.noTime"))
            }

            // create item
            val item = object: Item() {
                override fun getItemStack(): ItemStack {
                    return Builder()
                        .material(parkour.material)
                        .displayName("${ChatColor.DARK_AQUA}${parkour.name}")
                        .lore(lore)
                        .build()
                }

                override fun onInteract(player: Player) {}
            }

            inventory.setItem(slot, item.getItemStack())
        }

        // TODO("Add navigation items")

        // open inventory
        player.openInventory(inventory)
    }
}
