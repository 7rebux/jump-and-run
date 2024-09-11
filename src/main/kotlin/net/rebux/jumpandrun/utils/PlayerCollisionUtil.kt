package net.rebux.jumpandrun.utils

import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

object PlayerCollisionUtil {

    private const val ANTI_COLLISION_TEAM_NAME = "NO_COLLISIONS"

    fun Scoreboard.addPlayerToAntiCollisionTeam(player: Player) {
        try {
            if (this.getTeam(ANTI_COLLISION_TEAM_NAME) == null) {
                this.registerNewTeam(ANTI_COLLISION_TEAM_NAME)
                    .setOption(
                        Team.Option.COLLISION_RULE,
                        Team.OptionStatus.NEVER
                    )
            }

            this.getTeam(ANTI_COLLISION_TEAM_NAME)!!.addPlayer(player)
        } catch (_: NoClassDefFoundError) {
            // No fallback needed since there is no player collision in older versions (right?)
        }
    }
}
