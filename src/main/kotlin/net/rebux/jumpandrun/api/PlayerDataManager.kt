package net.rebux.jumpandrun.api

import org.bukkit.entity.Player
import java.util.UUID

object PlayerDataManager {

  private val playerData = hashMapOf<UUID, PlayerData>()

  fun add(player: Player) {
    playerData[player.uniqueId] = PlayerData()
  }

  fun remove(player: Player) {
    playerData.remove(player.uniqueId)
  }

  val Player.data
    get() = playerData[this.uniqueId]
      ?: error("Player data not found for ${this.uniqueId}")
}
