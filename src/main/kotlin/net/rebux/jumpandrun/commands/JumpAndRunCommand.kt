package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.database.entities.LocationEntity
import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import net.rebux.jumpandrun.msg
import net.rebux.jumpandrun.msgTemplate
import net.rebux.jumpandrun.parkour.ParkourDifficulty
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.Material
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class JumpAndRunCommand(private val plugin: Plugin) : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        abel: String,
        args: Array<String>
    ): Boolean {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) { ->
            when (args.firstOrNull()?.lowercase()) {
                "list" -> handleListCommand(sender)
                "add" -> handleAddCommand(sender, args.copyOfRange(1, args.size))
                "remove" -> handleRemoveCommand(sender, args.getOrNull(1)?.toIntOrNull())
                "reset" -> handleResetCommand(sender, args.copyOfRange(1, args.size))
                else -> sendUsage(sender)
            }
        }

        return true
    }

    private fun handleListCommand(sender: CommandSender) {
        val parkours = plugin.parkourManager.parkours.values

        if (parkours.isEmpty()) {
            sender.msgTemplate("commands.jnr.list.empty")
            return
        }

        sender.msgTemplate("commands.jnr.list.full", mapOf("size" to parkours.size))
        parkours.forEach { parkour ->
            sender.msg("#${parkour.id}: ${ChatColor.GREEN}${parkour.name} ${ChatColor.GRAY}- ${parkour.difficulty}")
        }
    }

    private fun handleAddCommand(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.msgTemplate("commands.playersOnly")
            return
        }

        // [Name, Builder, Difficulty, Material]
        if (args.size != 4) {
            sendUsage(sender)
            return
        }

        val difficulty = ParkourDifficulty.values().find { it.name == args[2].uppercase() }
        val material: Material? = Material.getMaterial(args[3].uppercase())

        if (difficulty == null) {
            sender.msgTemplate("commands.jnr.add.invalidDifficulty")
            return
        }

        if (material == null) {
            sender.msgTemplate("commands.jnr.add.invalidMaterial")
            return
        }

        transaction {
            val entity = ParkourEntity.new {
                this.name = args[0]
                this.builder = args[1]
                this.difficulty = difficulty
                this.material = material
                this.location = LocationEntity.ofLocation(sender.location)
            }

            plugin.parkourManager.add(entity)
            sender.msgTemplate("commands.jnr.add.success", mapOf("name" to entity.name))
        }
    }

    private fun handleRemoveCommand(sender: CommandSender, id: Int?) {
        if (id == null) {
            sendUsage(sender)
            return
        }

        if (plugin.parkourManager.parkours[id] == null) {
            sender.msgTemplate("commands.jnr.remove.notFound")
            return
        }

        val removed = plugin.parkourManager.parkours.remove(id)!!
        transaction {
            ParkourEntity.findById(id)?.delete()
        }

        sender.msgTemplate("commands.jnr.remove.success", mapOf("name" to removed.name))
    }

    private fun handleResetCommand(sender: CommandSender, args: Array<String>) {
        // [Id, UUID/All]
        if (args.size != 2) {
            sendUsage(sender)
            return
        }

        val id = args[0].toIntOrNull()

        if (id == null) {
            sender.msgTemplate("commands.jnr.reset.idNotNumeric")
            return
        }

        val parkour = plugin.parkourManager.parkours.get(id)

        if (parkour == null) {
            sender.msgTemplate("commands.jnr.reset.notFound")
            return
        }

        if (args[1].lowercase() == "all") {
            transaction {
                TimeEntity.all()
                    .filter { entity ->
                        entity.parkour.id.value == id
                    }
                    .forEach { entity ->
                        entity.delete()
                        parkour.times.remove(entity.uuid)
                    }
            }
            sender.msgTemplate("commands.jnr.reset.successAll", mapOf("name" to parkour.name))
        } else {
            transaction {
                val timeEntity = TimeEntity.all().singleOrNull { entity ->
                    entity.parkour.id.value == id && entity.uuid == UUID.fromString(args[1])
                }

                if (timeEntity == null) {
                    sender.msgTemplate("commands.jnr.reset.notFound")
                    return@transaction
                }

                timeEntity.delete()
                parkour.times.remove(timeEntity.uuid)
                sender.msgTemplate(
                    "commands.jnr.reset.successSingle", mapOf(
                        "name" to parkour.name,
                        "player" to Bukkit.getOfflinePlayer(timeEntity.uuid).name!!
                    )
                )
            }
        }
    }

    private fun sendUsage(sender: CommandSender) {
        sender.msg("/jnr list")
        sender.msg("/jnr add <name> <builder> <difficulty> <material>")
        sender.msg("/jnr remove <id>")
        sender.msg("/jnr reset <id> <uuid | all>")
    }
}
