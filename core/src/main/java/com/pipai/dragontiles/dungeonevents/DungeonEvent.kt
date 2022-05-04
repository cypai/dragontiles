package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.spells.Spell

abstract class DungeonEvent : Localized {

    abstract val beginningTextId: String
    abstract val beginningOptions: List<EventOption>
    open val beginningParams: EventParams = EventParams(listOf(), listOf())

    fun initialize(api: EventApi) {
        api.changeMainText(beginningTextId, beginningParams)
        api.changeOptions(beginningOptions)
    }

    open fun available(floorNumber: Int): Boolean = true

    open fun onEventStart(api: EventApi) {
    }

}

interface EventOption {
    val id: String
    fun available(api: EventApi): Boolean = true
    fun params(api: EventApi): EventParams = EventParams(listOf(), listOf())
    fun tooltipItem(api: EventApi): Localized? = null
    fun onSelect(api: EventApi)
}

data class EventParams(val numbers: List<Int>, val items: List<Localized>)

class FinishEventOption : EventOption {
    override val id = "next"

    override fun onSelect(api: EventApi) {
        api.allowMapAdvance()
        api.showMap()
    }
}
