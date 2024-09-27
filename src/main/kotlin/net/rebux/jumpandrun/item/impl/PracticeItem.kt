package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.MessageBuilder
import org.bukkit.block.Block
import org.bukkit.entity.Player

object PracticeItem : Item("practice") {

    override fun onInteract(player: Player) {
        if (player.data.inPractice) {
            player.safeTeleport(player.data.practiceData.startLocation!!)
            player.data.practiceData.timer.stop()
        }
    }

    override fun onLeftClickBlock(player: Player, block: Block) {
        if (!player.data.inPractice) {
            return
        }

        player.data.practiceData.finishPosition = block.location

        MessageBuilder("Set practice finish block").buildAndSend(player)
    }
}
