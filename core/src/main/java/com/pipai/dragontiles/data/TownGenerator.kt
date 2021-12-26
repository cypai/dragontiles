package com.pipai.dragontiles.data

import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.upgrades.SpellUpgrade
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
            val potion = RewardGenerator().choosePotion(gameData, rng)
            itemShop.potions.add(pricedPotion(potion))
        }
        val scribe = Scribe(mutableListOf())
        repeat(6) {
            val upgrade = gameData.allSpellUpgrades().choose(rng)
            scribe.upgrades.add(pricedUpgrade(upgrade))
        }
        runData.town = Town(3, null, spellShop, itemShop, scribe)
    }

    private fun pricedSpell(spell: Spell): PricedItem {
        val price = when (spell.rarity) {
            Rarity.COMMON -> 3
            Rarity.UNCOMMON -> 5
            Rarity.RARE -> 7
            else -> 0
        }
        return PricedItem(spell.id, price)
    }

    private fun pricedRelic(relic: Relic): PricedItem {
        val price = when (relic.rarity) {
            Rarity.COMMON -> 6
            Rarity.UNCOMMON -> 9
            Rarity.RARE -> 12
            else -> 0
        }
        return PricedItem(relic.id, price)
    }

    private fun pricedPotion(potion: Potion): PricedItem {
        val price = when (potion.rarity) {
            Rarity.COMMON -> 3
            Rarity.UNCOMMON -> 4
            Rarity.RARE -> 5
            else -> 4
        }
        return PricedItem(potion.id, price)
    }

    private fun pricedUpgrade(upgrade: SpellUpgrade): PricedItem {
        val price = when (upgrade.rarity) {
            Rarity.COMMON -> 5
            Rarity.UNCOMMON -> 8
            Rarity.RARE -> 11
            else -> 4
        }
        return PricedItem(upgrade.id, price)
    }
}
