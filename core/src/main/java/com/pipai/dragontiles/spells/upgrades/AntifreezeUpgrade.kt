package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.TransformAspect

class AntifreezeUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:AntifreezeUpgrade"
    override val rarity: Rarity = Rarity.COMMON
    override val assetName: String = "antifreeze.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell.aspects.any { it is TransformAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.removeAll { it is TransformAspect }
    }
}
