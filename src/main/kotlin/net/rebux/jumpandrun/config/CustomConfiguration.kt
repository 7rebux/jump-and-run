package net.rebux.jumpandrun.config

import net.rebux.jumpandrun.Plugin
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader

abstract class CustomConfiguration(private val fileName: String) {

  private lateinit var file: File
  protected lateinit var config: FileConfiguration

  fun createOrLoad(plugin: Plugin) {
    this.file = File(plugin.dataFolder, fileName)

    if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdir()
    if (!file.exists()) file.createNewFile()

    this.config = YamlConfiguration.loadConfiguration(file)

    // Set default values
    with(InputStreamReader(this.javaClass.getResourceAsStream("/$fileName")!!, "UTF8")) {
      config.setDefaults(YamlConfiguration.loadConfiguration(this))
    }

    config.options().copyDefaults(true)
    config.save(file)
  }
}
