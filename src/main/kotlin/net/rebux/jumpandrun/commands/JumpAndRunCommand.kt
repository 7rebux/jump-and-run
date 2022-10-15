package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.msg
import net.rebux.jumpandrun.msgTemplate
import net.rebux.jumpandrun.parkour.Difficulty
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.sql.SQLQueries
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.Material
import org.bukkit.entity.Player
import java.lang.Exception
import java.util.*

object JumpAndRunCommand : CommandExecutor {

    private val plugin = Instance.plugin

    private fun CommandSender.sendUsage() {
        this.msg("/jnr list")
        this.msg("/jnr add <name> <builder> <difficultyId> <material>")
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

            "add" -> {
                if (sender !is Player)
                    sender.msgTemplate("commands.playersOnly")
                else if (args.size != 5)
                    sender.sendUsage()
                else {
                    try {
                        val block = sender.location.block.location
                        val location = block.add(
                            if (block.x < 0) -0.5 else 0.5,
                            0.0,
                            if (block.z < 0) -0.5 else 0.5
                        )
                        val id = (plugin.parkourManager.getMaxId() ?: 0) + 1
                        val name = args[1]
                        val builder = args[2]
                        val difficulty = Difficulty.getById(args[3].toInt())!!
                        val material = Material.getMaterial(args[4].uppercase())

                        location.yaw = 90F

                        val parkour = Parkour(id, name, builder, difficulty, material, location)

                        plugin.parkourManager.addParkour(parkour)
                        sender.msgTemplate("commands.jnr.add.success", mapOf("name" to name))
                    } catch (e: Exception) {
                        sender.msg("${ChatColor.RED}${e.message}")
                    }
                }
            }

            "remove" -> {
                if (args.size != 2)
                    sender.sendUsage()
                else {
                    try {
                        val id = args[1].toInt()

                        if (plugin.parkourManager.hasParkour(id)) {
                            val parkour = plugin.parkourManager.getParkourById(id)!!

                            plugin.parkourManager.removeParkour(parkour)
                            sender.msgTemplate("commands.jnr.remove.success", mapOf("name" to parkour.name))
                        }
                        else
                            sender.msgTemplate("commands.jnr.remove.notFound")
                    } catch (e: Exception) {
                        sender.msg("${ChatColor.RED}${e.message}")
                    }
                }
            }

            "reset" -> {
                if (args.size != 3)
                    sender.sendUsage()
                else {
                    try {
                        val parkour = plugin.parkourManager.getParkourById(args[1].toInt())!!

                        if (args[2].lowercase() == "all") {
                            Bukkit.getScheduler().runTaskAsynchronously(plugin) {
                                SQLQueries.removeBestTimes(parkour)
                                sender.msgTemplate("commands.jnr.reset.successAll", mapOf("name" to parkour.name))
                            }
                        } else {
                            val uuid = UUID.fromString(args[2])

                            Bukkit.getScheduler().runTaskAsynchronously(plugin) {
                                if (SQLQueries.hasPersonalBestTime(uuid, parkour)) {
                                    SQLQueries.removeBestTime(uuid, parkour)
                                    sender.msgTemplate("commands.jnr.reset.successSingle", mapOf(
                                        "name" to parkour.name,
                                        "player" to Bukkit.getOfflinePlayer(uuid))
                                    )
                                } else
                                    sender.msgTemplate("commands.jnr.reset.notFound")
                            }
                        }
                    } catch (e: Exception) {
                        sender.msg("${ChatColor.RED}${e.message}")
                    }
                }
            }

            "debug" -> {
                sender.msg("Active players: ${ChatColor.GREEN}${plugin.active.keys.joinToString(", ") { it.name } }")
            }

            else -> sender.sendUsage()
        }
        return true
    }
}
