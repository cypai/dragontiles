package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.spells.upgrades.SurgeUpgrade
import com.pipai.dragontiles.utils.choose

class StrangeLotus : DungeonEvent() {

    override val id = "base:events:StrangeLotus"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(Sit(), Poke(), SkipOption())

    private class Sit : EventOption {
        override val id = "sit"

        private fun hpLoss(api: EventApi): Int = (api.runData.hero.hpMax * 0.2f).toInt()

        override fun params(api: EventApi): EventParams {
            return EventParams(listOf(hpLoss(api)), listOf())
        }

        override fun onSelect(api: EventApi) {
            api.gainHpImmediate(-hpLoss(api))
            val relic = api.gameData.getRelic(api.runData.availableRelics.choose(api.runData.seed.relicRng()))
            api.gainRelicImmediate(relic)
            api.changeToEventEnd("sitMain")
        }
    }

    private class Poke : EventOption {
        override val id = "poke"

        private fun hpLoss(api: EventApi): Int = (api.runData.hero.hpMax * 0.1f).toInt()

        override fun params(api: EventApi): EventParams {
            return EventParams(listOf(hpLoss(api)), listOf(SurgeUpgrade()))
        }

        override fun tooltipItem(api: EventApi): Localized {
            return SurgeUpgrade()
        }

        override fun onSelect(api: EventApi) {
            api.gainHpImmediate(-hpLoss(api))
            api.changeOptions(listOf(PokeResponse()))
            api.changeMainText("pokeMain")
        }
    }

    private class PokeResponse : EventOption {
        override val id = "pokeResponse"

        override fun onSelect(api: EventApi) {
            api.queryUpgradeSpell(SurgeUpgrade())
            api.changeToEventEnd("pokeMain")
        }
    }

    private class SkipOption : EventOption {
        override val id = "skip"

        override fun onSelect(api: EventApi) {
            api.changeToEventEnd("skipMain")
        }
    }
}
