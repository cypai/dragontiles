package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class ScryUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:ScryUpgrade"
    override val rarity: Rarity = Rarity.COMMON
    override val assetName: String = "scry.png"

    override fun canUpgrade(spell: Spell): Boolean {
        val scryAspect = spell.aspects.findAs(ScryAspect::class)
        if (scryAspect != null) {
            return scryAspect.amount != null
        }
        return spell is StandardSpell || spell is PowerSpell
    }

    override fun onUpgrade(spell: Spell) {
        val aspect = spell.aspects.findAs(ScryAspect::class)
        if (aspect == null) {
            spell.aspects.add(ScryAspect(1))
        } else {
            aspect.amount = aspect.amount ?: 0 + 1
        }
    }
}
