package net.rebux.jumpandrun.database.entities

import net.rebux.jumpandrun.database.models.Parkours
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.utils.LocationSerializer
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ParkourEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParkourEntity>(Parkours)
    var name        by Parkours.name
    var builder     by Parkours.builder
    var difficulty  by Parkours.difficulty
    var material    by Parkours.material
    var location    by Parkours.location

    fun toParkour() = Parkour(
        id.value,
        name,
        builder,
        difficulty,
        material,
        LocationSerializer.fromBase64String(location)
    )

    override fun delete() {
        TimeEntity.all().filter { it.parkour == this }.forEach { it.delete() }
        super.delete()
    }
}
