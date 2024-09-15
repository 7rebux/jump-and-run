package net.rebux.jumpandrun.api

import net.rebux.jumpandrun.parkour.Parkour

enum class MenuSorting(val sortFunction: (Parkour) -> Comparable<*>) {
    Difficulty(Parkour::difficulty),
    Name(Parkour::name),
    Completions({ it.times.size }),
    Time({ it.times.values.minOrNull() ?: Long.MAX_VALUE }),
    Records({ parkour ->
        val record = parkour.times.values.minOrNull() ?: 0
        parkour.times.values.count { it == record }
    })
}
