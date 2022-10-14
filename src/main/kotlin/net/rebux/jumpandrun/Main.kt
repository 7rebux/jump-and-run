package net.rebux.jumpandrun

import net.rebux.jumpandrun.commands.JumpAndRunCommand
import net.rebux.jumpandrun.listeners.*
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.parkour.ParkourManager
import net.rebux.jumpandrun.sql.SQLConnection
import net.rebux.jumpandrun.sql.SQLQueries
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

// Sadly this can't be an object due to bukkit implementation
class Main : JavaPlugin() {

    private val configFile = File(dataFolder, "config.yml")
    val config: YamlConfiguration = YamlConfiguration.loadConfiguration(configFile)
    val sqlConnection = SQLConnection()
    val parkourManager = ParkourManager()
    val active = hashMapOf<Player, Parkour>()
    val checkpoints = hashMapOf<Player, Location>()
    val times = hashMapOf<Player, Int>()

    override fun onEnable() {
        instance = this

        Bukkit.getScheduler().runTaskAsynchronously(this) {
            // connect to database
            sqlConnection.connect(
                config.getString("hostname"),
                config.getInt("port").toString(),
                config.getString("database"),
                config.getString("username"),
                config.getString("password")
            )

            // load parkours
            if (sqlConnection.hasTable("Parkours"))
                parkourManager.loadParkours()
            else
                SQLQueries.createTables()
        }

        // register listeners
        server.pluginManager.registerEvents(ConnectionListener, this)
        server.pluginManager.registerEvents(MovementListener, this)
        server.pluginManager.registerEvents(InteractionListener, this)
        server.pluginManager.registerEvents(CommandListener, this)

        // register commands
        getCommand("jumpandrun").executor = JumpAndRunCommand
    }

    override fun onDisable() {
        sqlConnection.disconnect()
    }

    companion object {
        val PREFIX = "${org.bukkit.ChatColor.GRAY}[${org.bukkit.ChatColor.YELLOW}Jump&Run${org.bukkit.ChatColor.GRAY}]"
        lateinit var instance: Main
    }
}
