package net.rebux.jumpandrun.database.entities

import net.rebux.jumpandrun.database.models.Times
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TimeEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<TimeEntity>(Times)
    var uuid by Times.uuid
    var time by Times.time
    var date by Times.date
    var parkour by ParkourEntity referencedOn Times.parkour

    fun toMapEntry() = uuid to time
}
