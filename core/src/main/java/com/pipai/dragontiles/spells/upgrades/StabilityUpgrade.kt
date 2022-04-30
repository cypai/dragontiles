package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.Stable
import com.pipai.dragontiles.spells.StandardSpell

class StabilityUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:StabilityUpgrade"
    override val rarity: Rarity = Rarity.COMMON
    override val assetName: String = "stability.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell is StandardSpell
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.add(Stable())
    }
}
