package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.spells.common.DragonRage
import com.pipai.dragontiles.spells.common.DragonScale

class DragonInquiryEvent : DungeonEvent() {

    override val id = "base:events:DragonInquiry"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(PowerOption(), WisdomOption(), WealthOption())

    override fun onEventStart(api: EventApi) {
        api.allowMapAdvance()
    }

    private class PowerOption : EventOption {
        override val id = "power"

        override fun onSelect(api: EventApi) {
            api.addSpellToDeck(DragonRage())
            api.changeToEventEnd("powerMain")
        }
    }

    private class WisdomOption : EventOption {
        override val id = "wisdom"

        override fun onSelect(api: EventApi) {
            api.addSpellToDeck(DragonScale())
            api.changeToEventEnd("wisdomMain")
        }
    }

    private class WealthOption : EventOption {
        override val id = "wealth"

        override fun onSelect(api: EventApi) {
            api.gainGold(5)
            api.changeToEventEnd("wealthMain")
        }
    }
}
