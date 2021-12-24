package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeon.Dungeon
import com.pipai.dragontiles.hero.HeroClass
import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.upgrades.SpellUpgrade

class GameData {

    companion object {
        const val BASE_POTION_CHANCE = 0.2f
        const val COLORLESS = "colorless"
    }

    private val heroClasses: MutableMap<String, HeroClass> = mutableMapOf()
    private val dungeons: MutableMap<String, Dungeon> = mutableMapOf()
    private val spells: MutableMap<String, Spell> = mutableMapOf()
    private val classSpells: MutableMap<String, MutableList<Spell>> = mutableMapOf(Pair(COLORLESS, mutableListOf()))
    private val spellUpgrades: MutableMap<String, SpellUpgrade> = mutableMapOf()
    private val relics: MutableMap<String, Relic> = mutableMapOf()
    private val potions: MutableMap<String, Potion> = mutableMapOf()

    fun allHeroClasses() = heroClasses.values
    fun allHeroSpells(heroClassId: String) = classSpells[heroClassId]!!.toList()
    fun colorlessSpells() = classSpells[COLORLESS]!!.toList()
    fun allRelics() = relics.values
    fun allSpellUpgrades() = spellUpgrades.values
    fun allPotions() = potions.values

    fun getHeroClass(id: String) = heroClasses[id]!!
    fun getDungeon(id: String) = dungeons[id]!!
    fun getSpell(id: String) = spells[id]!!.newClone()
    fun getSpellUpgrade(id: String) = spellUpgrades[id]!!
    fun getRelic(id: String) = relics[id]!!.newClone()
    fun getPotion(id: String) = potions[id]!!

    fun addHeroClass(heroClass: HeroClass) {
        heroClasses[heroClass.id] = heroClass
    }

    fun addDungeon(dungeon: Dungeon) {
        dungeons[dungeon.id] = dungeon
    }

    fun addSpell(heroClassId: String, spell: Spell) {
        spells[spell.id] = spell
        if (heroClassId !in classSpells) {
            classSpells[heroClassId] = mutableListOf()
        }
        classSpells[heroClassId]!!.add(spell)
    }

    fun addSpellUpgrade(spellUpgrade: SpellUpgrade) {
        spellUpgrades[spellUpgrade.id] = spellUpgrade
    }

    fun addRelic(relic: Relic) {
        relics[relic.id] = relic
    }

    fun addPotion(potion: Potion) {
        potions[potion.id] = potion
    }

}
