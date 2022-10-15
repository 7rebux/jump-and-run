package net.rebux.jumpandrun.config

import net.rebux.jumpandrun.Instance
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration

@Suppress("SpellCheckingInspection")
class PluginConfig {

    private val plugin = Instance.plugin
    private val config: FileConfiguration = plugin.config

    init {
        // values
        config.addDefault("prefix", "${ChatColor.GRAY}[${ChatColor.YELLOW}Jump&Run${ChatColor.GRAY}]")
        config.addDefault("resetHeight", 70)

        // database
        config.addDefault("database.host", "127.0.0.1")
        config.addDefault("database.port", "3306")
        config.addDefault("database.name", "database")
        config.addDefault("database.user", "username")
        config.addDefault("database.pass", "password")

        // messages
        config.addDefault("messages.timeUnitMinutes", "Minuten")
        config.addDefault("messages.parkour.completed", "Du hast das Jump and Run ${ChatColor.GREEN}{name} ${ChatColor.GRAY}in ${ChatColor.GREEN}{time} ${ChatColor.GRAY}geschafft")
        config.addDefault("messages.parkour.checkpint", "Du hast einen neuen ${ChatColor.GREEN}Checkpoint ${ChatColor.GRAY}erreicht!")
        config.addDefault("messages.parkour.firstGlobalBest", "Du hast die ${ChatColor.GREEN}Erste Globale Bestzeit ${ChatColor.GRAY}erzielt!")
        config.addDefault("messages.parkour.globalBest", "${ChatColor.GREEN}{player} ${ChatColor.GRAY}hat die Bestzeit bei ${ChatColor.GREEN}{name} ${ChatColor.GRAY}von ${ChatColor.GREEN}{holders} ${ChatColor.GRAY}um ${ChatColor.GREEN}{time} ${ChatColor.GRAY}geschlagen!")
        config.addDefault("messages.parkour.personalBest", "Du hast eine neue ${ChatColor.GREEN}persönliche Bestzeit ${ChatColor.GRAY}erzielt!")

        // commands
        config.addDefault("messages.commands.jnr.list.empty", "Es sind ${ChatColor.RED}keine ${ChatColor.GRAY}Jump and Runs vorhanden!")
        config.addDefault("messages.commands.jnr.list.full", "Es wurden ${ChatColor.GREEN}{size} ${ChatColor.GRAY}Jump and Runs gefunden:")
        config.addDefault("messages.commands.jnr.add.playersOnly", "${ChatColor.RED}Dieser Befehl kann nur als Spieler ausgeführt werden!")
        config.addDefault("messages.commands.jnr.add.success", "Jump and Run ${ChatColor.GREEN}{name} ${ChatColor.GRAY}erfolgreich hinzugefügt")
        config.addDefault("messages.commands.jnr.remove.success", "Jump and Run ${ChatColor.RED}{name} ${ChatColor.GRAY}erfolgreich entfernt")
        config.addDefault("messages.commands.jnr.remove.notFound", "${ChatColor.RED}Dieses Jump and Run existiert nicht!")
        config.addDefault("messages.commands.jnr.reset.successAll", "Erfolgreich ${ChatColor.GREEN}alle Bestzeiten für ${ChatColor.GREEN}{name} ${ChatColor.GRAY}entfernt")
        config.addDefault("messages.commands.jnr.reset.successSingle", "Erfolgreich Bestzeit für {name} ${ChatColor.GRAY}von ${ChatColor.GREEN}{player} ${ChatColor.GRAY}entfernt")
        config.addDefault("messages.commands.jnr.reset.notFound", "${ChatColor.RED}Dieser Spieler hat keine Bestzeit!")

        // difficulties
        config.addDefault("difficulty.easy", "Einfach")
        config.addDefault("difficulty.medium", "Mittel")
        config.addDefault("difficulty.hard", "Schwer")
        config.addDefault("difficulty.extreme", "Extrem")

        config.addDefault("menu.difficulty", "${ChatColor.GRAY}» ${ChatColor.YELLOW}Schwierigkeit: {difficulty}")
        config.addDefault("menu.builder", "${ChatColor.GRAY}» ${ChatColor.YELLOW}Builder: ${ChatColor.BLUE}{builder}")
        config.addDefault("menu.personalBest.title", "${ChatColor.GRAY}» ${ChatColor.GOLD}Persönliche Bestzeit:")
        config.addDefault("menu.personalBest.time", "${ChatColor.BLUE}{time}")
        config.addDefault("menu.globalBest.title", "${ChatColor.GRAY}» ${ChatColor.GOLD}Globale Bestzeit:")
        config.addDefault("menu.globalBest.time", "${ChatColor.BLUE}{time}")
        config.addDefault("menu.globalBest.subtitle", "${ChatColor.GRAY}von:")
        config.addDefault("menu.globalBest.player", "${ChatColor.BLUE}{player}")
        config.addDefault("menu.noTime", "${ChatColor.RED}--.--.---")

        // items
        config.addDefault("items.menu", "${ChatColor.GRAY}» ${ChatColor.AQUA}JumpAndRuns")
        config.addDefault("items.checkpoint", "${ChatColor.RED}Zurück zum Checkpoint")
        config.addDefault("items.restart", "${ChatColor.RED}Neustart")
        config.addDefault("items.leave", "${ChatColor.RED}Verlassen ${ChatColor.GRAY}(/spawn)")

        save()
    }

    private fun save() {
        config.options().copyDefaults(true)
        plugin.saveConfig()
    }
}
