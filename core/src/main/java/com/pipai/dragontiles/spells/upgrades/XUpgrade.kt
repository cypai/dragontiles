package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.XAspect
import com.pipai.dragontiles.utils.findAs

class XUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:XUpgrade"
    override val assetName: String = "x.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell.aspects.any { it is XAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.findAs(XAspect::class)!!.amount += 1
    }
}
