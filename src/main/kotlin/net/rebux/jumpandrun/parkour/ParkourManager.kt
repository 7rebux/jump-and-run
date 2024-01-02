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

    fun getParkourById(id: Int): Parkour? = parkours.find { it.id == id }
}
