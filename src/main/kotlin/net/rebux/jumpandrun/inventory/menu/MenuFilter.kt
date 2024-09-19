package net.rebux.jumpandrun.inventory.menu

import net.rebux.jumpandrun.parkour.Parkour
import org.bukkit.entity.Player

enum class MenuFilter(val predicate: (Parkour, Player) -> Boolean) {
    None({ _, _ -> true }),
    Records({ parkour, player -> player.hasGlobalBest(parkour) }),
    NonRecords({ parkour, player -> !player.hasGlobalBest(parkour) }),
    Played({ parkour, player -> parkour.times.contains(player.uniqueId) }),
    NonPlayed({ parkour, player -> !parkour.times.contains(player.uniqueId) })
}

private fun Player.hasGlobalBest(parkour: Parkour): Boolean {
    val globalBest = parkour.times.values.minOrNull() ?: return false
    return parkour.times[this.uniqueId] == globalBest
}
