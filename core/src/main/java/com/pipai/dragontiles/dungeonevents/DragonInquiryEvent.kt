package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.spells.colorless.DragonRage
import com.pipai.dragontiles.spells.colorless.DragonScale

class DragonInquiryEvent : DungeonEvent() {

    override val id = "base:events:DragonInquiry"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(PowerOption(), WisdomOption(), WealthOption())

    private class PowerOption : EventOption {
        override val id = "power"

        override fun params(api: EventApi): EventParams {
            return EventParams(listOf(), listOf(DragonRage()))
        }

        override fun tooltipItem(api: EventApi): Localized {
            return DragonRage()
        }

        override fun onSelect(api: EventApi) {
            api.addSpellToDeck(DragonRage())
            api.changeToEventEnd("powerMain")
        }
    }

    private class WisdomOption : EventOption {
        override val id = "wisdom"

        override fun params(api: EventApi): EventParams {
            return EventParams(listOf(), listOf(DragonScale()))
        }

        override fun tooltipItem(api: EventApi): Localized {
            return DragonScale()
        }

        override fun onSelect(api: EventApi) {
            api.addSpellToDeck(DragonScale())
            api.changeToEventEnd("wisdomMain")
        }
    }

    private class WealthOption : EventOption {
        override val id = "wealth"

        override fun onSelect(api: EventApi) {
            api.gainGoldImmediate(5)
            api.changeToEventEnd("wealthMain")
        }
    }
}
