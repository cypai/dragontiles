package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.*

class BlessUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:BlessUpgrade"
    override val rarity: Rarity = Rarity.COMMON
    override val assetName: String = "bless.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell is StandardSpell || spell is PowerSpell
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.add(Blessed())
    }
}
