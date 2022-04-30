package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.*

class StabilityUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:StabilityUpgrade"
    override val rarity: Rarity = Rarity.COMMON
    override val assetName: String = "stability.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell is StandardSpell || spell is PowerSpell
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.add(Stable())
    }
}
