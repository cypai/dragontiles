package com.pipai.dragontiles.data

import com.pipai.dragontiles.potions.ExplosivePotion
import com.pipai.dragontiles.potions.HealingPotion
import com.pipai.dragontiles.relics.*
import com.pipai.dragontiles.sorceries.Chow
import com.pipai.dragontiles.sorceries.Pong
import com.pipai.dragontiles.spells.common.*
import com.pipai.dragontiles.spells.elementalist.Singularity

class GameData {

    companion object {
        val colorlessSpells = listOf(
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

        val relics = listOf(
            Bamboo(),
            Cherry(),
            Coffee(),
            Elixir(),
            Ginsengfruit(),
            Peach(),
            Tea(),
        )

        val potions = listOf(
            ExplosivePotion(),
            HealingPotion(),
        )

        const val BASE_POTION_CHANCE = 0.2f
    }

}
