package net.rebux.jumpandrun.sql

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.parkour.Difficulty
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.utils.LocationSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

object SQLQueries {

    private val plugin = Instance.plugin
    private val sqlConnection = plugin.sqlConnection

    fun createTables() {
        sqlConnection.update("""
            CREATE TABLE Parkours (
                id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
                name varchar(255),
                builder varchar(255),
                difficulty int,
                material varchar(255),
                location varchar(1024)
            );
        """.trimIndent())


        sqlConnection.update("""
            CREATE TABLE BestTimes (
                uuid varchar(36),
                parkour_id int,
                time int
            );
        """.trimIndent())
    }

    fun hasGlobalBestTime(parkour: Parkour): Boolean {
        sqlConnection.query("""
            SELECT *
            FROM BestTimes
            WHERE parkour_id = ${parkour.id};
        """.trimIndent()).also { return it.next() }
    }

    fun getGlobalBestTimes(parkour: Parkour): Pair<List<UUID>, Int> {
        sqlConnection.query("""
            SELECT uuid, time 
            FROM BestTimes
            WHERE parkour_id = ${parkour.id}
	            AND time = (SELECT MIN(time) FROM BestTimes WHERE parkour_id = ${parkour.id});
        """.trimIndent()).also { resultSet ->
            val players = arrayListOf<UUID>()
            var time = Integer.MAX_VALUE

            while (resultSet.next()) {
                players += UUID.fromString(resultSet.getString("uuid"))
                time = resultSet.getInt("time")
            }

            return Pair(players, time)
        }
    }

    fun hasPersonalBestTime(player: Player, parkour: Parkour): Boolean {
        sqlConnection.query("""
            SELECT *
            FROM BestTimes
            WHERE parkour_id = ${parkour.id}
                AND uuid = "${player.uniqueId}";
        """.trimIndent()).also { return it.next() }
    }

    fun hasPersonalBestTime(uuid: UUID, parkour: Parkour): Boolean {
        sqlConnection.query("""
            SELECT *
            FROM BestTimes
            WHERE parkour_id = ${parkour.id}
                AND uuid = "$uuid";
        """.trimIndent()).also { return it.next() }
    }

    fun getPersonalBestTime(player: Player, parkour: Parkour): Int {
        sqlConnection.query("""
            SELECT time
            FROM BestTimes
            WHERE parkour_id = ${parkour.id}
                AND uuid = "${player.uniqueId}";
        """.trimIndent()).also { resultSet ->
            resultSet.next()
            return resultSet.getInt("time")
        }
    }

    fun updateBestTime(time: Int, player: Player, parkour: Parkour) {
        if (hasPersonalBestTime(player, parkour)) {
            sqlConnection.update("""
                UPDATE BestTimes
                SET time = $time
                WHERE uuid = "${player.uniqueId}"
                    AND parkour_id = ${parkour.id};
            """.trimIndent())
        } else {
            sqlConnection.update("""
                INSERT INTO BestTimes(uuid, parkour_id, time)
                VALUES("${player.uniqueId}", ${parkour.id}, $time);
            """.trimIndent())
        }
    }

    fun removeBestTime(uuid: UUID, parkour: Parkour) {
        sqlConnection.update("""
            DELETE FROM BestTimes
            WHERE parkour_id = ${parkour.id}
                AND uuid = "$uuid";
        """.trimIndent())
    }

    fun getParkours(): ArrayList<Parkour> {
        val parkours = arrayListOf<Parkour>()

        val resultSet = sqlConnection.query("""
            SELECT *
            FROM Parkours;
        """.trimIndent())

        while (resultSet.next()) {
            parkours.add(
                Parkour(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("builder"),
                    Difficulty.getById(resultSet.getInt("difficulty"))!!,
                    Material.getMaterial(resultSet.getString("material")),
                    LocationSerializer.fromBase64String(resultSet.getString("location")),
                )
            )
        }

        return parkours
    }

    fun addParkour(parkour: Parkour) {
        sqlConnection.update("""
            INSERT INTO Parkours(id, name, builder, difficulty, material, location)
            VALUES(${parkour.id}, "${parkour.name}", "${parkour.builder}", ${parkour.difficulty.id}, "${parkour.material.name}",
                "${LocationSerializer.toBase64String(parkour.location)}");
        """.trimIndent())
    }

    fun removeParkour(parkour: Parkour) {
        sqlConnection.update("""
            DELETE FROM Parkours
            WHERE id = ${parkour.id};
        """.trimIndent())

        removeBestTimes(parkour)
    }

    fun removeBestTimes(parkour: Parkour) {
        sqlConnection.update("""
            DELETE FROM BestTimes
            WHERE parkour_id = ${parkour.id};
        """.trimIndent())
    }
}
