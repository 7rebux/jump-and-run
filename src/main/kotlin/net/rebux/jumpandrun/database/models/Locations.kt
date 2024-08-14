package net.rebux.jumpandrun.database.models

import org.jetbrains.exposed.dao.id.IntIdTable

object Locations : IntIdTable() {

    val world = text("world")
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val yaw = float("yaw")
    val pitch = float("pitch")
}
