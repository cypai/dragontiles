package com.pipai.dragontiles.spells.upgrades

import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell

interface SpellUpgrade : Localized {
    val rarity: Rarity
    val assetName: String
    fun canUpgrade(spell: Spell): Boolean
    fun onUpgrade(spell: Spell)
}

