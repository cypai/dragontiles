package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.StackableAspect
import com.pipai.dragontiles.utils.findAs

class DoublestackUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:DoublestackUpgrade"
    override val rarity: Rarity = Rarity.COMMON
    override val assetName: String = "doublestack.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell.aspects.any { it is StackableAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.filterIsInstance(StackableAspect::class.java)
            .map { it.status.amount *= 2 }
    }
}
