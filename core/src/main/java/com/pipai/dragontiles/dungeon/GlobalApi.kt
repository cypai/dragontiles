package com.pipai.dragontiles.dungeon

import com.pipai.dragontiles.artemis.events.*
import com.pipai.dragontiles.combat.GameOverEvent
import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.sorceries.Sorcery
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.upgrades.SpellUpgrade
import net.mostlyoriginal.api.event.common.EventSystem

open class GlobalApi(private val runData: RunData, private val sEvent: EventSystem) {
    fun removeSpell(spell: Spell) {
        runData.hero.spells.remove(spell)
        runData.hero.sideboard.remove(spell)
        runData.hero.sorceries.remove(spell)
    }

    fun addSpellToDeck(spell: Spell) {
        if (spell is Sorcery) {
            if (runData.hero.sorceries.size < runData.hero.sorceriesSize) {
                runData.hero.sorceries.add(spell)
                sEvent.dispatch(SpellGainedEvent(spell))
            } else {
                sEvent.dispatch(ReplaceSpellQueryEvent(spell))
            }
        } else {
            if (runData.hero.spells.size < runData.hero.spellsSize) {
                runData.hero.spells.add(spell)
                sEvent.dispatch(SpellGainedEvent(spell))
            } else {
                addSpellToSideboard(spell)
            }
        }
    }

    fun addSpellToSideboard(spell: Spell) {
        if (runData.hero.sideboard.size < runData.hero.sideboardSize) {
            runData.hero.sideboard.add(spell)
            sEvent.dispatch(SpellGainedEvent(spell))
        } else {
            sEvent.dispatch(ReplaceSpellQueryEvent(spell))
        }
    }

    fun queryUpgradeSpell(upgrade: SpellUpgrade) {
        sEvent.dispatch(UpgradeSpellQueryEvent(upgrade))
    }

    fun queryTransformSpell() {
        sEvent.dispatch(TransformSpellQueryEvent())
    }

    fun gainRelicImmediate(relic: Relic) {
        runData.hero.relics.add(relic)
        runData.relicData.availableRelics.remove(relic)
        relic.onPickup(this)
        sEvent.dispatch(TopRowUiUpdateEvent())
    }

    fun gainGoldImmediate(gold: Int) {
        runData.hero.gold += gold
        sEvent.dispatch(TopRowUiUpdateEvent())
    }

    fun gainHpImmediate(hp: Int) {
        runData.hero.hp += hp
        if (runData.hero.hp > runData.hero.hpMax) {
            runData.hero.hp = runData.hero.hpMax
        }
        sEvent.dispatch(TopRowUiUpdateEvent())
        if (runData.hero.hp <= 0) {
            sEvent.dispatch(GameOverEvent())
        }
    }

    fun gainMaxHpImmediate(hp: Int) {
        runData.hero.hpMax += hp
        gainHpImmediate(hp)
    }

    fun gainMaxFluxImmediate(amount: Int) {
        runData.hero.fluxMax += amount
        sEvent.dispatch(TopRowUiUpdateEvent())
    }

    fun gainPotion(potion: Potion) {
        runData.hero.potions.add(potion)
        sEvent.dispatch(TopRowUiUpdateEvent())
    }

    fun removePotion(potion: Potion) {
        runData.hero.potions.remove(potion)
        sEvent.dispatch(TopRowUiUpdateEvent())
    }
}
