package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.utils.choose

class ShinyInAHole : DungeonEvent() {

    override val id = "base:events:ShinyInAHole"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(CheckOption(), SkipOption())

    override fun onEventStart(api: EventApi) {
        api.allowMapAdvance()
    }

    private class CheckOption : EventOption {
        override val id = "check"

        private fun hpLoss(api: EventApi): Int = (api.runData.hero.hpMax * 0.2f).toInt()

        override fun additionalText(api: EventApi): String {
            return "Gain a random relic. Lose ${hpLoss(api)} HP."
        }

        override fun onSelect(api: EventApi) {
            api.gainHpImmediate(-hpLoss(api))
            api.gainRelicImmediate(api.runData.relicData.availableRelics.choose(api.runData.rng))
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
