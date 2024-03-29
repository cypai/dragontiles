package com.pipai.dragontiles.dungeonevents

class TheBeggar : DungeonEvent() {

    override val id = "base:events:TheBeggar"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(Offer1(), Offer3(), SkipOption())

    private class Offer1 : EventOption {
        override val id = "offer1"

        override fun available(api: EventApi): Boolean {
            return api.runData.hero.gold > 0
        }

        private fun hpGain(api: EventApi): Int = (api.runData.hero.hpMax * 0.25f).toInt()

        override fun params(api: EventApi): EventParams {
            return EventParams(listOf(hpGain(api)), listOf())
        }

        override fun onSelect(api: EventApi) {
            api.gainGoldImmediate(-1)
            api.gainHpImmediate(hpGain(api))
            api.changeToEventEnd("offer1Main")
        }
    }

    private class Offer3 : EventOption {
        override val id = "offer3"

        override fun available(api: EventApi): Boolean {
            return api.runData.hero.gold > 2
        }

        override fun onSelect(api: EventApi) {
            api.gainGoldImmediate(-3)
            api.runData.hero.sideboardSize++
            api.changeToEventEnd("offer3Main")
        }
    }

    private class SkipOption : EventOption {
        override val id = "skip"

        override fun onSelect(api: EventApi) {
            api.changeToEventEnd("skipMain")
        }
    }
}
