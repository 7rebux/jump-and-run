package net.rebux.jumpandrun.database.entities

import net.rebux.jumpandrun.database.models.Locations.nullable
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
    var location    by Parkours.location
    var newLocation by Parkours.newLocation.nullable()

    fun toParkour() = Parkour(
        id.value,
        name,
        builder,
        difficulty,
        material,
        location
    )

    override fun delete() {
        TimeEntity.all()
            .filter { entity -> entity.parkour == this }
            .forEach(TimeEntity::delete)

        super.delete()
    }

    companion object : IntEntityClass<ParkourEntity>(Parkours)
}
