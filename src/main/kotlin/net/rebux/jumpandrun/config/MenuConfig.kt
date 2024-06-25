package net.rebux.jumpandrun.config

object MenuConfig : CustomConfiguration("menu.yml") {

  val parkoursPerPage = config.getInt("parkoursPerPage")
}
