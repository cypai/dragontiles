package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.dungeon.DungeonMap
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Spell

fun runDataFixture(spells: MutableList<Spell>, relics: MutableList<Relic>): RunData {
    return RunData(
        Hero(
            "base:hero:Elementalist",
            "Elementalist",
            80,
            80,
            0,
            40,
            15,
            spells.map { it.toInstance() }.toMutableList(),
            6,
            mutableListOf(),
            3,
            mutableListOf(),
            9,
            relics.map { it.toInstance() }.toMutableList(),
            0,
            mutableListOf()
        ),
        DungeonMap("", listOf(), "", mutableListOf(), mutableListOf()),
        mutableListOf(),
        null,
        0,
        GameData.BASE_POTION_CHANCE,
        false,
        mutableListOf(),
        RunHistory(VictoryStatus.IN_PROGRESS, 0, mutableListOf()),
        Seed(),
    )
}

val COMBAT_REWARDS_FIXTURE = CombatRewardConfig(SpellRewardType.STANDARD, 0, false, null, GameData.BASE_POTION_CHANCE)
