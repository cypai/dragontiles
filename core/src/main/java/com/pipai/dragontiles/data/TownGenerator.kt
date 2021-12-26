package com.pipai.dragontiles.data

import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.choose

class TownGenerator {

    fun generate(gameData: GameData, runData: RunData) {
        val rng = runData.seed.rewardRng()
        val heroClass = gameData.getHeroClass(runData.hero.heroClassId)
        val spellShop = SpellShop(
            heroClass.getRandomClassSpells(runData.seed, 6).map { pricedSpell(it) }.toMutableList(),
            mutableListOf(),
            pricedSpell(gameData.colorlessSpells().filter { it.rarity != Rarity.SPECIAL }.choose(rng)),
        )
        val itemShop = ItemShop(mutableListOf(), mutableListOf())
        val relicRng = runData.seed.relicRng()
        repeat(3) {
            val relic = runData.availableRelics.choose(relicRng)
            itemShop.relics.add(pricedRelic(gameData.getRelic(relic)))
        }
        repeat(3) {
            val potion = gameData.allPotions().choose(rng)
            itemShop.potions.add(pricedPotion(potion))
        }
        val scribe = Scribe(mutableListOf())
        repeat(6) {
            val upgrade = gameData.allSpellUpgrades().choose(rng)
            scribe.upgrades.add(PricedItem(upgrade.id, upgrade.price))
        }
        runData.town = Town(3, null, spellShop, itemShop, scribe)
    }

    private fun pricedSpell(spell: Spell): PricedItem {
        val price = when (spell.rarity) {
            Rarity.COMMON -> 2
            Rarity.UNCOMMON -> 3
            Rarity.RARE -> 4
            else -> 0
        }
        return PricedItem(spell.id, price)
    }

    private fun pricedRelic(relic: Relic): PricedItem {
        val price = when (relic.rarity) {
            Rarity.COMMON -> 3
            Rarity.UNCOMMON -> 4
            Rarity.RARE -> 5
            else -> 0
        }
        return PricedItem(relic.id, price)
    }

    private fun pricedPotion(potion: Potion): PricedItem {
        val price = when (potion.rarity) {
            Rarity.COMMON -> 1
            Rarity.UNCOMMON -> 2
            Rarity.RARE -> 3
            else -> 3
        }
        return PricedItem(potion.id, price)
    }
}
