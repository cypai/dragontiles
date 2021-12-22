package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.FluxGainAspect
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.findAs

class EfficiencyUpgrade : SpellUpgrade {
    override val name: String = "Efficiency Upgrade"
    override val assetName: String = "efficiency.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell.aspects.any { it is FluxGainAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.findAs(FluxGainAspect::class)!!.amount /= 2
    }
}
