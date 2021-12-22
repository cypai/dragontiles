package com.pipai.dragontiles.hero

import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.potions.ExplosivePotion
import com.pipai.dragontiles.potions.HealingPotion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.sorceries.Sorcery
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.choose

interface HeroClass {
    val strId: String
    val assetName: String

    val startingRelic: Relic
    val classRelics: List<Relic>
    val starterDeck: List<Spell>
    val spells: List<Spell>
    val hpMax: Int
    val fluxMax: Int
    val startingGold: Int
    val handSize: Int
    val activeSpellSize: Int
    val sideboardSize: Int
    val sorceriesSize: Int
    val potionSlotSize: Int

    fun generateHero(name: String): Hero {
        val hero = Hero(
            this, name, hpMax, hpMax, 0, fluxMax, handSize,
            mutableListOf(), activeSpellSize,
            mutableListOf(), sideboardSize,
            mutableListOf(), sorceriesSize,
            mutableListOf(startingRelic),
            startingGold,
            mutableListOf(),
        )
        hero.spells.addAll(starterDeck.filter { it !is Sorcery }.map { it.newClone() })
        hero.sorceries.addAll(starterDeck.filterIsInstance<Sorcery>().map { it.newClone() as Sorcery })
        repeat(potionSlotSize) {
            hero.potionSlots.add(PotionSlot(null))
        }
        hero.potionSlots[0].potion = ExplosivePotion()
        hero.potionSlots[1].potion = HealingPotion()
        return hero
    }

    fun getRandomClassSpells(runData: RunData, amount: Int): List<Spell> {
        val spells = spells.shuffled().toMutableList()
        val rewards: MutableList<Spell> = mutableListOf()
        repeat(amount) {
            val rarityRoll = runData.rng.nextInt(20)
            val rarity = when {
                rarityRoll == 0 -> Rarity.RARE
                rarityRoll < 6 -> Rarity.UNCOMMON
                else -> Rarity.COMMON
            }
            val spell = spells.firstOrNull { it.rarity == rarity } ?: spells.choose(runData.rng)
            spells.remove(spell)
            rewards.add(spell)
        }
        return rewards
    }
}
