package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import org.jetbrains.exposed.sql.transactions.transaction

class ParkourManager {
    val parkours = arrayListOf<Parkour>()

    fun load() = transaction {
        parkours.addAll(ParkourEntity.all().map { parkourEntity ->
            parkourEntity.toParkour().apply {
                times.addAll(
                    TimeEntity.all()
                        .filter { it.parkour.id.value == this.id }
                        .map(TimeEntity::toParkourTime)
                )
            }
        })
    }

    fun add(parkour: Parkour) {
        transaction { parkour.toEntity() }
        parkours += parkour
    }

    fun remove(parkour: Parkour) {
        transaction {
            ParkourEntity.findById(parkour.id)?.let(ParkourEntity::delete)
        }
        parkours -= parkour
    }

    fun getParkourById(id: Int) = parkours.find { it.id == id }
}
