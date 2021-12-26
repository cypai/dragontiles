package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class SwapUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:SwapUpgrade"
    override val rarity: Rarity = Rarity.RARE
    override val assetName: String = "swap.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell is StandardSpell || spell is PowerSpell || spell.aspects.any { it is SwapAspect }
    }

    override fun onUpgrade(spell: Spell) {
        val aspect = spell.aspects.findAs(SwapAspect::class)
        if (aspect == null) {
            spell.aspects.add(SwapAspect(1))
        } else {
            aspect.amount++
        }
    }
}
