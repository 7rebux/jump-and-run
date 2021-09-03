package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.Main

object ConfigUtil {
    fun getString(name: String): String = Main.instance.mainConfig.getString(name)

    fun getInt(name: String): Int = Main.instance.mainConfig.getInt(name)
}