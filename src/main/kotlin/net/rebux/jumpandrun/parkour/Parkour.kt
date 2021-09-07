package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.sql.SQLQueries
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class Parkour(val id: Int,
    val name: String,
    val difficulty: Difficulty,
    val material: Material,
    val location: Location,
    val resetHeight: Int) {

    fun getItem(player: Player): ItemStack {
        val itemStack = ItemStack(material)
        val itemMeta = itemStack.itemMeta
        val itemLore = arrayListOf<String>()
        val hasBestTime = SQLQueries.hasPersonalBestTime(player.uniqueId, id)

        itemMeta.displayName = "${ChatColor.DARK_PURPLE}$name"
        itemLore.add("${ChatColor.YELLOW}Schwierigkeit: $difficulty")

        if (hasBestTime)
            itemLore.add("${ChatColor.GOLD}Persönliche Bestzeit: ${ChatColor.AQUA}" +
                    TimeUtil.millisToTime(SQLQueries.getPersonalBestTime(player.uniqueId, id)))

        if (SQLQueries.hasGlobalBestTime(id)) {
            SQLQueries.getGlobalBestTime(id).also {
                itemLore.add("${ChatColor.RED}Globale Bestzeit von ${ChatColor.BLUE}${Bukkit.getOfflinePlayer(it.first).name}: " +
                        "${ChatColor.AQUA}${TimeUtil.millisToTime(it.second)}")
            }
        }

        itemMeta.lore = itemLore
        itemStack.itemMeta = itemMeta

        if (hasBestTime)
            itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10)

        return itemStack
    }

    fun start(player: Player) {
        player.teleport(location)

        Main.instance.playerInventorySaves[player] = arrayListOf()
        for (i in 0..player.inventory.size) {
            if (player.inventory.getItem(i) != null && player.inventory.getItem(i).type != Material.AIR)
                Main.instance.playerInventorySaves[player]!! += Pair(player.inventory.getItem(i).clone(), i)
        }
        player.inventory.clear()
        player.inventory.setItem(0, Items.getCheckpointItem())
        player.inventory.setItem(8, Items.getLeaveItem())

        Main.instance.playerCheckpoints[player] = Pair(this, location)

        Main.instance.timers[player]!!.start()
    }

    fun quit(player: Player) {
        Main.instance.timers[player]!!.stop()

        player.inventory.clear()
        Main.instance.playerInventorySaves[player]!!.forEach { player.inventory.setItem(it.second, it.first) }
        Main.instance.playerInventorySaves.remove(player)

        Main.instance.playerCheckpoints.remove(player)
    }

    fun finish(player: Player) {
        Main.instance.timers[player]!!.stop()
        val time = Main.instance.timers[player]!!.elapsedMillis
        var flag = false

        player.sendMessage("${Main.PREFIX} Du hast den Parkour erfolgreich abgeschlossen! " +
                "Deine Zeit: ${ChatColor.AQUA}${TimeUtil.millisToTime(time)}")

        Bukkit.getScheduler().runTaskAsynchronously(Main.instance) {
            if (!SQLQueries.hasPersonalBestTime(player.uniqueId, id) || time < SQLQueries.getPersonalBestTime(player.uniqueId, id)) {
                player.sendMessage("${Main.PREFIX} Du hast eine neue persönliche Bestzeit aufgestellt!")
                player.playSound(player.location, Sound.ANVIL_LAND, 1.0F, 1.0F)
                flag = true
            }

            if (!SQLQueries.hasGlobalBestTime(id) || time < SQLQueries.getGlobalBestTime(id).second) {
                Bukkit.broadcastMessage("${Main.PREFIX} Der Spieler ${ChatColor.BLUE}${player.name} ${ChatColor.GRAY}hat " +
                        "mit ${ChatColor.AQUA}${TimeUtil.millisToTime(time)} ${ChatColor.GRAY}eine neue globale Bestzeit " +
                        "für den Parkour ${ChatColor.DARK_PURPLE}$name ${ChatColor.GRAY}aufgestellt!")
                Bukkit.getOnlinePlayers().forEach { it.playSound(player.location, Sound.LEVEL_UP, 1.0F, 1.0F) }
            }

            if (flag)
                SQLQueries.updateBestTime(time, player.uniqueId, id)
        }

        player.inventory.clear()
        Main.instance.playerInventorySaves[player]!!.forEach { player.inventory.setItem(it.second, it.first) }
        Main.instance.playerInventorySaves.remove(player)

        Main.instance.playerCheckpoints.remove(player)

        player.performCommand("/spawn")
    }
}