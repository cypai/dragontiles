package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.spells.upgrades.EfficiencyUpgrade
import com.pipai.dragontiles.spells.upgrades.PowerUpgrade
import com.pipai.dragontiles.spells.upgrades.RepeatUpgrade
import com.pipai.dragontiles.utils.choose

class UnusedSeal : DungeonEvent() {

    override val id = "base:events:UnusedSeal"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(UseOption(), SkipOption())

    private class UseOption : EventOption {
        override val id = "use"

        override fun onSelect(api: EventApi) {
            api.queryUpgradeSpell(
                listOf(
                    PowerUpgrade(),
                    RepeatUpgrade(),
                    EfficiencyUpgrade()
                ).choose(api.runData.seed.miscRng())
            )
            api.changeToEventEnd("useMain")
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
