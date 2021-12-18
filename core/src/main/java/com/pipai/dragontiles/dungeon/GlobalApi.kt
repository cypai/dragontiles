package com.pipai.dragontiles.dungeon

import com.pipai.dragontiles.artemis.events.GoldChangeEvent
import com.pipai.dragontiles.artemis.events.ReplaceSpellQueryEvent
import com.pipai.dragontiles.spells.Spell
import net.mostlyoriginal.api.event.common.EventSystem

open class GlobalApi(private val runData: RunData, private val sEvent: EventSystem) {
    fun addSpellToDeck(spell: Spell) {
        if (runData.hero.spells.size < runData.hero.spellsSize) {
            runData.hero.spells.add(spell)
        } else {
            addSpellToSideboard(spell)
        }
    }

    fun addSpellToSideboard(spell: Spell) {
        println("add to sideboard")
        if (runData.hero.sideDeck.size < runData.hero.sideDeckSize) {
            runData.hero.sideDeck.add(spell)
        } else {
            sEvent.dispatch(ReplaceSpellQueryEvent(spell))
        }
    }

    fun gainGold(gold: Int) {
        runData.hero.gold += gold
        sEvent.dispatch(GoldChangeEvent(gold))
    }
}
