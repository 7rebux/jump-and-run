package net.rebux.jumpandrun.database.entities

import net.rebux.jumpandrun.database.models.Times
import net.rebux.jumpandrun.parkour.Parkour
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TimeEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<TimeEntity>(Times)

    var uuid        by Times.uuid
    var time        by Times.time
    var version     by Times.version
    var date        by Times.date
    var parkour     by ParkourEntity referencedOn Times.parkour

    fun toParkourTime() = Parkour.Time(
        uuid,
        time,
        version
    )
}
