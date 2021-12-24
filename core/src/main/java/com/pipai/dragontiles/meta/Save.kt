package com.pipai.dragontiles.meta

import com.pipai.dragontiles.data.RunData

data class Save(
    var currentRun: RunData?,
    var honor: Int,
    val options: GameOptions,
    var requireTutorial: Boolean,
)
