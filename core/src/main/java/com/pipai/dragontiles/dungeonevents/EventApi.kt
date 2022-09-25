package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.screens.CombatScreen
import com.pipai.dragontiles.artemis.systems.ui.DeckDisplayUiSystem
import com.pipai.dragontiles.artemis.systems.ui.EventUiSystem
import com.pipai.dragontiles.combat.CombatRewardConfig
import com.pipai.dragontiles.data.EventLocalization
import com.pipai.dragontiles.dungeon.Encounter
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.data.RunData
import net.mostlyoriginal.api.event.common.EventSystem

class EventApi(
    val game: DragonTilesGame,
    runData: RunData,
    sEvent: EventSystem,
    val sUi: EventUiSystem,
    private val sDeckUi: DeckDisplayUiSystem,
    val event: EventLocalization
) : GlobalApi(game.data, game.assets, runData, sEvent) {

    private val keyRegex = "(!\\w+)\\((\\w+)\\)?".toRegex()

    private fun keyText(id: String): String {
        return if (id == "next") {
            "Continue"
        } else {
            event.keyText[id] ?: throw IllegalStateException("Event $id not found")
        }
    }

    fun displayDeckUi() {
        sDeckUi.updateStandardDisplay({ true }, true)
        sDeckUi.activate()
    }

    private fun interpolateTextParams(text: String, params: EventParams): String {
        val interpolatedText = text.replace(keyRegex) {
            val key = it.groupValues[1]
            val param = it.groupValues[2].toInt()
            when (key) {
                "!n" -> params.numbers[param].toString()
                "!i" -> game.gameStrings.nameLocalization(params.items[param])
                else -> ""
            }
        }
        return interpolatedText
    }

    fun changeMainText(textId: String, params: EventParams = EventParams(listOf(), listOf())) {
        sUi.setMainText(interpolateTextParams(keyText(textId), params))
    }

    fun changeOptions(options: List<EventOption>) {
        sUi.clearOptions()
        options.forEach {
            sUi.addOption(interpolateTextParams(keyText(it.id), it.params(this)), it)
        }
    }

    fun changeToEventEnd(textId: String, params: EventParams = EventParams(listOf(), listOf())) {
        allowMapAdvance()
        changeMainText(textId, params)
        changeOptions(listOf(FinishEventOption()))
    }

    fun allowMapAdvance() {
        sUi.allowMapAdvance()
    }

    fun showMap() {
        sUi.showMap()
    }

    fun startCombat(encounter: Encounter, rewardConfig: CombatRewardConfig) {
        runData.combatWon = false
        game.screen = CombatScreen(game, runData, encounter, rewardConfig, false)
    }

}
