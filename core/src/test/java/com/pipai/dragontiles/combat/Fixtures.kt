package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.GameData
import com.pipai.dragontiles.dungeon.DungeonInitializer
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.dungeon.RunHistory
import com.pipai.dragontiles.hero.Elementalist
import com.pipai.dragontiles.hero.Hero
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.relics.RelicData
import com.pipai.dragontiles.spells.Spell
import java.util.*

fun runDataFixture(spells: MutableList<Spell>, relics: MutableList<Relic>): RunData {
    return RunData(
        DungeonInitializer(),
        Hero(
            Elementalist(),
            "Elementalist",
            80,
            80,
            0,
            40,
            15,
            spells,
            6,
            mutableListOf(),
            3,
            mutableListOf(),
            9,
            relics,
            0,
            mutableListOf()
        ),
        RelicData(mutableListOf()),
        null,
        0,
        GameData.BASE_POTION_CHANCE,
        RunHistory(mutableListOf()),
        Random(),
    )
}

val COMBAT_REWARDS_FIXTURE = CombatRewards(SpellRewardType.STANDARD, 0, false, null, GameData.BASE_POTION_CHANCE)
