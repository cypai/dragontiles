package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.spells.colorless.PotOfGreed
import com.pipai.dragontiles.spells.upgrades.FetchUpgrade
import kotlin.math.E

class WhatDoesPotOfGreedDo : DungeonEvent() {

    override val id = "base:events:WhatDoesPotOfGreedDo"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(NoOption(), DrawOption(), GoldOption())

    private class NoOption : EventOption {
        override val id = "noOption"

        override fun params(api: EventApi): EventParams {
            return EventParams(listOf(), listOf(PotOfGreed()))
        }

        override fun tooltipItem(api: EventApi): Localized {
            return PotOfGreed()
        }

        override fun onSelect(api: EventApi) {
            api.addSpellToDeck(PotOfGreed())
            api.changeToEventEnd("noMain")
        }
    }

    private class DrawOption : EventOption {
        override val id = "draw"

        override fun params(api: EventApi): EventParams {
            return EventParams(listOf(), listOf(FetchUpgrade()))
        }

        override fun tooltipItem(api: EventApi): Localized {
            return FetchUpgrade()
        }

        override fun onSelect(api: EventApi) {
            api.queryUpgradeSpell(FetchUpgrade())
            api.changeToEventEnd("drawMain")
        }
    }

    private class GoldOption : EventOption {
        override val id = "gold"

        override fun onSelect(api: EventApi) {
            api.gainGoldImmediate(5)
            api.changeToEventEnd("goldMain")
        }
    }
}
