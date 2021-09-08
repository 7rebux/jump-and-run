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
        SQLQueries.addParkour(parkour)
    }

    fun removeParkour(id: Int) {
        parkours.remove(getParkourById(id))
        SQLQueries.removeParkour(id)
        SQLQueries.removeBestTimes(id)
    }

    fun hasParkour(id: Int) = getParkourById(id) != null

    fun getMaxId(): Int? = parkours.map { it.id }.maxOrNull()

    private fun getParkourById(id: Int): Parkour? = parkours.find { it.id == id }
}