package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.ui.EventUiSystem
import com.pipai.dragontiles.data.EventLocalization
import com.pipai.dragontiles.dungeon.RunData

class EventApi(val game: DragonTilesGame, val runData: RunData, val sUi: EventUiSystem, val event: EventLocalization) {

    private fun keyText(id: String): String {
        return event.keyText[id] ?: throw IllegalStateException("Event $id not found")
    }

    fun changeMainText(textId: String) {
        sUi.setMainText(keyText(textId))
    }

    fun changeOptions(options: List<EventOption>) {
        options.forEach {
            sUi.addOption(keyText(it.id), it)
        }
    }

    fun allowMapAdvance() {

    }

    fun showMap() {

    }

}
