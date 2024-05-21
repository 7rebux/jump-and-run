package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.database.entities.LocationEntity
import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.io.BukkitObjectInputStream
import org.jetbrains.exposed.sql.transactions.transaction
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream

class ParkourManager {

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

        migrate()
    }

    fun add(parkourEntity: ParkourEntity) {
        parkours[parkourEntity.id.value] = parkourEntity.toParkour()
    }

    private fun migrate() {
        parkours.values.forEach { parkour ->
            val location = fromBase64String(parkour.location)
            val entity = transaction { LocationEntity.ofLocation(location) }

            transaction {
                val parkourEntity = ParkourEntity.findById(parkour.id)!!
                parkourEntity.newLocation = entity.id
            }

            Bukkit.getLogger().info("Created locations table entry for parkour ${parkour.name} with id ${entity.id}")
        }
    }

    private fun fromBase64String(data: String): Location {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)

        val location = dataInput.readObject() as Location

        dataInput.close()

        return location
    }
}
