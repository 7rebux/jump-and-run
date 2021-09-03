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
    private val prefix = plugin.prefix

    // TODO permission
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            if (args.isEmpty() || args[0].lowercase(Locale.getDefault()) == "help") {
                sender.sendMessage("$prefix /jnr list")
                sender.sendMessage("$prefix /jnr add <name> <difficultyId> <material> <resetHeight>")
                sender.sendMessage("$prefix /jnr remove <id>")
                sender.sendMessage("$prefix /jnr reset <id> <uuid>")
            }

            when (args[0].lowercase(Locale.getDefault())) {
                "list" -> {
                    if (plugin.parkourManager.parkours.isEmpty())
                        sender.sendMessage("$prefix Es sind keine Parcours vorhanden!")
                    else
                        plugin.parkourManager.parkours.forEach {
                            sender.sendMessage("$prefix #${it.id}: ${it.name} - ${it.difficulty}")
                    }
                }
                "add" -> {
                    if (sender !is Player)
                        sender.sendMessage("$prefix Dieser Befehl kann nur als Spieler ausgeführt werden!")
                    else if (args.size != 5)
                        sender.sendMessage("$prefix /jnr add <name> <difficultyId> <material> <resetHeight>")
                    else {
                        try {
                            val location = sender.location
                            val id = plugin.parkourManager.getMaxId() ?: -1
                            val name = args[1]
                            val difficulty = Difficulty.getById(args[2].toInt())
                            val material = Material.getMaterial(args[3])
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

                            sender.sendMessage("$prefix Parkour erfolgreich hinzugefügt")
                        } catch (e: Exception) {
                            sender.sendMessage(e.message)
                        }
                    }
                }
                "remove" -> {
                    if (args.size != 2)
                        sender.sendMessage("$prefix /jnr remove <id>")
                    else {
                        try {
                            val id = args[1].toInt()

                            if (plugin.parkourManager.hasParkour(id)) {
                                plugin.parkourManager.removeParkour(id)
                                sender.sendMessage("$prefix Parkour erfolgreich entfernt")
                            }
                            else
                                sender.sendMessage("$prefix Dieser Parkour existiert nicht!")
                        } catch (e: Exception) {
                            sender.sendMessage(e.message)
                        }
                    }
                }
                "reset" -> {
                    if (args.size != 3)
                        sender.sendMessage("$prefix /jnr reset <id> <uuid>")
                    else {
                        try {
                            val id = args[1].toInt()
                            val uuid = UUID.fromString(args[2])

                            if (SQLQueries.hasPersonalBestTime(uuid, id))
                                SQLQueries.resetBestTime(uuid, id)
                            else
                                sender.sendMessage("$prefix Dieser Spieler hat keine Bestzeit!")
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