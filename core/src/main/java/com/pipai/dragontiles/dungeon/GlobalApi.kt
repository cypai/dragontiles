package com.pipai.dragontiles.dungeon

import com.pipai.dragontiles.artemis.events.GoldChangeEvent
import com.pipai.dragontiles.artemis.events.ReplaceSpellQueryEvent
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.sorceries.Sorcery
import com.pipai.dragontiles.spells.Spell
import net.mostlyoriginal.api.event.common.EventSystem

open class GlobalApi(private val runData: RunData, private val sEvent: EventSystem) {
    fun addSpellToDeck(spell: Spell) {
        if (spell is Sorcery) {
            runData.hero.sorceries.add(spell)
        } else {
            if (runData.hero.spells.size < runData.hero.spellsSize) {
                runData.hero.spells.add(spell)
            } else {
                addSpellToSideboard(spell)
            }
        }
    }

    fun addSpellToSideboard(spell: Spell) {
        if (runData.hero.sideDeck.size < runData.hero.sideDeckSize) {
            runData.hero.sideDeck.add(spell)
        } else {
            sEvent.dispatch(ReplaceSpellQueryEvent(spell))
        }
    }

    fun gainRelic(relic: Relic) {
        runData.hero.relics.add(relic)
        runData.relicData.availableRelics.remove(relic)
    }

    fun gainGold(gold: Int) {
        runData.hero.gold += gold
        sEvent.dispatch(GoldChangeEvent(gold))
    }

    fun gainHp(hp: Int) {
        runData.hero.hp += hp
        if (runData.hero.hp > runData.hero.hpMax) {
            runData.hero.hp = runData.hero.hpMax
        }
    }

    fun gainMaxHp(hp: Int) {
        runData.hero.hpMax += hp
        gainHp(hp)
    }

    fun gainMaxFlux(amount: Int) {
        runData.hero.fluxMax += amount
    }
}
