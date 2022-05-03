package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.FluxLossAspect
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.StandardSpell
import com.pipai.dragontiles.utils.findAs

class VentUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:VentUpgrade"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val assetName: String = "vent.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell is StandardSpell && spell.aspects.any { it is FluxLossAspect }
    }

    override fun onUpgrade(spell: Spell) {
        val aspect = spell.aspects.findAs(FluxLossAspect::class)!!
        aspect.amount += aspect.amount / 2
    }
}
