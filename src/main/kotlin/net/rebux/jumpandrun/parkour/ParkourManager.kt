package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.sql.SQLQueries
import org.bukkit.Bukkit

class ParkourManager {

    private val plugin = Instance.plugin
    val parkours = arrayListOf<Parkour>()

    fun loadParkours() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            parkours.addAll(SQLQueries.getParkours())
        }
    }

    fun addParkour(parkour: Parkour) {
        parkours.add(parkour)

        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            SQLQueries.addParkour(parkour)
        }
    }

    fun removeParkour(parkour: Parkour) {
        parkours.remove(parkour)

        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            SQLQueries.removeParkour(parkour)
        }
    }

    fun hasParkour(id: Int) = getParkourById(id) != null

    fun getMaxId(): Int? = parkours.maxOfOrNull { it.id }

    fun getParkourById(id: Int): Parkour? = parkours.find { it.id == id }
}
