package net.rebux.jumpandrun.database.entities

import net.rebux.jumpandrun.database.models.Parkours
import net.rebux.jumpandrun.parkour.Parkour
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ParkourEntity(id: EntityID<Int>) : IntEntity(id) {

  var name        by Parkours.name
  var builder     by Parkours.builder
  var difficulty  by Parkours.difficulty
  var material    by Parkours.material
  var location    by LocationEntity referencedOn Parkours.location

  fun toParkour() = Parkour(
    id.value,
    name,
    builder,
    difficulty,
    material,
    location.toLocation()
  )

  override fun delete() {
    TimeEntity.all()
      .filter { entity -> entity.parkour == this }
      .forEach(TimeEntity::delete)
    location.delete()

    super.delete()
  }

  companion object : IntEntityClass<ParkourEntity>(Parkours)
}
