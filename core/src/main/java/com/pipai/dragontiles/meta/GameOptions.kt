package com.pipai.dragontiles.meta

data class GameOptions(
    var musicVolume: Float,
    var soundVolume: Float,
    val disabledKeywords: MutableList<String>,
)
