package com.pipai.dragontiles.meta

import com.badlogic.gdx.files.FileHandle
import com.pipai.dragontiles.dungeon.RunData
import kotlinx.serialization.Serializable

data class SaveFile(
    val fileHandle: FileHandle,
    val save: Save
) {
    init {
        if (fileHandle.exists()) {
            // do stuff
        } else {
            // create file
        }
    }
}

data class Save(
    var currentRun: RunData?,
    var honor: Int,
    val options: GameOptions,
    var requireTutorial: Boolean,
)
