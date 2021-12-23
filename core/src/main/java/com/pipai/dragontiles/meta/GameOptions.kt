package com.pipai.dragontiles.meta

import kotlinx.serialization.Serializable

@Serializable
data class GameOptions(
    val disabledKeywords: MutableList<String>,
)
