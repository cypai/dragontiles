package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.spells.*
import java.util.*

class HeroSpells {

    fun generateRewards(runData: RunData, amount: Int): List<Spell> {
        val spells = listOf(
                Strike(rngUpgrade(runData.rng)),
                RampStrike(rngUpgrade(runData.rng)),
                Break(rngUpgrade(runData.rng)),
                Concentrate(rngUpgrade(runData.rng)),
                Blast(rngUpgrade(runData.rng)),
                Explosion(rngUpgrade(runData.rng))
        )
        return spells.shuffled().subList(0, amount)
    }

    private fun rngUpgrade(rng: Random) = rng.nextInt(100) < 20

}
