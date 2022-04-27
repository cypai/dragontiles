package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Keywords
import com.pipai.dragontiles.spells.*

class ScoreUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:ScoreUpgrade"
    override val rarity: Rarity = Rarity.RARE
    override val assetName: String = "score.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell is StandardSpell && spell.aspects.none { it is CountdownAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.add(CountdownAspect.generateScoreCountdown(17))
    }

    private suspend fun callback(api: CombatApi) {
        api.score()
    }
}
