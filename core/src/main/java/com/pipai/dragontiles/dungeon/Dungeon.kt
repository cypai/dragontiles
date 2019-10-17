package com.pipai.dragontiles.dungeon

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.enemies.Slime
import com.pipai.dragontiles.utils.choose

abstract class Dungeon {

    abstract val easyEncounters: List<Encounter>
    abstract val standardEncounters: List<Encounter>
    abstract val eliteEncounters: List<Encounter>
    abstract val bossEncounters: List<Encounter>

    fun easyEncounter(runData: RunData): Encounter {
        return easyEncounters.choose(runData.rng)
    }

    fun standardEncounter(runData: RunData): Encounter {
        return standardEncounters.choose(runData.rng)
    }

    fun eliteEncounter(runData: RunData): Encounter {
        return eliteEncounters.choose(runData.rng)
    }

    fun bossEncounter(runData: RunData): Encounter {
        return bossEncounters.choose(runData.rng)
    }
}

class PlainsDungeon : Dungeon() {
    override val easyEncounters: MutableList<Encounter> = mutableListOf(
            Encounter(listOf(Pair(FlameTurtle(), Vector2(92f, 420f)))),
            Encounter(listOf(
                    Pair(Slime(), Vector2(92f, 420f)),
                    Pair(Slime(), Vector2(270f, 420f))
            ))
    )
    override val standardEncounters: List<Encounter> = listOf()
    override val eliteEncounters: List<Encounter> = listOf()
    override val bossEncounters: List<Encounter> = listOf()
}
