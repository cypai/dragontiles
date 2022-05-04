package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.spells.upgrades.BlessUpgrade
import com.pipai.dragontiles.spells.upgrades.StabilityUpgrade

class CheapUpgrades : DungeonEvent() {

    override val id = "base:events:CheapUpgrades"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(BuyBless(), BuyStability(), PassOption())

    private class BuyBless : EventOption {
        override val id = "buy"

        override fun available(api: EventApi): Boolean {
            return api.runData.hero.gold > 1
        }

        override fun params(api: EventApi): EventParams {
            return EventParams(listOf(), listOf(BlessUpgrade()))
        }

        override fun tooltipItem(api: EventApi): Localized {
            return BlessUpgrade()
        }

        override fun onSelect(api: EventApi) {
            api.gainGoldImmediate(-2)
            api.queryUpgradeSpell(BlessUpgrade())
            api.changeMainText("buyMain")
            api.changeOptions(listOf(BuyBless(), BuyStability(), MoveOnOption()))
        }
    }

    private class BuyStability : EventOption {
        override val id = "buy"

        override fun available(api: EventApi): Boolean {
            return api.runData.hero.gold > 1
        }

        override fun params(api: EventApi): EventParams {
            return EventParams(listOf(), listOf(StabilityUpgrade()))
        }

        override fun tooltipItem(api: EventApi): Localized {
            return StabilityUpgrade()
        }

        override fun onSelect(api: EventApi) {
            api.gainGoldImmediate(-2)
            api.queryUpgradeSpell(StabilityUpgrade())
            api.changeMainText("buyMain")
            api.changeOptions(listOf(BuyBless(), BuyStability(), MoveOnOption()))
        }
    }

    private class PassOption : EventOption {
        override val id = "pass"

        override fun onSelect(api: EventApi) {
            api.allowMapAdvance()
            api.showMap()
        }
    }

    private class MoveOnOption : EventOption {
        override val id = "moveOn"

        override fun onSelect(api: EventApi) {
            api.allowMapAdvance()
            api.showMap()
        }
    }
}
