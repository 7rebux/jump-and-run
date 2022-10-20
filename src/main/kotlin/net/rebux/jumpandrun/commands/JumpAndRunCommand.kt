package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import net.rebux.jumpandrun.msg
import net.rebux.jumpandrun.msgTemplate
import net.rebux.jumpandrun.parkour.Difficulty
import net.rebux.jumpandrun.utils.LocationSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.Material
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object JumpAndRunCommand : CommandExecutor {

    private val plugin = Instance.plugin

    private fun CommandSender.sendUsage() {
        this.msg("/jnr list")
        this.msg("/jnr add <name> <builder> <difficulty> <material>")
        this.msg("/jnr remove <id>")
        this.msg("/jnr reset <id> <uuid | all>")
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) {
            sender.sendUsage()
            return true
        }

        // list parkours
        when (args[0].lowercase()) {
            "list" -> {
                if (plugin.parkourManager.parkours.isEmpty())
                    sender.msgTemplate("commands.jnr.list.empty")
                else {
                    sender.msgTemplate("commands.jnr.list.full", mapOf("size" to plugin.parkourManager.parkours.size))
                    plugin.parkourManager.parkours.forEach {
                        sender.msg("#${it.id}: ${ChatColor.GREEN}${it.name} ${ChatColor.GRAY}- ${it.difficulty}")
                    }
                }
            }

            // add parkour
            "add" -> {
                if (sender !is Player)
                    sender.msgTemplate("commands.playersOnly")
                else if (args.size != 5)
                    sender.sendUsage()
                else {
                    val block = sender.location.block.location
                    val location = block.add(
                        if (block.x < 0) -0.5 else 0.5,
                        0.0,
                        if (block.z < 0) -0.5 else 0.5
                    ).apply { yaw = 90F }

                    Bukkit.getScheduler().runTaskAsynchronously(plugin) {
                        transaction {
                            ParkourEntity.new {
                                this.name = args[1]
                                this.builder = args[2]
                                this.difficulty = Difficulty.getDifficulty(args[3].uppercase())!!
                                this.material = Material.getMaterial(args[4].uppercase())
                                this.location = LocationSerializer.toBase64String(location)
                            }.also {
                                plugin.parkourManager.parkours += it.toParkour()
                                sender.msgTemplate("commands.jnr.add.success", mapOf("name" to it.name))
                            }
                        }
                    }
                }
            }

            // remove parkour
            "remove" -> {
                if (args.size != 2)
                    sender.sendUsage()
                else {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin) {
                        transaction {
                            ParkourEntity.findById(args[1].toInt())?.let {
                                it.delete()
                                plugin.parkourManager.parkours -= plugin.parkourManager.getParkourById(it.id.value)!!
                                sender.msgTemplate("commands.jnr.remove.success", mapOf("name" to it.name))
                            } ?: sender.msgTemplate("commands.jnr.remove.notFound")
                        }
                    }
                }
            }

            // reset times
            "reset" -> {
                if (args.size != 3)
                    sender.sendUsage()
                else {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin) {
                        if (args[2].lowercase() != "all") {
                            transaction {
                                TimeEntity.all()
                                    .singleOrNull { it.parkour.id.value == args[1].toInt() && it.uuid == UUID.fromString(args[2]) }?.let {
                                        it.delete()
                                        sender.msgTemplate("commands.jnr.reset.successSingle", mapOf(
                                            "name" to it.parkour.name,
                                            "player" to Bukkit.getOfflinePlayer(it.uuid).name)
                                        )
                                        plugin.parkourManager.getParkourById(args[1].toInt())!!.times
                                            .remove(it.uuid)
                                    } ?: sender.msgTemplate("commands.jnr.reset.notFound")
                            }
                        } else {
                            transaction {
                                TimeEntity.all()
                                    .filter { it.parkour.id.value == args[1].toInt() }
                                    .onEach {
                                        it.delete()
                                        plugin.parkourManager.getParkourById(args[1].toInt())!!.times
                                            .remove(it.uuid)
                                    }
                                    .also {
                                        sender.msgTemplate("commands.jnr.reset.successAll",
                                            mapOf("name" to it.first().parkour.name))
                                    }
                            }
                        }
                    }
                }
            }

            // if subcommand is not valid
            else -> sender.sendUsage()
        }
        return true
    }
}
