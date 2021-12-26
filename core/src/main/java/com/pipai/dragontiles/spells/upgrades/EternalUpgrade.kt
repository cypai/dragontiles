package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.ExhaustAspect
import com.pipai.dragontiles.spells.FluxGainAspect
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.findAs

class EternalUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:EternalUpgrade"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val assetName: String = "eternal.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell.aspects.any { it is ExhaustAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.removeAll { it is ExhaustAspect }
    }
}
