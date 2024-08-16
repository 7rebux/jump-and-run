@file:Suppress("unused", "SpellCheckingInspection")

package net.rebux.jumpandrun

import net.rebux.jumpandrun.commands.JumpAndRunCommand
import net.rebux.jumpandrun.commands.PracticeCommand
import net.rebux.jumpandrun.commands.TopCommand
import net.rebux.jumpandrun.database.DatabaseConnector
import net.rebux.jumpandrun.database.SchemaInitializer
import net.rebux.jumpandrun.listeners.*
import net.rebux.jumpandrun.parkour.ParkourManager
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

// Sadly this can't be an object due to bukkit implementation
class Plugin : JavaPlugin() {

    private val instance = ParkourInstance(this)

    @Override
    override fun onEnable() {
        this.logger.info("Connecting to database...")
        DatabaseConnector.connect()
        this.logger.info("Successfully connected to database!")
        SchemaInitializer.initialize()
        this.logger.info("Successfully loaded database schema!")

        this.logger.info("Loading parkours from database...")
        ParkourManager.load()
        this.logger.info("Successfully loaded ${ParkourManager.parkours.size} parkours!")

        registerListeners(
            PlayerConnectionListener,
            PlayerMoveListener,
            PlayerInteractListener,
            PlayerDropItemListener,
            InventoryClickListener,
            CommandPreprocessListener,
            FoodLevelChangeListener,
            EntityDamageListener,
            PracticeEnableListener,
            PracticeDisableListener,
            ParkourJoinListener,
            ParkourLeaveListener,
            ParkourFinishListener(this),
        )

        registerCommands(
            "jumpandrun" to JumpAndRunCommand(this),
            "practice" to PracticeCommand(),
            "top" to TopCommand())
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
        const val DIFFICULTY_TAG = "net.rebux.jumpandrun.difficulty"
    }
}
