package net.rebux.jumpandrun.config

import org.bukkit.Material

object MenuConfig : CustomConfiguration("menu.yml") {

    val parkoursPerPage = config.getInt("parkoursPerPage")
    val inventoryTitle = getNonNullableString("title")
    val nextPageTitle = getNonNullableString("nextPage")
    val previousPageTitle = getNonNullableString("previousPage")

    val leaderboardItem = config.getBoolean("leaderboardItem")
    val categoryItems = config.getBoolean("categoryItems")

    internal object Category {

        internal object All {
            val material = config.getString("category.all.material")?.let(Material::getMaterial)
                ?: error("Material for category All not found!")
            val name = getNonNullableString("category.all.name")
        }

        internal object Easy {
            val material = config.getString("category.easy.material")?.let(Material::getMaterial)
                ?: error("Material for category Easy not found!")
            val name = getNonNullableString("category.easy.name")
        }

        internal object Normal {
            val material = config.getString("category.normal.material")?.let(Material::getMaterial)
                ?: error("Material for category Normal not found!")
            val name = getNonNullableString("category.normal.name")
        }

        internal object Hard {
            val material = config.getString("category.hard.material")?.let(Material::getMaterial)
                ?: error("Material for category Hard not found!")
            val name = getNonNullableString("category.hard.name")
        }

        internal object Ultra {
            val material = config.getString("category.ultra.material")?.let(Material::getMaterial)
                ?: error("Material for category Ultra not found!")
            val name = getNonNullableString("category.ultra.name")
        }
    }

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
