package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import org.jetbrains.exposed.sql.transactions.transaction

object ParkourManager {

  val parkours = hashMapOf<Int, Parkour>()

  fun load() = transaction {
    ParkourEntity.all()
      .map { entity ->
        val parkour = entity.toParkour()
        val parkourTimes = TimeEntity.all()
          .filter { time -> time.parkour.id.value == parkour.id }
          .map { time -> time.uuid to time.time }

        parkour.times.putAll(parkourTimes)

        return@map parkour
      }
      .forEach { parkour ->
        parkours[parkour.id] = parkour
      }
  }

  fun add(parkourEntity: ParkourEntity) {
    parkours[parkourEntity.id.value] = parkourEntity.toParkour()
  }
}
