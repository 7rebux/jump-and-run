package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.utils.MessageBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object HiderItem : Item("hider") {

    @Override
    override fun onInteract(player: Player) {
        if (!player.data.inParkour) {
            return
        }

        if (player.data.playersHidden) {
            Bukkit.getOnlinePlayers().forEach(player::showPlayer)
            player.data.playersHidden = false
            MessageBuilder(MessagesConfig.Item.Hider.showPlayers).buildAndSend(player)
        } else {
            Bukkit.getOnlinePlayers().forEach(player::hidePlayer)
            player.data.playersHidden = true
            MessageBuilder(MessagesConfig.Item.Hider.hidePlayers).buildAndSend(player)
        }
    }
}
