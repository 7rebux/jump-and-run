package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.sql.SQLQueries
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@Suppress("SpellCheckingInspection")
class MenuItem : Item() {

    private val plugin = Main.instance

    override fun getItemStack(): ItemStack {
        return Builder()
            .material(Material.PAPER)
            .displayName("${ChatColor.GRAY}» ${ChatColor.AQUA}JumpAndRuns")
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
            val hasTime = SQLQueries.hasPersonalBestTime(player, parkour)

            val lore = arrayListOf(
                "${ChatColor.GRAY}» ${ChatColor.YELLOW}Schwierigkeit: ${parkour.difficulty}",
                "${ChatColor.GRAY}» ${ChatColor.YELLOW}Builder: ${ChatColor.BLUE}${parkour.builder}",
                "",
                "${ChatColor.GRAY}» ${ChatColor.GOLD}Persönliche Bestzeit:",
                if (hasTime) "${ChatColor.BLUE}${TimeUtil.ticksToTime(SQLQueries.getPersonalBestTime(player, parkour))}" else "${ChatColor.RED}--.--.---",
                "",
                "${ChatColor.GRAY}» ${ChatColor.GOLD}Globale Bestzeit:",
                if (SQLQueries.hasGlobalBestTime(parkour)) "${ChatColor.BLUE}${TimeUtil.ticksToTime(SQLQueries.getGlobalBestTimes(parkour).second)}" else "${ChatColor.RED}--.--.---",
            )

            if (SQLQueries.hasGlobalBestTime(parkour)) {
                lore.add("${ChatColor.GRAY}von:")
                lore.addAll(SQLQueries.getGlobalBestTimes(parkour).first.map { "${ChatColor.BLUE}${Bukkit.getOfflinePlayer(it).name}" })
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
