package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.common.*
import com.pipai.dragontiles.spells.elementalist.*

class HeroSpells {

    fun elementalistStarterDeck(): MutableList<Spell> {
        return mutableListOf(
            Invoke(),
            Strike(),
            Vent(),
            ElementalRune(),
            Break()
        )
    }

    fun elementalistSpells(): List<Spell> {
        return listOf(
            Mulligan(),
            Ground(),
            Reserve(),
            QuickInvoke(),
            MultiInvoke(),
            StrengthRune(),
            RampStrike(),
            Concentrate(),
            FeedbackLoop(),
            Bump(),
            Nudge(),
            Singularity(),
            Spark(),
            Blast(),
            Explosion()
        )
    }

    fun cantrips(): List<Spell> {
        return listOf(
            Bump(),
            Mulligan(),
            Reserve(),
            Nudge(),
            Ground()
        )
    }

    fun colorlessSpells(): List<Spell> {
        return listOf(
            Singularity(),
            Fetch()
        )
    }

    fun generateRewards(runData: RunData, amount: Int): List<Spell> {
        val spells = elementalistSpells().shuffled().toMutableList()
        val rewards: MutableList<Spell> = mutableListOf()
        repeat(amount) {
            val rarityRoll = runData.rng.nextInt(20)
            val rarity = when {
                rarityRoll == 0 -> Rarity.RARE
                rarityRoll < 6 -> Rarity.UNCOMMON
                else -> Rarity.COMMON
            }
            val spell = spells.first { it.rarity == rarity }
            spells.remove(spell)
            rewards.add(spell)
        }
        return rewards
    }

}
