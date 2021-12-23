package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.spells.Spell

interface SpellUpgrade : Localized {
    val assetName: String
    fun canUpgrade(spell: Spell): Boolean
    fun onUpgrade(spell: Spell)
}

