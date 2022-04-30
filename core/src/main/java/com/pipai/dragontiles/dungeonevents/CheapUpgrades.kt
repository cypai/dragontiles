package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.spells.upgrades.HeatsinkUpgrade
import com.pipai.dragontiles.spells.upgrades.StabilityUpgrade

class CheapUpgrades : DungeonEvent() {

    override val id = "base:events:CheapUpgrades"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(BuyHeatsink(), BuyStability(), PassOption())

    private class BuyHeatsink : EventOption {
        override val id = "buyHeatsink"

        override fun available(api: EventApi): Boolean {
            return api.runData.hero.gold > 1
        }

        override fun onSelect(api: EventApi) {
            api.gainGoldImmediate(-2)
            api.queryUpgradeSpell(HeatsinkUpgrade())
        }
    }

    private class BuyStability : EventOption {
        override val id = "buyStability"

        override fun available(api: EventApi): Boolean {
            return api.runData.hero.gold > 1
        }

        override fun onSelect(api: EventApi) {
            api.gainGoldImmediate(-2)
            api.queryUpgradeSpell(StabilityUpgrade())
        }
    }

    private class PassOption : EventOption {
        override val id = "pass"

        override fun onSelect(api: EventApi) {
            api.allowMapAdvance()
            api.showMap()
        }
    }
}
