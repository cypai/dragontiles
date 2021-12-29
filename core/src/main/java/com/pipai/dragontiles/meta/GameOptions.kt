package com.pipai.dragontiles.meta

data class GameOptions(
    var musicVolume: Int,
    var soundVolume: Int,
    val disabledKeywords: MutableList<String>,
)
