package com.pipai.dragontiles.dungeon

import com.pipai.dragontiles.spells.Spell

open class GlobalApi(private val runData: RunData) {
    fun addSpellToDeck(spell: Spell) {
        runData.hero.spells.add(spell)
    }

    fun gainGold(gold: Int) {
        runData.hero.gold += gold
    }
}
