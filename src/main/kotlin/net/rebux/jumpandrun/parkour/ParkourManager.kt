package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import org.jetbrains.exposed.sql.transactions.transaction

class ParkourManager {

    val parkours = hashMapOf<Int, Parkour>()

    fun load() = transaction {
        ParkourEntity.all()
            .map { entity ->
                val parkour = entity.toParkour()
                val parkourTimes = TimeEntity.all()
                    .filter { time -> time.parkour.id.value == parkour.id }
                    .map(TimeEntity::toParkourTime)

                parkour.times.addAll(parkourTimes)

                return@map parkour
            }
            .forEach { parkour ->
                parkours[parkour.id] = parkour
            }
    }
}
