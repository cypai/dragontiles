package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.common.*
import com.pipai.dragontiles.spells.elementalist.*
import com.pipai.dragontiles.utils.choose

class HeroSpells {

    fun elementalistStarterDeck(): MutableList<Spell> {
        return mutableListOf(
            Invoke(),
            Strike(),
            Vent(),
            ElementalRune(),
            Break(),
            Concentrate(),
        )
    }

    fun elementalistSpells(): List<Spell> {
        return listOf(
            QuickInvoke(),
            MultiInvoke(),
            StrengthRune(),
            RampStrike(),
            Concentrate(),
            FeedbackLoop(),
            Singularity(),
            Spark(),
            Blast(),
            Explosion()
        )
    }

    fun cantrips(): List<Spell> {
        return listOf(
            Ground(),
            Mulligan(),
            Reserve(),
            Bump(),
            Nudge(),
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
            val spell = spells.firstOrNull { it.rarity == rarity } ?: spells.choose(runData.rng)
            spells.remove(spell)
            rewards.add(spell)
        }
        return rewards
    }

}
