package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.spells.common.Mulligan

class StrangeScribe : DungeonEvent() {

    override val id = "base:events:StrangeScribe"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(Transform(), Buy(), SkipOption())

    private class Transform : EventOption {
        override val id = "transform"

        override fun available(api: EventApi): Boolean {
            return api.runData.hero.gold > 0
        }

        override fun onSelect(api: EventApi) {
            api.gainGoldImmediate(-1)
            api.queryTransformSpell()
            api.changeToEventEnd("afterMain")
        }
    }

    private class Buy : EventOption {
        override val id = "buy"

        override fun available(api: EventApi): Boolean {
            return api.runData.hero.gold > 0
        }

        override fun onSelect(api: EventApi) {
            api.gainGoldImmediate(-1)
            api.addSpellToDeck(Mulligan())
            api.changeToEventEnd("afterMain")
        }
    }

    private class SkipOption : EventOption {
        override val id = "skip"

        override fun onSelect(api: EventApi) {
            api.changeToEventEnd("skipMain")
        }
    }
}
