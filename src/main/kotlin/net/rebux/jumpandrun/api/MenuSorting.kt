package net.rebux.jumpandrun.api

import net.rebux.jumpandrun.parkour.Parkour

enum class MenuSorting(val sortFunction: (Parkour) -> Comparable<*>) {
    Difficulty(Parkour::difficulty),
}
