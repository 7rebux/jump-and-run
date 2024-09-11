package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.ParkourInstance
import net.rebux.jumpandrun.config.WebhookConfig
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.parkour.ParkourDifficulty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bukkit.Color
import org.bukkit.entity.Player
import java.util.logging.Level

object DiscordWebhook {

    private val plugin = ParkourInstance.plugin

    private val client = OkHttpClient()
    private val colorByDifficulty = mapOf(
        ParkourDifficulty.EASY to Color.GREEN.asRGB(),
        ParkourDifficulty.NORMAL to Color.YELLOW.asRGB(),
        ParkourDifficulty.HARD to Color.RED.asRGB(),
        ParkourDifficulty.ULTRA to Color.PURPLE.asRGB()
    )

    fun postGlobalBest(
        player: Player,
        parkour: Parkour,
        previousHolders: String,
        time: String,
        deltaTime: String
    ) {
        val json = """{"embeds":[{"title":"New Record","color":${colorByDifficulty[parkour.difficulty]},"fields":[{"name":"Module","value":"${parkour.difficulty.name} ${parkour.name}"},{"name":"Time","value":"$time (-$deltaTime)"},{"name":"Player","value":"${player.name}"},{"name":"Previous Holders","value":"$previousHolders"}]}]}"""
        val request = Request.Builder()
            .url(WebhookConfig.url)
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                plugin.logger.log(
                    Level.WARNING,
                    "Failed to report new global best to discord webhook: ${response.code}"
                )
                return
            }

            plugin.logger.log(
                Level.INFO,
                "Successfully reported new global best to discord webhook with status ${response.code}"
            )
        }
    }
}
