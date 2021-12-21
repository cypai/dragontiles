package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.spells.SpellType
import com.pipai.dragontiles.utils.choose

class ThornedBush : DungeonEvent() {

    override val id = "base:events:ThornedBush"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(DigOption(), SkipOption())

    override fun onEventStart(api: EventApi) {
        api.allowMapAdvance()
    }

    private class DigOption : EventOption {
        override val id = "dig"

        override fun onSelect(api: EventApi) {
            api.gainHpImmediate(-api.runData.hero.hpMax / 10)
            val spell = api.game.heroSpells.elementalistSpells().filter { it.type != SpellType.SORCERY }
                .choose(api.runData.rng)
            println(spell)
            api.addSpellToDeck(spell)
            val sorcery = api.game.heroSpells.elementalistSpells().filter { it.type == SpellType.SORCERY }
                .choose(api.runData.rng)
            println(sorcery)
            api.addSpellToDeck(sorcery)
            api.changeToEventEnd("digMain")
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
