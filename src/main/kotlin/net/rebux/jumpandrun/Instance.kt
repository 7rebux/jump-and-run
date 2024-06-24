package net.rebux.jumpandrun

// TODO: Try to get rid of this as much as possible
class Instance(javaPlugin: Plugin) {
  init {
    plugin = javaPlugin
  }

  companion object {
    lateinit var plugin: Plugin
  }
}
