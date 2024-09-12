package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.ParkourInstance
import net.rebux.jumpandrun.config.WebhookConfig
import net.rebux.jumpandrun.parkour.Parkour
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bukkit.entity.Player
import java.util.logging.Level

object DiscordWebhook {

    private val plugin = ParkourInstance.plugin
    private val client = OkHttpClient()

    fun postGlobalBest(
        player: Player,
        parkour: Parkour,
        previousHolders: String,
        time: String,
        deltaTime: String
    ) {
        val json = """
            {
                "embeds": [
                    {
                        "title": "New Record",
                        "color": ${parkour.difficulty.rgbColor},
                        "fields": [
                            {
                                "name": "Module",
                                "value": "${parkour.difficulty.displayName} ${parkour.name}"
                            },
                            {
                                "name": "Time",
                                "value": "$time (-$deltaTime)"
                            },
                            {
                                "name": "Player",
                                "value": "${player.name}"
                            },
                            {
                                "name": "Previous Holders",
                                "value": "$previousHolders"
                            }
                        ]
                    }
                ]
            }""".trimMargin()
        val request = Request.Builder()
            .url(WebhookConfig.url)
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                plugin.logger.log(
                    Level.WARNING,
                    "Failed to report new global best to discord webhook: ${response.code} ${response.body?.string()}"
                )
                plugin.logger.log(Level.WARNING, json)
                return
            }

            plugin.logger.log(
                Level.INFO,
                "Successfully reported new global best to discord webhook with status ${response.code}"
            )
        }
    }
}
