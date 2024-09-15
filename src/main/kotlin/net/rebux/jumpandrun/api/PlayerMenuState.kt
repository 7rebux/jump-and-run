package net.rebux.jumpandrun.api

data class PlayerMenuState(
    var page: Int = 0,
    var category: MenuCategory = MenuCategory.All,
    var sorting: MenuSorting = MenuSorting.Difficulty,
)
