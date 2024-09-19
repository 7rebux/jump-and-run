package net.rebux.jumpandrun.inventory.menu

data class PlayerMenuState(
    var page: Int = 0,
    var category: MenuCategory = MenuCategory.All,
    var sorting: MenuSorting = MenuSorting.Difficulty,
    var filter: MenuFilter = MenuFilter.None
)
