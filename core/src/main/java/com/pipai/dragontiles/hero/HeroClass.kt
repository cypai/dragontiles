package com.pipai.dragontiles.hero

import com.pipai.dragontiles.data.Hero
import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.data.PotionSlot
import com.pipai.dragontiles.data.Seed
import com.pipai.dragontiles.potions.ExplosivePotion
import com.pipai.dragontiles.potions.HealingPotion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Sorcery
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.SpellInstance
import com.pipai.dragontiles.utils.choose

interface HeroClass : Localized {
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
            id, name, hpMax, hpMax, 0, fluxMax, handSize,
            mutableListOf(), activeSpellSize,
            mutableListOf(), sideboardSize,
            mutableListOf(), sorceriesSize,
            mutableListOf(startingRelic.toInstance()),
            startingGold,
            mutableListOf(),
        )
        hero.spells.addAll(starterDeck.filter { it !is Sorcery }.map { SpellInstance(it.id, mutableListOf()) })
        hero.sorceries.addAll(starterDeck.filterIsInstance<Sorcery>().map { SpellInstance(it.id, mutableListOf()) })
        repeat(potionSlotSize) {
            hero.potionSlots.add(PotionSlot(null))
        }
        hero.potionSlots[0].potionId = ExplosivePotion().id
        hero.potionSlots[1].potionId = HealingPotion().id
        return hero
    }

    fun getRandomClassSpells(seed: Seed, amount: Int): List<Spell> {
        val rng = seed.rewardRng()
        val spells = spells.shuffled().toMutableList()
        val rewards: MutableList<Spell> = mutableListOf()
        repeat(amount) {
            val rarityRoll = rng.nextInt(20)
            val rarity = when {
                rarityRoll == 0 -> Rarity.RARE
                rarityRoll < 6 -> Rarity.UNCOMMON
                else -> Rarity.COMMON
            }
            val spell = spells.firstOrNull { it.rarity == rarity } ?: spells.choose(rng)
            spells.remove(spell)
            rewards.add(spell)
        }
        return rewards
    }
}
