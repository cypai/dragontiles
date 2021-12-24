package com.pipai.dragontiles.data

import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.choose

class TownGenerator {

    fun generate(gameData: GameData, runData: RunData) {
        val rng = runData.seed.rewardRng()
        val heroClass = gameData.getHeroClass(runData.hero.heroClassId)
        val spellShop = SpellShop(
            heroClass.getRandomClassSpells(runData.seed, 3).map { pricedSpell(it) }.toMutableList(),
            mutableListOf(),
            pricedSpell(gameData.colorlessSpells().choose(rng)),
        )
        val itemShop = ItemShop(mutableListOf())
        val scribe = Scribe(mutableListOf())
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

}
