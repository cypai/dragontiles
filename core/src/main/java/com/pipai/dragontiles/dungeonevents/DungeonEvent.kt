package com.pipai.dragontiles.dungeonevents

import kotlinx.serialization.Serializable

@Serializable
abstract class DungeonEvent {

    abstract val id: String
    abstract val beginningTextId: String
    abstract val beginningOptions: List<EventOption>

    fun initialize(api: EventApi) {
        api.changeMainText(beginningTextId)
        api.changeOptions(beginningOptions)
    }

    open fun available(floorNumber: Int): Boolean = true

    open fun onEventStart(api: EventApi) {
    }

}

interface EventOption {
    val id: String
    fun available(api: EventApi): Boolean = true
    fun additionalText(api: EventApi): String = ""
    fun onSelect(api: EventApi)
}

class FinishEventOption : EventOption {
    override val id = "next"

    override fun onSelect(api: EventApi) {
        api.allowMapAdvance()
        api.showMap()
    }
}
