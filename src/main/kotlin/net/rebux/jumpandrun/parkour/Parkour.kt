package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.sql.SQLQueries
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Parkour(val id: Int,
    val name: String,
    val difficulty: Difficulty,
    val material: Material,
    val location: Location,
    val resetHeight: Int) {

    // TODO call this asynchronous
    fun getItem(player: Player): ItemStack {
        val itemStack = ItemStack(material)
        val itemMeta = itemStack.itemMeta
        val itemLore = arrayListOf<String>()

        itemMeta.displayName = name
        itemLore.add("Schwierigkeit: $difficulty")

        if (SQLQueries.hasPersonalBestTime(player.uniqueId, id)) {
            itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10)
            itemLore.add("Persönliche Bestzeit: ${TimeUtil.millisToTime(SQLQueries.getPersonalBestTime(player.uniqueId, id))}")
        }

        if (SQLQueries.hasGlobalBestTime(id)) {
            SQLQueries.getGlobalBestTime(id).also {
                itemLore.add("Globale Bestzeit von ${Bukkit.getOfflinePlayer(it.first).name}: ${TimeUtil.millisToTime(it.second)}")
            }
        }

        itemMeta.lore = itemLore
        itemStack.itemMeta = itemMeta

        return itemStack
    }

    fun start(player: Player) {
        player.teleport(location)

        Main.instance.playerInventorySaves[player] = arrayListOf()
        for (i in 0..player.inventory.size) {
            if (player.inventory.getItem(i) != null && player.inventory.getItem(i).type != Material.AIR)
                Main.instance.playerInventorySaves[player]!! += Pair(player.inventory.getItem(i), i)
        }
        player.inventory.clear()
        // TODO give jnr items

        Main.instance.playerCheckpoints[player] = Pair(this, location)

        Main.instance.timers[player]!!.start()
    }

    fun quit(player: Player) {
        Main.instance.timers[player]!!.stop()

        player.performCommand("/spawn")

        player.inventory.clear()
        Main.instance.playerInventorySaves[player]!!.forEach { player.inventory.setItem(it.second, it.first) }
        Main.instance.playerInventorySaves.remove(player)

        Main.instance.playerCheckpoints.remove(player)
    }

    fun finish(player: Player) {
        Main.instance.timers[player]!!.stop()
        val time = Main.instance.timers[player]!!.elapsedMillis

        player.performCommand("/spawn")

        player.sendMessage("${Main.instance.prefix} Du hast den Parkour erfolgreich abgeschlossen! Deine Zeit: ${TimeUtil.millisToTime(time)}")

        Bukkit.getScheduler().runTaskAsynchronously(Main.instance) {
            if (time < SQLQueries.getPersonalBestTime(player.uniqueId, id)) {
                player.sendMessage("${Main.instance.prefix} Du hast eine neue persönliche Bestzeit aufgestellt!")
                player.playSound(player.location, Sound.ANVIL_LAND, 1.0F, 1.0F)
            }

            if (time < SQLQueries.getGlobalBestTime(id).second) {
                Bukkit.broadcastMessage("${Main.instance.prefix} Der Spieler ${player.name} hat mit $time eine neue globale Bestzeit für den Parkour $name aufgestellt!")
                player.playSound(player.location, Sound.LEVEL_UP, 1.0F, 1.0F)
            }

            SQLQueries.updateBestTime(time, player.uniqueId, id)
        }

        player.inventory.clear()
        Main.instance.playerInventorySaves[player]!!.forEach { player.inventory.setItem(it.second, it.first) }
        Main.instance.playerInventorySaves.remove(player)

        Main.instance.playerCheckpoints.remove(player)
    }
}