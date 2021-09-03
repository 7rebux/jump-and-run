package net.rebux.jumpandrun.sql

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.parkour.Difficulty
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.utils.LocationSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import java.util.*

object SQLQueries {
    private val plugin = Main.instance
    private val sqlConnection = plugin.sqlConnection

    fun createTables() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            sqlConnection.update("""
                CREATE TABLE IF NOT EXISTS Parkours (
                    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    name varchar,
                    difficulty int,
                    material varchar,
                    location varchar,
                    reset_height int
                );
            """.trimIndent())

            sqlConnection.update("""
                CREATE TABLE IF NOT EXISTS BestTimes (
                    uuid varchar(36),
                    parkour_id int,
                    int time
                );
            """.trimIndent())
        }
    }

    fun hasGlobalBestTime(parkourId: Int): Boolean {
        sqlConnection.query("""
            SELECT *
            FROM BestTimes
            WHERE parkour_id = $parkourId;
        """.trimIndent()).also { return it.next() }
    }

    fun getGlobalBestTime(parkourId: Int): Pair<UUID, Int> {
        sqlConnection.query("""
            SELECT MIN(time), uuid
            FROM BestTimes
            WHERE parkour_id = $parkourId;
        """.trimIndent()).also { resultSet ->
            resultSet.next();
            return Pair(UUID.fromString(resultSet.getString("uuid")), resultSet.getInt("time"))
        }
    }

    fun hasPersonalBestTime(uuid: UUID, parkourId: Int): Boolean {
        sqlConnection.query("""
            SELECT *
            FROM BestTimes
            WHERE parkour_id = $parkourId
                AND uuid = $uuid;
        """.trimIndent()).also { return it.next() }
    }

    fun getPersonalBestTime(uuid: UUID, parkourId: Int): Int {
        sqlConnection.query("""
            SELECT time
            FROM BestTimes
            WHERE parkour_id = $parkourId
                AND uuid = $uuid;
        """.trimIndent()).also { resultSet ->
            resultSet.next()
            return resultSet.getInt("time")
        }
    }

    fun updateBestTime(time: Int, uuid: UUID, parkourId: Int) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            if (hasPersonalBestTime(uuid, parkourId)) {
                sqlConnection.update("""
                    UPDATE BestTimes
                    SET time = $time
                    WHERE uuid = $uuid
                        AND parkour_id = $parkourId;
            """.trimIndent())
            } else {
                sqlConnection.update("""
                    INSERT INTO BestTimes(uuid, parkour_id, time)
                    VALUES($uuid, $parkourId, $time);
                """.trimIndent())
            }
        }
    }

    fun resetBestTime(uuid: UUID, parkourId: Int) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            sqlConnection.update("""
                DELETE FROM BestTimes
                WHERE parkour_id = $parkourId
                    AND uuid = $uuid;
            """.trimIndent())
        }
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
                    Difficulty.getById(resultSet.getInt("difficulty"))!!,
                    Material.getMaterial(resultSet.getString("material")),
                    LocationSerializer.fromBase64String(resultSet.getString("location")),
                    resultSet.getInt("reset_height")
                )
            )
        }

        return parkours;
    }

    fun addParkour(parkour: Parkour) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            sqlConnection.update("""
                INSERT INTO Parkours(id, name, difficulty, material, location, reset_height)
                VALUES(${parkour.id}, ${parkour.name}, ${parkour.difficulty.id}, ${parkour.material.name}, 
                    ${LocationSerializer.toBase64String(parkour.location)}, ${parkour.resetHeight});
            """.trimIndent())
        }
    }

    fun removeParkour(id: Int) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            sqlConnection.update("""
                DELETE FROM Parkours
                WHERE id = $id;
            """.trimIndent())
        }
    }
}