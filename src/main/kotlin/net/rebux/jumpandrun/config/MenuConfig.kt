package net.rebux.jumpandrun.config

object MenuConfig : CustomConfiguration("menu.yml") {

    val parkoursPerPage = config.getInt("parkoursPerPage")
    val inventoryTile = getNonNullableString("title")
    val nextPageTitle = getNonNullableString("nextPage")
    val previousPageTitle = getNonNullableString("previousPage")

    val leaderboardItem = config.getBoolean("leaderboardItem")

    /**
     * Represents the lore of a parkour item in the menu inventory
     */
    internal object Entry {

        val difficulty = getNonNullableString("entry.difficulty")
        val builder = getNonNullableString("entry.builder")
        val noTime = getNonNullableString("entry.noTime")

        internal object PersonalBest {
            val title = getNonNullableString("entry.personalBest.title")
            val time = getNonNullableString("entry.personalBest.time")
        }

        internal object GlobalBest {
            val title = getNonNullableString("entry.globalBest.title")
            val subtitle = getNonNullableString("entry.globalBest.subtitle")
            val time = getNonNullableString("entry.globalBest.time")
            val player = getNonNullableString("entry.globalBest.player")
        }
    }

    private fun getNonNullableString(path: String): String {
        return config.getString(path)
            ?: error("Could not find config entry for $path")
    }
}
