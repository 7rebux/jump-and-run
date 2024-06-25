package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.database.entities.LocationEntity
import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.parkour.ParkourDifficulty
import net.rebux.jumpandrun.parkour.ParkourManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.Material
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

// TODO: Autocompletion
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
        "join" -> handleJoinCommand(sender, args.getOrNull(1)?.toIntOrNull())
        "add" -> handleAddCommand(sender, args.copyOfRange(1, args.size))
        "remove" -> handleRemoveCommand(sender, args.getOrNull(1)?.toIntOrNull())
        "reset" -> handleResetCommand(sender, args.copyOfRange(1, args.size))
        else -> sendUsage(sender)
      }
    }

    return true
  }

  private fun handleListCommand(sender: CommandSender) {
    val parkours = ParkourManager.parkours.values

    if (parkours.isEmpty()) {
      sender.sendMessage("No parkours found!")
      return
    }

    sender.sendMessage("Found ${parkours.size} parkours:")
    parkours.forEach { parkour ->
      sender.sendMessage("#${parkour.id}: ${parkour.name} - ${parkour.difficulty}")
    }
  }

  private fun handleJoinCommand(sender: CommandSender, id: Int?) {
    if (sender !is Player) {
      sender.sendMessage("This command can only be called as a player!")
      return
    }

    if (sender.data.isInParkour()) {
      sender.sendMessage("You are already in a parkour!")
      return
    }

    val parkour = ParkourManager.parkours[id]

    if (parkour == null) {
      sender.sendMessage("Parkour not found!")
      return
    }

    Bukkit.getPluginManager().callEvent(ParkourJoinEvent(sender, parkour))
  }

  private fun handleAddCommand(sender: CommandSender, args: Array<String>) {
    if (sender !is Player) {
      sender.sendMessage("This command can only be called as a player!")
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
      sender.sendMessage("Difficulty not found!")
      return
    }

    if (material == null) {
      sender.sendMessage("Material not found!")
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

      ParkourManager.add(entity)
      sender.sendMessage("Successfully added new parkour")
    }
  }

  private fun handleRemoveCommand(sender: CommandSender, id: Int?) {
    if (id == null) {
      sendUsage(sender)
      return
    }

    if (ParkourManager.parkours[id] == null) {
      sender.sendMessage("Parkour not found!")
      return
    }

    transaction {
      ParkourEntity.findById(id)?.delete()

      ParkourManager.parkours.remove(id)
      sender.sendMessage("Successfully removed parkour")
    }
  }

  private fun handleResetCommand(sender: CommandSender, args: Array<String>) {
    // [Id, UUID/All]
    if (args.size != 2) {
      sendUsage(sender)
      return
    }

    val id = args[0].toIntOrNull()

    if (id == null) {
      sender.sendMessage("Id must be an Integer!")
      return
    }

    val parkour = ParkourManager.parkours.get(id)

    if (parkour == null) {
      sender.sendMessage("Parkour not found!")
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
      sender.sendMessage("Successfully reset all times!")
    } else {
      transaction {
        val timeEntity = TimeEntity.all().singleOrNull { entity ->
          entity.parkour.id.value == id && entity.uuid == UUID.fromString(args[1])
        }

        if (timeEntity == null) {
          sender.sendMessage("No time for specified UUID found!")
          return@transaction
        }

        timeEntity.delete()
        parkour.times.remove(timeEntity.uuid)
        sender.sendMessage("Successfully reset time for player ${Bukkit.getOfflinePlayer(timeEntity.uuid).name}")
      }
    }
  }

  private fun sendUsage(sender: CommandSender) {
    sender.sendMessage("/jnr list")
    sender.sendMessage("/jnr join <id>")
    sender.sendMessage("/jnr add <name> <builder> <difficulty> <material>")
    sender.sendMessage("/jnr remove <id>")
    sender.sendMessage("/jnr reset <id> <uuid | all>")
  }
}
