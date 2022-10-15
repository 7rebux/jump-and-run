@file:Suppress("unused")

package net.rebux.jumpandrun

import net.rebux.jumpandrun.commands.JumpAndRunCommand
import net.rebux.jumpandrun.config.PluginConfig
import net.rebux.jumpandrun.listeners.*
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.parkour.ParkourManager
import net.rebux.jumpandrun.sql.SQLConnection
import net.rebux.jumpandrun.sql.SQLQueries
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

// Sadly this can't be an object due to bukkit implementation
class Plugin : JavaPlugin() {

    private val instance = Instance(this)
    private val config = PluginConfig()

    val sqlConnection = SQLConnection()
    val parkourManager = ParkourManager()
    val active = hashMapOf<Player, Parkour>()
    val checkpoints = hashMapOf<Player, Location>()
    val times = hashMapOf<Player, Int>()

    override fun onEnable() {
        Bukkit.getScheduler().runTaskAsynchronously(this) {
            // connect to database
            sqlConnection.connect()

            // load parkours
            if (sqlConnection.hasTable("Parkours"))
                parkourManager.loadParkours()
            else
                SQLQueries.createTables()
        }

        // register listeners
        registerListeners(
            ConnectionListener,
            MovementListener,
            InteractionListener,
            CommandListener,
        )

        // register commands
        getCommand("jumpandrun").executor = JumpAndRunCommand
    }

    override fun onDisable() {
        sqlConnection.disconnect()
    }

    private fun registerListeners(vararg listener: Listener) {
        listener.forEach { server.pluginManager.registerEvents(it, this) }
    }
}
