package net.rebux.jumpandrun.database.entities

import net.rebux.jumpandrun.database.models.Locations
import org.bukkit.Location
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LocationEntity(id: EntityID<Int>) : IntEntity(id) {

    var world by Locations.world
    var x by Locations.x
    var y by Locations.y
    var z by Locations.z
    var yaw by Locations.yaw
    var pitch by Locations.pitch

    fun toLocation(): Location {
        val serialized: Map<String, Any> =
            mutableMapOf(
                "world" to world, "x" to x, "y" to y, "z" to z, "yaw" to yaw, "pitch" to pitch)
        return Location.deserialize(serialized)
    }

    companion object : IntEntityClass<LocationEntity>(Locations) {

        fun ofLocation(location: Location) = new {
            world = location.world!!.name
            x = location.x
            y = location.y
            z = location.z
            yaw = location.yaw
            pitch = location.pitch
        }
    }
}
