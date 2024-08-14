package net.rebux.jumpandrun.config

object MenuConfig : CustomConfiguration("menu.yml") {

    val parkoursPerPage = config.getInt("parkoursPerPage")
    val tile = config.getString("title")!!
    val nextPage = config.getString("nextPage")!!
    val previousPage = config.getString("previousPage")!!

    internal object Entry {

        val difficulty = config.getString("entry.difficulty")!!
        val builder = config.getString("entry.builder")!!
        val noTime = config.getString("entry.noTime")!!

        internal object PersonalBest {
            val title = config.getString("entry.personalBest.title")!!
            val time = config.getString("entry.personalBest.time")!!
        }

        internal object GlobalBest {
            val title = config.getString("entry.globalBest.title")!!
            val subtitle = config.getString("entry.globalBest.subtitle")!!
            val time = config.getString("entry.globalBest.time")!!
            val player = config.getString("entry.globalBest.player")!!
        }
    }
}
