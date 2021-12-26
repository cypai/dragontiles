package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.Groundwire
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.StandardSpell

class GroundwireUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:GroundwireUpgrade"
    override val rarity: Rarity = Rarity.COMMON
    override val assetName: String = "groundwire.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell is StandardSpell
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.add(Groundwire())
    }
}
