package net.rebux.jumpandrun

import net.rebux.jumpandrun.commands.JumpAndRunCommand
import net.rebux.jumpandrun.listeners.*
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.parkour.ParkourManager
import net.rebux.jumpandrun.parkour.Timer
import net.rebux.jumpandrun.sql.SQLConnection
import net.rebux.jumpandrun.sql.SQLQueries
import net.rebux.jumpandrun.utils.ConfigUtil
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main: JavaPlugin() {
    private val mainConfigFile = File(dataFolder, "config.yml")
    val mainConfig: YamlConfiguration = YamlConfiguration.loadConfiguration(mainConfigFile)

    val sqlConnection = SQLConnection()
    val parkourManager = ParkourManager()

    val timers = hashMapOf<Player, Timer>()
    val playerInventorySaves = hashMapOf<Player, ArrayList<Pair<ItemStack, Int>>>()
    val playerCheckpoints = hashMapOf<Player, Pair<Parkour, Location>>()

    override fun onEnable() {
        instance = this

        sqlConnection.connect(
            ConfigUtil.getString("hostname"),
            ConfigUtil.getInt("port").toString(),
            ConfigUtil.getString("database"),
            ConfigUtil.getString("username"),
            ConfigUtil.getString("password"))
        SQLQueries.createTables()

        parkourManager.loadParkours()

        server.pluginManager.registerEvents(ConnectionListener(), this)
        server.pluginManager.registerEvents(MovementListener(), this)
        server.pluginManager.registerEvents(InteractionListener(), this)
        server.pluginManager.registerEvents(InventoryListener(), this)
        server.pluginManager.registerEvents(WorldChangeListener(), this)

        getCommand("jumpandrun").executor = JumpAndRunCommand()
    }

    override fun onDisable() {
        sqlConnection.disconnect()
    }

    companion object {
        val PREFIX = "${org.bukkit.ChatColor.GRAY}[${org.bukkit.ChatColor.YELLOW}Jump&Run${org.bukkit.ChatColor.GRAY}]"
        lateinit var instance: Main
    }
}