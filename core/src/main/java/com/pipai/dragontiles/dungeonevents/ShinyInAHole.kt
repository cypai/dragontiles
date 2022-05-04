package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.utils.choose

class ShinyInAHole : DungeonEvent() {

    override val id = "base:events:ShinyInAHole"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(CheckOption(), SkipOption())

    private class CheckOption : EventOption {
        override val id = "check"

        private fun hpLoss(api: EventApi): Int = (api.runData.hero.hpMax * 0.2f).toInt()

        override fun params(api: EventApi): EventParams {
            return EventParams(listOf(hpLoss(api)), listOf())
        }

        override fun onSelect(api: EventApi) {
            api.gainHpImmediate(-hpLoss(api))
            val relic = api.gameData.getRelic(api.runData.availableRelics.choose(api.runData.seed.relicRng()))
            api.gainRelicImmediate(relic)
            api.changeToEventEnd("checkResult")
        }
    }

    private class SkipOption : EventOption {
        override val id = "skip"

        override fun onSelect(api: EventApi) {
            api.allowMapAdvance()
            api.showMap()
        }
    }
}
