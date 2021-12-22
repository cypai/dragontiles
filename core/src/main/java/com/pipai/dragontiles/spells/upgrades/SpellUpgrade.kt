package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.spells.Spell

interface SpellUpgrade {
    val strId: String
    val assetName: String
    fun canUpgrade(spell: Spell): Boolean
    fun onUpgrade(spell: Spell)
}

