package com.pipai.dragontiles.data

import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.upgrades.ScoreUpgrade
import com.pipai.dragontiles.spells.upgrades.SpellUpgrade
import com.pipai.dragontiles.utils.choose
import com.pipai.dragontiles.utils.chooseAmount
import com.pipai.dragontiles.utils.removeRandom

class TownGenerator {

    fun generate(api: GlobalApi) {
        val runData = api.runData
        val gameData = api.gameData
        val rng = runData.seed.rewardRng()
        val heroClass = gameData.getHeroClass(runData.hero.heroClassId)
        val spellShop = SpellShop(
            heroClass.getRandomClassSpells(runData.seed, 6).map { pricedSpell(it) }.toMutableList(),
            gameData.colorlessSpells().filter { it.rarity != Rarity.SPECIAL }.chooseAmount(2, rng)
                .map { pricedSpell(it) }.toMutableList(),
        )
        val itemShop = ItemShop(mutableListOf(), mutableListOf())
        val relicRng = runData.seed.relicRng()
        repeat(3) {
            val relic = runData.availableRelics.choose(relicRng)
            itemShop.relics.add(pricedRelic(gameData.getRelic(relic)))
        }
        repeat(3) {
            val potion = api.randomPotion()
            itemShop.potions.add(pricedPotion(potion))
        }
        val scribe = Scribe(mutableListOf())
        val upgrades = gameData.allSpellUpgrades()
            .filter { it !is ScoreUpgrade }
            .toMutableList()
        repeat(7) {
            val upgrade = upgrades.removeRandom(rng)
            scribe.upgrades.add(pricedUpgrade(upgrade))
        }
        scribe.upgrades.add(pricedUpgrade(ScoreUpgrade()))
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
            Rarity.UNCOMMON -> 8
            Rarity.RARE -> 10
            else -> 0
        }
        return PricedItem(relic.id, price)
    }

    private fun pricedPotion(potion: Potion): PricedItem {
        val price = when (potion.rarity) {
            Rarity.COMMON -> 2
            Rarity.UNCOMMON -> 3
            Rarity.RARE -> 4
            else -> 3
        }
        return PricedItem(potion.id, price)
    }

    private fun pricedUpgrade(upgrade: SpellUpgrade): PricedItem {
        val price = when (upgrade.rarity) {
            Rarity.COMMON -> 3
            Rarity.UNCOMMON -> 5
            Rarity.RARE -> 7
            else -> 4
        }
        return PricedItem(upgrade.id, price)
    }
}
