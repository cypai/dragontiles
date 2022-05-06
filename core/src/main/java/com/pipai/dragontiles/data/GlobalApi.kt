package com.pipai.dragontiles.data

import com.pipai.dragontiles.artemis.events.*
import com.pipai.dragontiles.combat.GameOverEvent
import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.relics.RelicInstance
import com.pipai.dragontiles.spells.Sorcery
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.SpellUpgradeInstance
import com.pipai.dragontiles.spells.upgrades.SpellUpgrade
import net.mostlyoriginal.api.event.common.EventSystem

open class GlobalApi(val gameData: GameData, val runData: RunData, private val sEvent: EventSystem) {
    fun removeSpellAtIndex(index: Int) {
        runData.hero.spells.removeAt(index)
    }

    fun removeSideboardAtIndex(index: Int) {
        runData.hero.sideboard.removeAt(index)
    }

    fun removeSorceryAtIndex(index: Int) {
        runData.hero.sorceries.removeAt(index)
    }

    fun upgradeSpellAtIndex(index: Int, upgrade: SpellUpgrade) {
        val spellInstance = runData.hero.spells[index]
        val spell = gameData.getSpell(spellInstance.id)
        if (upgrade.canUpgrade(spell)) {
            spellInstance.upgrades.add(SpellUpgradeInstance(upgrade.id))
        }
    }

    fun upgradeSideboardAtIndex(index: Int, upgrade: SpellUpgrade) {
        val spellInstance = runData.hero.sideboard[index]
        val spell = gameData.getSpell(spellInstance.id)
        if (upgrade.canUpgrade(spell)) {
            spellInstance.upgrades.add(SpellUpgradeInstance(upgrade.id))
        }
    }

    fun upgradeSorceryAtIndex(index: Int, upgrade: SpellUpgrade) {
        val spellInstance = runData.hero.sorceries[index]
        val spell = gameData.getSpell(spellInstance.id)
        if (upgrade.canUpgrade(spell)) {
            spellInstance.upgrades.add(SpellUpgradeInstance(upgrade.id))
        }
    }

    fun addSpellToDeck(spell: Spell) {
        if (spell is Sorcery) {
            if (runData.hero.sorceries.size < runData.hero.sorceriesSize) {
                runData.hero.sorceries.add(spell.toInstance())
                sEvent.dispatch(SpellGainedEvent(spell))
            } else {
                sEvent.dispatch(ReplaceSpellQueryEvent(spell))
            }
        } else {
            if (runData.hero.spells.size < runData.hero.spellsSize) {
                runData.hero.spells.add(spell.toInstance())
                sEvent.dispatch(SpellGainedEvent(spell))
            } else {
                addSpellToSideboard(spell)
            }
        }
    }

    fun addSpellToSideboard(spell: Spell) {
        if (runData.hero.sideboard.size < runData.hero.sideboardSize) {
            runData.hero.sideboard.add(spell.toInstance())
            sEvent.dispatch(SpellGainedEvent(spell))
        } else {
            sEvent.dispatch(ReplaceSpellQueryEvent(spell))
        }
    }

    fun queryUpgradeSpell(upgrade: SpellUpgrade) {
        sEvent.dispatch(UpgradeSpellQueryEvent(upgrade, DeckQueryType.SKIPPABLE, {}, {}))
    }

    fun queryTransformSpell() {
        sEvent.dispatch(TransformSpellQueryEvent())
    }

    fun gainRelicImmediate(relic: Relic) {
        runData.hero.relicIds.add(RelicInstance(relic.id, 0))
        runData.availableRelics.remove(relic.id)
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
        runData.hero.tempFluxMax += amount
        runData.hero.fluxMax += amount
        sEvent.dispatch(TopRowUiUpdateEvent())
    }

    fun gainPotion(potion: Potion) {
        runData.hero.potionSlots.firstOrNull { it.potionId == null }?.potionId = potion.id
        sEvent.dispatch(TopRowUiUpdateEvent())
    }

    fun removePotionAtIndex(index: Int) {
        runData.hero.potionSlots[index].potionId = null
        sEvent.dispatch(TopRowUiUpdateEvent())
    }

    fun usePotion(index: Int) {
        val potionSlot = runData.hero.potionSlots[index]
        val potion = gameData.getPotion(potionSlot.potionId!!)
        potionSlot.potionId = null
        potion.onNonCombatUse(this)
    }

    fun gainPotionSlots(amount: Int) {
        repeat(amount) {
            runData.hero.potionSlots.add(PotionSlot(null))
        }
        sEvent.dispatch(TopRowUiUpdateEvent())
    }

    fun updateRelicCounter(relic: Relic) {
        runData.hero.relicIds.first { it.id == relic.id }.counter = relic.counter
        updateTopRow()
    }

    fun updateTopRow() {
        sEvent.dispatch(TopRowUiUpdateEvent())
    }
}
