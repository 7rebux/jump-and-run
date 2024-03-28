@file:Suppress("unused", "SpellCheckingInspection")

package net.rebux.jumpandrun

import net.rebux.jumpandrun.api.PlayerData
import net.rebux.jumpandrun.commands.*
import net.rebux.jumpandrun.config.PluginConfig
import net.rebux.jumpandrun.database.DatabaseConnector
import net.rebux.jumpandrun.database.SchemaInitializer
import net.rebux.jumpandrun.listeners.*
import net.rebux.jumpandrun.parkour.ParkourManager
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

// Sadly this can't be an object due to bukkit implementation
class Plugin : JavaPlugin() {
    private val instance = Instance(this)
    private val config = PluginConfig()
    private val databaseConnector = DatabaseConnector()
    private val schemaInitializer = SchemaInitializer()
    val parkourManager = ParkourManager()
    val players = mutableListOf<PlayerData>()

    override fun onEnable() {
        databaseConnector.connect()
        schemaInitializer.initialize()
        parkourManager.load()

        registerListeners(
            ConnectionListener,
            MovementListener,
            InteractionListener,
            CommandListener,
        )
        registerCommands(
            "jumpandrun" to JumpAndRunCommand,
            "top" to TopCommand
        )
    }

    private fun registerListeners(vararg listener: Listener) {
        listener.forEach { server.pluginManager.registerEvents(it, this) }
    }

    private fun registerCommands(vararg commands: Pair<String, CommandExecutor>) {
        commands.forEach { this.getCommand(it.first).executor = it.second }
    }

    companion object {
        const val ID_TAG = "net.rebux.jumpandrun.id"
        const val PARKOUR_TAG = "net.rebux.jumpandrun.parkour"
        const val PAGE_TAG = "net.rebux.jumpandrun.page"
    }
}
