package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class SurgeUpgrade : SpellUpgrade {
    override val id: String = "base:upgrades:SurgeUpgrade"
    override val rarity: Rarity = Rarity.COMMON
    override val assetName: String = "surge.png"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell is StandardSpell && spell.aspects.any { it is AttackDamageAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.findAs(AttackDamageAspect::class)!!.amount += 12
        if (spell.aspects.none { it is ExhaustAspect } && spell.getUpgrades().none { it is EternalUpgrade }) {
            spell.aspects.add(ExhaustAspect())
        }
    }
}
