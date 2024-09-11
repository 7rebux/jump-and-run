package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.database.entities.LocationEntity
import net.rebux.jumpandrun.database.entities.ParkourEntity
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object ParkourManager {

    val parkours = hashMapOf<Int, Parkour>()

    fun load() = transaction {
        ParkourEntity.all()
            .map(ParkourEntity::toParkour)
            .forEach { parkour ->
                parkours[parkour.id] = parkour
            }
    }

    fun register(parkour: Parkour) {
        val id = if (parkour.id == -1) null else parkour.id

        transaction {
            val entity =
                ParkourEntity.new(id) {
                    this.name = parkour.name
                    this.builder = parkour.builder
                    this.difficulty = parkour.difficulty
                    this.material = parkour.material
                    this.location = LocationEntity.ofLocation(parkour.startLocation)
                    this.finishLocation =
                        parkour.finishLocation?.let(LocationEntity.Companion::ofLocation)
                }

            parkours[entity.id.value] = parkour.copy(id = entity.id.value)
        }
    }

    fun recordsByPlayer(): Map<UUID, Int> {
        return parkours.values
            .asSequence()
            .filter { it.times.isNotEmpty() }
            .flatMap { parkour ->
                val recordTime = parkour.times.values.min()

                parkour.times
                    .filterValues { it == recordTime }
                    .map { (uuid, _) -> uuid }
            }
            .groupingBy { it }
            .eachCount()
            .toList()
            .sortedByDescending { (_, count) -> count }
            .toMap()
    }

    fun countParkoursPlayed(player: Player): Int {
        return parkours.values.count { parkour ->
            player.uniqueId in parkour.times
        }
    }

    fun countParkourRecords(player: Player): Int {
        return parkours.values.count { parkour ->
            val recordTime = parkour.times.values.minOrNull()
            val playerTime = parkour.times[player.uniqueId] ?: Long.MAX_VALUE

            recordTime == playerTime
        }
    }
}
