package net.rebux.jumpandrun.api

import net.rebux.jumpandrun.parkour.Parkour

enum class MenuSorting(val comparator: Comparator<Parkour>) {
    Difficulty(compareBy(Parkour::difficulty, Parkour::name)),
    Name(compareBy(Parkour::name)),
    Completions(compareByDescending { it.times.size }),
    Time(compareBy{ it.times.values.minOrNull() ?: Long.MAX_VALUE }),
    Records(compareByDescending{ parkour ->
        val record = parkour.times.values.minOrNull() ?: 0
        parkour.times.values.count { it == record }
    })
}
