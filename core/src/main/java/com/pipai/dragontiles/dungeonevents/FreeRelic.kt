package com.pipai.dragontiles.dungeonevents

class FreeRelic : DungeonEvent() {

    override val id = "base:events:FreeRelic"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(TakeOption(), SkipOption())

    private class TakeOption : EventOption {
        override val id = "take"

        override fun onSelect(api: EventApi) {
            api.gainRelicImmediate(api.randomRelic())
            api.changeToEventEnd("takeMain")
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
