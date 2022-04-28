package com.pipai.dragontiles.dungeonevents

import com.pipai.dragontiles.spells.SpellType
import com.pipai.dragontiles.utils.choose

class ThornedBush : DungeonEvent() {

    override val id = "base:events:ThornedBush"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(DigOption(), SkipOption())

    private class DigOption : EventOption {
        override val id = "dig"

        private fun hpLoss(api: EventApi): Int = (api.runData.hero.hpMax * 0.25f).toInt()

        override fun additionalText(api: EventApi): String {
            return "Lose ${hpLoss(api)} HP."
        }

        override fun onSelect(api: EventApi) {
            api.gainHpImmediate(-hpLoss(api))
            val heroSpells = api.gameData.allHeroSpells(api.runData.hero.heroClassId)
            val rng = api.runData.seed.rewardRng()
            val spell = heroSpells.filter { it.type != SpellType.SORCERY }
                .choose(rng)
            api.addSpellToDeck(spell.newClone())
            val sorcery = heroSpells.filter { it.type == SpellType.SORCERY }
                .choose(rng)
            api.addSpellToDeck(sorcery.newClone())
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
