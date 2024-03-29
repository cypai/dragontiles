package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class FetchUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:FetchUpgrade"
    override val rarity: Rarity = Rarity.COMMON
    override val assetName: String = "fetch.png"

    override fun canUpgrade(spell: Spell): Boolean {
        val fetchAspect = spell.aspects.findAs(FetchAspect::class)
        if (fetchAspect != null) {
            return fetchAspect.amount != null
        }
        return spell is StandardSpell || spell is PowerSpell
    }

    override fun onUpgrade(spell: Spell) {
        val aspect = spell.aspects.findAs(FetchAspect::class)
        if (aspect == null) {
            spell.aspects.add(FetchAspect(1))
        } else {
            aspect.amount = aspect.amount ?: 0 + 1
        }
    }
}
