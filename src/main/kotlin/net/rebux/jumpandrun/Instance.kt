package net.rebux.jumpandrun

// TODO: Find a solution to remove this
class Instance(javaPlugin: Plugin) {
  init {
    plugin = javaPlugin
  }

  companion object {
    lateinit var plugin: Plugin
  }
}
