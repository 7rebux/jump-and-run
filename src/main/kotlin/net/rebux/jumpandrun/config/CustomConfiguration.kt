package net.rebux.jumpandrun.config

import net.rebux.jumpandrun.ParkourInstance
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader

abstract class CustomConfiguration(fileName: String) {

    private val plugin = ParkourInstance.plugin
    private var file: File = File(plugin.dataFolder, fileName)
    protected var config: FileConfiguration

    init {
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
