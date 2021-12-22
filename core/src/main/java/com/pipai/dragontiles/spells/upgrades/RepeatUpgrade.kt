package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.LimitedRepeatableAspect
import com.pipai.dragontiles.spells.RepeatableAspect
import com.pipai.dragontiles.spells.Rune
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.findAs

class RepeatUpgrade : SpellUpgrade {
    override val name: String = "Repeat Upgrade"
    override val assetName: String = "repeat.png"

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
