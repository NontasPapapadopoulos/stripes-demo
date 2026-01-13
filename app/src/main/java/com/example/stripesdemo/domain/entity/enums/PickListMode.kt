package com.example.stripesdemo.domain.entity.enums

enum class PickListMode(val value: String) {
    AimingDecoding("Aiming Decoding"),
    FullAreaDecoding("Full Area Decoding"),
    CentralAreaDecoding("Central Area Decoding")
}

val pickListModeCommands = mapOf(
    PickListMode.AimingDecoding to "{G3023/0/02 00 01 0C 00 04 20 00 06 41 45 41 44 45 43 32 3B 21 03}",
    PickListMode.FullAreaDecoding to "{G3023/0/02 00 01 0C 00 04 20 00 06 41 45 41 44 45 43 30 3B 23 03}",
    PickListMode.CentralAreaDecoding to "{G3023/0/02 00 01 0C 00 04 20 00 06 41 45 41 44 45 43 31 3B 22 03}"
)