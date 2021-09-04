package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.parkour.Difficulty
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.sql.SQLQueries
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.Material
import org.bukkit.entity.Player
import java.lang.Exception
import java.util.*

class JumpAndRunCommand : CommandExecutor {
    private val plugin = Main.instance

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            if (args.isEmpty() || args[0] == " " || args[0].lowercase(Locale.getDefault()) == "help") {
                sender.sendMessage("${Main.PREFIX} /jnr list")
                sender.sendMessage("${Main.PREFIX} /jnr add <name> <difficultyId> <material> <resetHeight>")
                sender.sendMessage("${Main.PREFIX} /jnr remove <id>")
                sender.sendMessage("${Main.PREFIX} /jnr reset <id> <uuid>")
            }

            when (args[0].lowercase(Locale.getDefault())) {
                "list" -> {
                    if (plugin.parkourManager.parkours.isEmpty())
                        sender.sendMessage("${Main.PREFIX} Es sind keine Parcours vorhanden!")
                    else
                        plugin.parkourManager.parkours.forEach {
                            sender.sendMessage("${Main.PREFIX} #${it.id}: ${it.name} - ${it.difficulty}")
                    }
                }
                "add" -> {
                    if (sender !is Player)
                        sender.sendMessage("${Main.PREFIX} Dieser Befehl kann nur als Spieler ausgeführt werden!")
                    else if (args.size != 5)
                        sender.sendMessage("${Main.PREFIX} /jnr add <name> <difficultyId> <material> <resetHeight>")
                    else {
                        try {
                            val location = sender.location
                            val id = plugin.parkourManager.getMaxId() ?: 0
                            val name = args[1]
                            val difficulty = Difficulty.getById(args[2].toInt())
                            val material = Material.getMaterial(args[3].uppercase())
                            val resetHeight = args[4].toInt()

                            plugin.parkourManager.addParkour(
                                Parkour(
                                    id+1,
                                    name,
                                    difficulty!!,
                                    material,
                                    location,
                                    resetHeight
                                )
                            )

                            sender.sendMessage("${Main.PREFIX} Parkour erfolgreich hinzugefügt")
                        } catch (e: Exception) {
                            sender.sendMessage(e.message)
                        }
                    }
                }
                "remove" -> {
                    if (args.size != 2)
                        sender.sendMessage("${Main.PREFIX} /jnr remove <id>")
                    else {
                        try {
                            val id = args[1].toInt()

                            if (plugin.parkourManager.hasParkour(id)) {
                                plugin.parkourManager.removeParkour(id)
                                sender.sendMessage("${Main.PREFIX} Parkour erfolgreich entfernt")
                            }
                            else
                                sender.sendMessage("${Main.PREFIX} Dieser Parkour existiert nicht!")
                        } catch (e: Exception) {
                            sender.sendMessage(e.message)
                        }
                    }
                }
                "reset" -> {
                    if (args.size != 3)
                        sender.sendMessage("${Main.PREFIX} /jnr reset <id> <uuid>")
                    else {
                        try {
                            val id = args[1].toInt()
                            val uuid = UUID.fromString(args[2])

                            if (SQLQueries.hasPersonalBestTime(uuid, id))
                                SQLQueries.resetBestTime(uuid, id)
                            else
                                sender.sendMessage("${Main.PREFIX} Dieser Spieler hat keine Bestzeit!")
                        } catch (e: Exception) {
                            sender.sendMessage(e.message)
                        }
                    }
                }
            }
        }
        return true
    }
}