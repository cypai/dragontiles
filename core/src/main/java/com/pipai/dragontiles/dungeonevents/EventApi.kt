package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.ui.EventUiSystem
import com.pipai.dragontiles.data.EventLocalization
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.dungeon.RunData

class EventApi(val game: DragonTilesGame, val runData: RunData, val sUi: EventUiSystem, val event: EventLocalization)
    : GlobalApi(runData) {

    private fun keyText(id: String): String {
        return if (id == "next") {
            "Continue"
        } else {
            event.keyText[id] ?: throw IllegalStateException("Event $id not found")
        }
    }

    fun changeMainText(textId: String) {
        sUi.setMainText(keyText(textId))
    }

    fun changeOptions(options: List<EventOption>) {
        sUi.clearOptions()
        options.forEach {
            sUi.addOption(keyText(it.id), it)
        }
    }

    fun changeToEventEnd(textId: String) {
        allowMapAdvance()
        changeMainText(textId)
        changeOptions(listOf(FinishEventOption()))
    }

    fun allowMapAdvance() {
        sUi.allowMapAdvance()
    }

    fun showMap() {
        sUi.showMap()
    }

}
