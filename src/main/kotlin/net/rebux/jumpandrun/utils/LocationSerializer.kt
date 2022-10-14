package net.rebux.jumpandrun.utils

import org.bukkit.Location
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object LocationSerializer {
    fun toBase64String(location: Location): String {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)

        dataOutput.writeObject(location)

        dataOutput.close()

        return Base64Coder.encodeLines(outputStream.toByteArray())
    }

    fun fromBase64String(data: String): Location {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)

        val location = dataInput.readObject() as Location

        dataInput.close()

        return location
    }
}
