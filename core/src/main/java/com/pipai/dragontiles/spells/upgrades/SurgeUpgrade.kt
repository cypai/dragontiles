package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.AttackDamageAspect
import com.pipai.dragontiles.spells.PostExhaustAspect
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.findAs

class SurgeUpgrade : SpellUpgrade {
    override val strId: String = "base:upgrades:SurgeUpgrade"
    override val assetName: String = "power.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell.aspects.any { it is AttackDamageAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.findAs(AttackDamageAspect::class)!!.amount += 12
        if (spell.aspects.none { it is PostExhaustAspect }) {
            spell.aspects.add(PostExhaustAspect())
        }
    }
}