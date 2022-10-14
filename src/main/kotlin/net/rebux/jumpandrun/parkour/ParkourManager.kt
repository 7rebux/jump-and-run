package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.sql.SQLQueries
import org.bukkit.Bukkit

class ParkourManager {
    val parkours = arrayListOf<Parkour>()

    fun loadParkours() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance) {
            parkours.addAll(SQLQueries.getParkours())
        }
    }

    fun addParkour(parkour: Parkour) {
        parkours.add(parkour)

        Bukkit.getScheduler().runTaskAsynchronously(Main.instance) {
            SQLQueries.addParkour(parkour)
        }
    }

    fun removeParkour(parkour: Parkour) {
        parkours.remove(parkour)

        Bukkit.getScheduler().runTaskAsynchronously(Main.instance) {
            SQLQueries.removeParkour(parkour)
        }
    }

    fun hasParkour(id: Int) = getParkourById(id) != null

    fun getMaxId(): Int? = parkours.map { it.id }.maxOrNull()

    fun getParkourById(id: Int): Parkour? = parkours.find { it.id == id }
}
