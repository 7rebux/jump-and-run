package net.rebux.jumpandrun.config

/**
 * We only store messages inside this config which can be seen by players without any special
 * permissions.
 */
object MessagesConfig : CustomConfiguration("messages.yml") {

    val prefix = getMessage("prefix")

    internal object Event {
        val checkpoint = getMessage("event.checkpoint")
        val resetBlock = getMessage("event.resetBlock")
        val completed = getMessage("event.completed")
        val globalBest = getMessage("event.globalBest")
        val firstGlobalBest = getMessage("event.firstGlobalBest")
        val personalBest = getMessage("event.personalBest")
    }

    internal object Command {
        internal object Top {
            val invalid = getMessage("command.top.invalid")
            val empty = getMessage("command.top.empty")
            val header = getMessage("command.top.header")
            val entry = getMessage("command.top.entry")
        }

        internal object Practice {
            val enabled = getMessage("command.practice.enabled")
            val disabled = getMessage("command.practice.disabled")
        }
    }

    internal object Item {
        internal object Hider {
            val hidePlayers = getMessage("item.hider.hidePlayers")
            val showPlayers = getMessage("item.hider.showPlayers")
        }
    }

    internal object Timer {

        val bar = getMessage("timer.bar")

        internal object Unit {
            val seconds = getMessage("timer.unit.seconds")
            val minutes = getMessage("timer.unit.minutes")
        }
    }

    private fun getMessage(path: String): String {
        return config.getString(path) ?: error("Could not find config entry for message $path")
    }
}
