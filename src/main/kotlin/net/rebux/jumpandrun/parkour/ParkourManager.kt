package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.database.entities.LocationEntity
import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import org.jetbrains.exposed.sql.transactions.transaction

object ParkourManager {

  val parkours = hashMapOf<Int, Parkour>()

  fun load() = transaction {
    ParkourEntity.all()
        .map { entity ->
          val parkour = entity.toParkour()
          val parkourTimes =
              TimeEntity.all()
                  .filter { time -> time.parkour.id.value == parkour.id }
                  .map { time -> time.uuid to time.time }

          parkour.times.putAll(parkourTimes)

          return@map parkour
        }
        .forEach { parkour -> parkours[parkour.id] = parkour }
  }

  fun register(parkour: Parkour) {
    val id = parkour.id.let { if (it == -1) null else parkour.id }

    transaction {
      val entity =
          ParkourEntity.new(id) {
            this.name = parkour.name
            this.builder = parkour.builder
            this.difficulty = parkour.difficulty
            this.material = parkour.material
            this.location = LocationEntity.ofLocation(parkour.startLocation)
            this.finishLocation = parkour.finishLocation?.let(LocationEntity.Companion::ofLocation)
          }

      parkours[entity.id.value] = parkour
    }
  }
}
