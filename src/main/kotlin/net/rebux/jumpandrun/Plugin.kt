@file:Suppress("unused", "SpellCheckingInspection")

package net.rebux.jumpandrun

import net.rebux.jumpandrun.commands.*
import net.rebux.jumpandrun.config.DatabaseConfig
import net.rebux.jumpandrun.config.ItemsConfig
import net.rebux.jumpandrun.config.MenuConfig
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.config.ParkourConfig
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

  @Override
  override fun onEnable() {
    this.logger.info("Loading configurations...")
    MessagesConfig.createOrLoad(this)
    ItemsConfig.createOrLoad(this)
    MenuConfig.createOrLoad(this)
    ParkourConfig.createOrLoad(this)
    DatabaseConfig.createOrLoad(this)

    this.logger.info("Connecting to database...")
    DatabaseConnector.connect()
    SchemaInitializer.initialize()

    this.logger.info("Loading parkours from database...")
    ParkourManager.load()

    registerListeners(
      PlayerConnectionListener,
      PlayerMoveListener,
      PlayerInteractListener,
      InventoryClickListener,
      CommandPreprocessListener,
      ParkourJoinListener,
      ParkourFinishListener(this),
    )

    registerCommands(
      "jumpandrun" to JumpAndRunCommand(this),
      "top" to TopCommand()
    )
  }

  private fun registerListeners(vararg listener: Listener) {
    listener.forEach { server.pluginManager.registerEvents(it, this) }
  }

  private fun registerCommands(vararg commands: Pair<String, CommandExecutor>) {
    commands.forEach { this.getCommand(it.first)!!.setExecutor(it.second) }
  }

  companion object {
    const val ID_TAG = "net.rebux.jumpandrun.id"
    const val PARKOUR_TAG = "net.rebux.jumpandrun.parkour"
    const val PAGE_TAG = "net.rebux.jumpandrun.page"
    const val PAGE_STEP_TAG = "net.rebux.jumpandrun.page.step"
  }
}
