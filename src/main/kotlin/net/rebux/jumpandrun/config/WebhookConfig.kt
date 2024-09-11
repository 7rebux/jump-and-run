package net.rebux.jumpandrun.config

object WebhookConfig : CustomConfiguration("webhook.yml") {

    val enabled = config.getBoolean("enabled")
    val url = config.getString("url") ?: ""
}
