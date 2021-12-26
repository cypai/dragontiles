package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.Heatsink
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.StandardSpell

class HeatsinkUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:HeatsinkUpgrade"
    override val price: Int = 3
    override val assetName: String = "heatsink.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell is StandardSpell
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.add(Heatsink())
    }
}
