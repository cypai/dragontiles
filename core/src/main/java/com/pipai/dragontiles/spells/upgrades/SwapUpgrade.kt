package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.PowerSpell
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.StandardSpell
import com.pipai.dragontiles.spells.SwapAspect
import com.pipai.dragontiles.utils.findAs

class SwapUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:SwapUpgrade"
    override val price: Int = 5
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
