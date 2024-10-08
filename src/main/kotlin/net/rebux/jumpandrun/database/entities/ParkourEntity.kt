package net.rebux.jumpandrun.database.entities

import net.rebux.jumpandrun.database.models.Parkours
import net.rebux.jumpandrun.database.models.Times
import net.rebux.jumpandrun.parkour.Parkour
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ParkourEntity(id: EntityID<Int>) : IntEntity(id) {

    var name by Parkours.name
    var builder by Parkours.builder
    var difficulty by Parkours.difficulty
    var material by Parkours.material
    var location by LocationEntity referencedOn Parkours.location
    var finishLocation by LocationEntity optionalReferencedOn Parkours.finishLocation
    val times by TimeEntity referrersOn Times.parkour

    fun toParkour(): Parkour {
        val timeEntries = times.associate {
            it.uuid to it.time
        }

        return Parkour(
            id.value,
            name,
            builder,
            difficulty,
            material,
            location.toLocation(),
            finishLocation?.toLocation()
        ).apply {
            this.times.putAll(timeEntries)
        }
    }

    override fun delete() {
        // TODO: Use onDelete callback of exposed
        TimeEntity.all().filter { entity -> entity.parkour == this }.forEach(TimeEntity::delete)
        super.delete()
    }

    companion object : IntEntityClass<ParkourEntity>(Parkours)
}
