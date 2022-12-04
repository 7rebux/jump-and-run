@file:Suppress("unused", "SpellCheckingInspection")

package net.rebux.jumpandrun

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer
import net.minecraft.server.v1_8_R3.MinecraftServer
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import net.rebux.jumpandrun.commands.*
import net.rebux.jumpandrun.config.PluginConfig
import net.rebux.jumpandrun.database.DatabaseConnector
import net.rebux.jumpandrun.database.SchemaInitializer
import net.rebux.jumpandrun.listeners.*
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.parkour.ParkourManager
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.Location
import org.bukkit.command.CommandExecutor
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.Timer
import java.util.TimerTask

// Sadly this can't be an object due to bukkit implementation
class Plugin : JavaPlugin() {

    private val instance = Instance(this)
    private val config = PluginConfig()
    private val databaseConnector = DatabaseConnector()
    private val schemaInitializer = SchemaInitializer()

    val parkourManager = ParkourManager()
    val active = hashMapOf<Player, Parkour>()
    val checkpoints = hashMapOf<Player, Location>()
    val times = hashMapOf<Player, Int>()

    override fun onEnable() {
        // connect to database
        databaseConnector.connect()
        schemaInitializer.initialize()

        // load data
        parkourManager.load()

        // register listeners
        registerListeners(
            ConnectionListener,
            MovementListener,
            InteractionListener,
            CommandListener,
        )

        // register commands
        registerCommands(mapOf(
            "jumpandrun" to JumpAndRunCommand,
            "top" to TopCommand
        ))

        // add bar scheduler
        Timer().scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                active.keys.forEach { player ->
                    val time: Int = times[player] ?: MinecraftServer.getServer().at()
                    val bar: String = template(
                        "timer.bar",
                        mapOf("time" to TimeUtil.ticksToTime(MinecraftServer.getServer().at() - time))
                    )

                    (player as CraftPlayer).handle.playerConnection
                        .sendPacket(PacketPlayOutChat(ChatSerializer.a("{\"text\":\"$bar\"}"), 2))
                }
            }
        }, 0, 50)
    }

    private fun registerListeners(vararg listener: Listener) {
        listener.forEach { server.pluginManager.registerEvents(it, this) }
    }

    private fun registerCommands(commands: Map<String, CommandExecutor>) {
        commands.forEach { this.getCommand(it.key).executor = it.value }
    }

    companion object {
        const val ID_TAG = "net.rebux.jumpandrun.id"
        const val PARKOUR_TAG = "net.rebux.jumpandrun.parkour"
        const val PAGE_TAG = "net.rebux.jumpandrun.page"
    }
}
