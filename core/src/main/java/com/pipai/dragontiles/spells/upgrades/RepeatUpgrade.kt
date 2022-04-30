package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class RepeatUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:RepeatUpgrade"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val assetName: String = "repeat.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell is StandardSpell
                && spell.aspects.none { it is RepeatableAspect }
    }

    override fun onUpgrade(spell: Spell) {
        val aspect = spell.aspects.findAs(LimitedRepeatableAspect::class)
        if (aspect == null) {
            spell.aspects.add(LimitedRepeatableAspect(2))
        } else {
            aspect.max += 1
        }
    }
}
