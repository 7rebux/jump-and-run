package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.events.ParkourLeaveEvent
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.parkour.ParkourDifficulty
import net.rebux.jumpandrun.parkour.ParkourManager
import net.rebux.jumpandrun.utils.MessageBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class JumpAndRunCommand(private val plugin: Plugin) : CommandExecutor, TabCompleter {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        when (args.firstOrNull()?.lowercase()) {
            "list" -> handleListCommand(sender)
            "join" -> handleJoinCommand(sender, args.getOrNull(1)?.toIntOrNull())
            "leave" -> handleLeaveCommand(sender)
            "add" -> handleAddCommand(sender, args.copyOfRange(1, args.size))
            "remove" -> handleRemoveCommand(sender, args.getOrNull(1)?.toIntOrNull())
            "reset" -> handleResetCommand(sender, args.copyOfRange(1, args.size))
            else -> sendUsage(sender)
        }

        return true
    }

    private fun handleListCommand(sender: CommandSender) {
        val parkours = ParkourManager.parkours.values

        if (parkours.isEmpty()) {
            MessageBuilder("No parkours found!").error().buildAndSend(sender)
            return
        }

        MessageBuilder("Found {size} parkours:")
            .values(mapOf("size" to parkours.size))
            .buildAndSend(sender)

        parkours.forEach { parkour ->
            MessageBuilder("# {id}: {name} - {difficulty}")
                .values(
                    mapOf(
                        "id" to parkour.id,
                        "name" to parkour.name,
                        "difficulty" to parkour.difficulty))
                .buildAndSend(sender)
        }
    }

    private fun handleJoinCommand(sender: CommandSender, id: Int?) {
        if (sender !is Player) {
            MessageBuilder(MessagesConfig.Command.playersOnly)
                .error()
                .buildAndSend(sender)
            return
        }

        if (sender.data.inPractice) {
            MessageBuilder("Can't join parkour in practice mode!").error().buildAndSend(sender)
            return
        }

        ParkourManager.parkours[id]?.let {
            Bukkit.getPluginManager().callEvent(ParkourJoinEvent(sender, it))
        } ?: MessageBuilder("Parkour not found!").error().buildAndSend(sender)
    }

    private fun handleAddCommand(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            MessageBuilder(MessagesConfig.Command.playersOnly)
                .error()
                .buildAndSend(sender)
            return
        }

        // [Name, Builder, Difficulty, Material]
        if (args.size != 4) {
            sendUsage(sender)
            return
        }

        val difficulty = ParkourDifficulty.entries.find { it.name == args[2].uppercase() }
        val material = Material.entries.find { it.name == args[3].uppercase() }

        if (difficulty == null) {
            MessageBuilder("Difficulty not found!").error().buildAndSend(sender)
            return
        }

        if (material == null) {
            MessageBuilder("Material not found!").error().buildAndSend(sender)
            return
        }

        ParkourManager.register(
            Parkour(
                id = -1,
                name = args[0],
                builder = args[1],
                difficulty = difficulty,
                material = material,
                startLocation = sender.location))
        MessageBuilder("Successfully added new parkour").buildAndSend(sender)
    }

    private fun handleLeaveCommand(sender: CommandSender) {
        if (sender !is Player) {
            MessageBuilder(MessagesConfig.Command.playersOnly)
                .error()
                .buildAndSend(sender)
            return
        }

        // Events can not be called asynchronously
        Bukkit.getScheduler().runTask(plugin) { ->
            Bukkit.getPluginManager().callEvent(ParkourLeaveEvent(sender))
        }
    }

    private fun handleRemoveCommand(sender: CommandSender, id: Int?) {
        ParkourManager.parkours[id]?.let {
            transaction {
                ParkourEntity.findById(it.id)?.delete()

                ParkourManager.parkours.remove(it.id)
                sender.sendMessage("Successfully removed parkour")
            }
        } ?: MessageBuilder("Parkour not found!").error().buildAndSend(sender)
    }

    private fun handleResetCommand(sender: CommandSender, args: Array<String>) {
        // [Id, UUID/All]
        if (args.size != 2) {
            sendUsage(sender)
            return
        }

        val id = args[0].toIntOrNull()

        if (id == null) {
            MessageBuilder("Id must be an Integer!").error().buildAndSend(sender)
            return
        }

        val parkour = ParkourManager.parkours.get(id)

        if (parkour == null) {
            MessageBuilder("Parkour not found!").error().buildAndSend(sender)
            return
        }

        if (args[1].lowercase() == "all") {
            transaction {
                TimeEntity.all()
                    .filter { entity -> entity.parkour.id.value == id }
                    .forEach { entity ->
                        entity.delete()
                        parkour.times.remove(entity.uuid)
                    }
            }
            MessageBuilder("Successfully reset all times!").buildAndSend(sender)
        } else {
            transaction {
                val timeEntity =
                    TimeEntity.all().singleOrNull { entity ->
                        entity.parkour.id.value == id && entity.uuid == UUID.fromString(args[1])
                    }

                if (timeEntity == null) {
                    MessageBuilder("No time for specified UUID found!").error().buildAndSend(sender)
                    return@transaction
                }

                timeEntity.delete()
                parkour.times.remove(timeEntity.uuid)
                MessageBuilder("Successfully reset time for player {name}")
                    .values(mapOf("name" to (Bukkit.getOfflinePlayer(timeEntity.uuid).name ?: timeEntity.uuid.toString())))
                    .error()
                    .buildAndSend(sender)
            }
        }
    }

    private fun sendUsage(sender: CommandSender) {
        MessageBuilder(
                """
      /jnr list
      /jnr join <id>
      /jnr leave
      /jnr add <name> <builder> <difficulty> <material>
      /jnr remove <id>
      /jnr reset <id> <uuid | all>
    """
                    .trimIndent())
            .buildAndSend(sender)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): List<String> {
        if (args.size == 1) {
            return listOf("list", "join", "add", "remove", "reset")
        }

        if (args.size == 2 && args[0] in listOf("join", "remove", "reset")) {
            return ParkourManager.parkours.keys.map(Int::toString)
        }

        if (args.size == 4 && args[0] == "add") {
            return ParkourDifficulty.entries.map(ParkourDifficulty::name).filter {
                it.startsWith(args[3])
            }
        }

        if (args.size == 5 && args[0] == "add") {
            return Material.entries.map(Material::name).filter { it.startsWith(args[4]) }
        }

        return emptyList()
    }
}
