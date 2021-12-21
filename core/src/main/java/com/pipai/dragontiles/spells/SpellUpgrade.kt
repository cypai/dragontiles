package com.pipai.dragontiles.spells

import com.pipai.dragontiles.utils.findAs

interface SpellUpgrade {
    val name: String
    fun canUpgrade(spell: Spell): Boolean
    fun onUpgrade(spell: Spell)
}

class PowerUpgrade : SpellUpgrade {
    override val name: String = "Power Upgrade"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell.aspects.any { it is AttackDamageAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.findAs(AttackDamageAspect::class)!!.amount += 3
    }
}

class RepeatUpgrade : SpellUpgrade {
    override val name: String = "Repeat Upgrade"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell !is Rune
                && spell.aspects.none { it is RepeatableAspect }
    }

    override fun onUpgrade(spell: Spell) {
        val aspect = spell.aspects.findAs(LimitedRepeatableAspect::class)
        if (aspect == null) {
            spell.aspects.add(LimitedRepeatableAspect(2))
        } else {
            aspect.max += 1
        }
    }
}

class EfficiencyUpgrade : SpellUpgrade {
    override val name: String = "Efficiency Upgrade"

    override fun canUpgrade(spell: Spell): Boolean {
        return spell.aspects.any { it is FluxGainAspect }
    }

    override fun onUpgrade(spell: Spell) {
        spell.aspects.findAs(FluxGainAspect::class)!!.amount /= 2
    }
}
