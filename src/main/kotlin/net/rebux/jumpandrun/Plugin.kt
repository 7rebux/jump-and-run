@file:Suppress("unused", "SpellCheckingInspection")

package net.rebux.jumpandrun

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
  private val databaseConnector = DatabaseConnector(this)

  @Override
  override fun onEnable() {
    databaseConnector.connect()

    SchemaInitializer.initialize()
    ParkourManager.load()

    registerListeners(
      PlayerConnectionListener(),
      PlayerMoveListener(this),
      PlayerInteractListener(),
      InventoryClickListener(),
      CommandPreprocessListener(),
      ParkourFinishListener(this),
      ParkourJoinListener(),
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
