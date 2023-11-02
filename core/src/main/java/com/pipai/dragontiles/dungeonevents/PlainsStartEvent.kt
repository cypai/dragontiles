package com.pipai.dragontiles.dungeonevents

class PlainsStartEvent : DungeonEvent() {

    override val id = "base:events:PlainsStartEvent"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(ReceiveBlessing())

    private class ReceiveBlessing : EventOption {
        override val id = "receiveBlessing"

        override fun onSelect(api: EventApi) {
            api.changeMainText("receiveBlessingMain")
            api.changeOptions(listOf(ChooseSpell()))
            api.addSpellToDeck(
                api.randomSpell(api.gameData.getHeroClass(api.runData.hero.heroClassId), 0f, 0f)
                    .toSpell(api.gameData)
            )
            api.gainRelicImmediate(api.randomRelic())
            api.gainPotion(api.randomPotion())
        }
    }

    private class ChooseSpell : EventOption {
        override val id = "chooseSpell"

        override fun onSelect(api: EventApi) {
            api.changeMainText("chooseSpellMain")
            api.changeOptions(listOf(TimeToGo()))
        }
    }

    private class TimeToGo : EventOption {
        override val id = "next"

        override fun onSelect(api: EventApi) {
            api.allowMapAdvance()
            api.showMap()
        }
    }

}
