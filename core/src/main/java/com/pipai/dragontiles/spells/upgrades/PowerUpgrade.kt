package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.AttackDamageAspect
import com.pipai.dragontiles.spells.FluxGainAspect
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.findAs

class PowerUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:PowerUpgrade"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val assetName: String = "power.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell.aspects.any { it is AttackDamageAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.findAs(AttackDamageAspect::class)!!.amount += 3
        val fluxGainAspect = spell.aspects.findAs(FluxGainAspect::class)
        if (fluxGainAspect == null) {
            spell.aspects.add(FluxGainAspect(1))
        } else {
            fluxGainAspect.amount++
        }
    }
}
