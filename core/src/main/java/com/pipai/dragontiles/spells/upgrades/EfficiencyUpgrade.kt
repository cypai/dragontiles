package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.FluxGainAspect
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.findAs

class EfficiencyUpgrade : SpellUpgrade {
    override val name: String = "Efficiency Upgrade"
    override val assetName: String = "efficiency.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell.aspects.any { it is FluxGainAspect && it.amount > 1 }
    }

    override fun onUpgrade(spell: Spell) {
        val aspect = spell.aspects.findAs(FluxGainAspect::class)!!
        aspect.amount /= 2
        if (aspect.amount == 0) {
            aspect.amount = 1
        }
    }
}
