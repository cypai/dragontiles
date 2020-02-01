package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.common.Blast
import com.pipai.dragontiles.spells.common.Invoke
import com.pipai.dragontiles.spells.common.Strike
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

    fun generateRewards(runData: RunData, amount: Int): List<Spell> {
        val spells = listOf(
                Mulligan(rngUpgrade(runData.rng)),
                Ground(rngUpgrade(runData.rng)),
                Reserve(rngUpgrade(runData.rng)),
                QuickInvoke(rngUpgrade(runData.rng)),
                MultiInvoke(rngUpgrade(runData.rng)),
                EnhanceRune(rngUpgrade(runData.rng)),
                StrengthRune(rngUpgrade(runData.rng)),
                RampStrike(rngUpgrade(runData.rng)),
                Concentrate(rngUpgrade(runData.rng)),
                FeedbackLoop(rngUpgrade(runData.rng)),
                Bump(rngUpgrade(runData.rng)),
                Nudge(rngUpgrade(runData.rng)),
                Singularity(rngUpgrade(runData.rng)),
                Spark(rngUpgrade(runData.rng)),
                Blast(rngUpgrade(runData.rng)),
                Explosion(rngUpgrade(runData.rng))
        )
        return spells.shuffled().subList(0, amount)
    }

    private fun rngUpgrade(rng: Random) = rng.nextInt(100) < 20

}
