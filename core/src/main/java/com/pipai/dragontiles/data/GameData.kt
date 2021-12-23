package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeon.Dungeon
import com.pipai.dragontiles.hero.HeroClass
import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Spell

class GameData {

    companion object {
        const val BASE_POTION_CHANCE = 0.2f
    }

    private val heroClasses: MutableMap<String, HeroClass> = mutableMapOf()
    private val dungeons: MutableMap<String, Dungeon> = mutableMapOf()
    private val spells: MutableMap<String, Spell> = mutableMapOf()
    private val relics: MutableMap<String, Relic> = mutableMapOf()
    private val potions: MutableMap<String, Potion> = mutableMapOf()

    fun getHeroClass(id: String) = heroClasses[id]
    fun getDungeon(id: String) = dungeons[id]
    fun getSpell(id: String) = spells[id]
    fun getRelic(id: String) = relics[id]
    fun getPotion(id: String) = potions[id]

    fun addHeroClass(heroClass: HeroClass) {
        heroClasses[heroClass.id] = heroClass
    }

    fun addDungeon(dungeon: Dungeon) {
        dungeons[dungeon.id] = dungeon
    }

    fun addSpell(spell: Spell) {
        spells[spell.id] = spell
    }

    fun addRelic(relic: Relic) {
        relics[relic.id] = relic
    }

    fun addPotion(potion: Potion) {
        potions[potion.id] = potion
    }

}
