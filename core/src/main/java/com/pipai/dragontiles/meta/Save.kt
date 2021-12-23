package com.pipai.dragontiles.meta

import com.pipai.dragontiles.dungeon.RunData
import kotlinx.serialization.Serializable

@Serializable
data class Save(
    var currentRun: RunData?,
    var honor: Int,
    val options: GameOptions,
    var requireTutorial: Boolean,
)
