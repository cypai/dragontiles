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

        private fun hpLoss(api: EventApi): Int = (api.runData.hero.hpMax * 0.15f).toInt()

        override fun additionalText(api: EventApi): String {
            return "Lose ${hpLoss(api)} HP."
        }

        override fun onSelect(api: EventApi) {
            api.gainHpImmediate(-hpLoss(api))
            val spell = api.game.heroSpells.elementalistSpells().filter { it.type != SpellType.SORCERY }
                .choose(api.runData.rng)
            api.addSpellToDeck(spell)
            val sorcery = api.game.heroSpells.elementalistSpells().filter { it.type == SpellType.SORCERY }
                .choose(api.runData.rng)
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
