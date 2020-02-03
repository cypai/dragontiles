package com.pipai.dragontiles.dungeonevents

abstract class DungeonEvent {

    abstract val id: String
    abstract val beginningTextId: String
    abstract val beginningOptions: List<EventOption>

    fun initialize(api: EventApi) {
        api.changeMainText(beginningTextId)
        api.changeOptions(beginningOptions)
    }

    open fun onEventStart(api: EventApi) {
    }

}

interface EventOption {
    val id: String
    fun onSelect(api: EventApi)
}
