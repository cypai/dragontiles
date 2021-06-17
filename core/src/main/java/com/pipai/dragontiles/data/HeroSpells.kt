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
            Invoke(),
            Strike(),
            Break(),
            ElementalRune()
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

    fun generateRewards(runData: RunData, amount: Int): List<Spell> {
        val spells = elementalistSpells()
        val commons = spells.filter { it.rarity == Rarity.COMMON }
        val uncommons = spells.filter { it.rarity == Rarity.UNCOMMON }
        val rares = spells.filter { it.rarity == Rarity.RARE }
        val rng = runData.rng
        val rarityRoll = rng.nextInt(20)
        val rewardSpells = when {
            rarityRoll == 0 -> rares
            rarityRoll < 6 -> uncommons
            else -> commons
        }
        return rewardSpells.shuffled().subList(0, amount).map { it.newClone() }
    }

    private fun rngUpgrade(rng: Random) = rng.nextInt(100) < 10

}
