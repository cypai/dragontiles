package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.common.*
import com.pipai.dragontiles.spells.elementalist.*
import java.util.*

class HeroSpells {

    fun elementalistStarterDeck(): MutableList<Spell> {
        return mutableListOf(
                Invoke(false),
                Strike(false),
                Break(false),
                ElementalRune(false))
    }

    fun elementalistSpells(): List<Spell> {
        return listOf(
                Mulligan(false),
                Ground(false),
                Reserve(false),
                QuickInvoke(false),
                MultiInvoke(false),
                EnhanceRune(false),
                StrengthRune(false),
                RampStrike(false),
                Concentrate(false),
                FeedbackLoop(false),
                Bump(false),
                Nudge(false),
                Singularity(false),
                Spark(false),
                Blast(false),
                Explosion(false)
        )
    }

    fun generateRewards(runData: RunData, amount: Int): List<Spell> {
        val spells = elementalistSpells()
        val commons = spells.filter { it.rarity == Rarity.COMMON }
        val uncommons = spells.filter { it.rarity == Rarity.UNCOMMON }
        val rares = spells.filter { it.rarity == Rarity.RARE }
        val rng = runData.rng
        val rarityRoll = rng.nextInt(20)
        val rewardSpells = when  {
            rarityRoll == 0 -> rares
            rarityRoll < 6 -> uncommons
            else -> commons
        }
        return rewardSpells.shuffled().subList(0, amount).map { it.newClone(rngUpgrade(rng)) }
    }

    private fun rngUpgrade(rng: Random) = rng.nextInt(100) < 10

}
