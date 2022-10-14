package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.Main
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

@Suppress("SpellCheckingInspection")
object JumpAndRunCommand : CommandExecutor {

    private val plugin = Main.instance

    private fun CommandSender.msg(message: String) = this.sendMessage("${Main.PREFIX} $message")

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
                    sender.msg("Es sind ${ChatColor.RED}keine ${ChatColor.GRAY}Jump and Runs vorhanden!")
                else {
                    sender.msg("Es wurden ${ChatColor.GREEN}${plugin.parkourManager.parkours.size} ${ChatColor.GRAY}Jump and Runs gefunden:")
                    plugin.parkourManager.parkours.forEach {
                        sender.msg("#${it.id}: ${ChatColor.GREEN}${it.name} ${ChatColor.GRAY}- ${it.difficulty}")
                    }
                }
            }

            "add" -> {
                if (sender !is Player)
                    sender.msg("${ChatColor.RED}Dieser Befehl kann nur als Spieler ausgeführt werden!")
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
                        sender.msg("Jump and Run ${ChatColor.GREEN}${parkour.name} ${ChatColor.GRAY}erfolgreich hinzugefügt")
                    } catch (e: Exception) {
                        sender.sendMessage(e.message)
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
                            sender.msg("Jump and Run ${ChatColor.RED}${parkour.name} ${ChatColor.GRAY}erfolgreich entfernt")
                        }
                        else
                            sender.msg("${ChatColor.RED}Dieses Jump and Run existiert nicht!")
                    } catch (e: Exception) {
                        sender.sendMessage("${ChatColor.RED}${e.message}")
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
                                sender.msg("Erfolgreich ${ChatColor.GREEN}alle Bestzeiten für ${ChatColor.GREEN}${parkour.name} ${ChatColor.GRAY}entfernt")
                            }
                        } else {
                            val uuid = UUID.fromString(args[2])

                            Bukkit.getScheduler().runTaskAsynchronously(plugin) {
                                if (SQLQueries.hasPersonalBestTime(uuid, parkour)) {
                                    SQLQueries.removeBestTime(uuid, parkour)
                                    sender.msg("Erfolgreich Bestzeit für ${parkour.name} ${ChatColor.GRAY}von ${ChatColor.GREEN}${Bukkit.getOfflinePlayer(uuid)} ${ChatColor.GRAY}entfernt")
                                } else
                                    sender.msg("${ChatColor.RED}Dieser Spieler hat keine Bestzeit!")
                            }
                        }
                    } catch (e: Exception) {
                        sender.sendMessage(e.message)
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
