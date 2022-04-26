package com.pipai.dragontiles.dungeonevents

class MountainsStartEvent : DungeonEvent() {

    override val id = "base:events:MountainsStartEvent"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(Option())

    override fun onEventStart(api: EventApi) {
        api.allowMapAdvance()
    }

    private class Option : EventOption {
        override val id = "option"

        override fun onSelect(api: EventApi) {
            api.showMap()
        }
    }

}
