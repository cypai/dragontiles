package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.sorceries.*
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.common.*
import com.pipai.dragontiles.spells.elementalist.*
import com.pipai.dragontiles.utils.choose

class HeroSpells {

    fun elementalistStarterDeck(): List<Spell> {
        return listOf(
            Invoke(),
            Fireball(),
            Vent(),
            ElementalRune(),
            Break(),
            BurnRune(),
        )
    }

    fun elementalistStarterSorceries(): List<Sorcery> {
        return listOf(
            Eyes(),
        )
    }

    fun elementalistSpells(): List<Spell> {
        return listOf(
            QuickInvoke(),
            MultiInvoke(),
            StrengthRune(),
            PiercingStrike(),
            RampStrike(),
            Concentrate(),
            FeedbackLoop(),
            Singularity(),
            Spark(),
            Blast(),
            Explosion(),
            Precipitate(),
            BurnRune(),
            FrostRune(),
            Fireball(),
            IceShard(),
            ChainLightning(),
            CommonSorcery(),
            Chow(),
            Pong(),
        )
    }

    fun colorlessSpells(): List<Spell> {
        return listOf(
            Singularity(),
            Fetch(),
            Ground(),
            Mulligan(),
            Reserve(),
            Bump(),
            Nudge(),
            Chow(),
            Pong(),
        )
    }

    fun getRandomClassSpells(runData: RunData, amount: Int): List<Spell> {
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
