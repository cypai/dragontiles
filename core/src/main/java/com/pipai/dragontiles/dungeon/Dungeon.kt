package com.pipai.dragontiles.dungeon

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.enemies.Slime
import com.pipai.dragontiles.utils.choose

abstract class Dungeon {
    var currentFloor: Int = 1

    abstract val easyEncounters: MutableList<Encounter>
    abstract val standardEncounters: MutableList<Encounter>
    abstract val eliteEncounters: MutableList<Encounter>
    abstract val bossEncounters: MutableList<Encounter>

    open fun easyEncounter(runData: RunData): Encounter {
        return easyEncounters.choose(runData.rng)
    }

    open fun standardEncounter(runData: RunData): Encounter {
        return standardEncounters.choose(runData.rng)
    }

    open fun eliteEncounter(runData: RunData): Encounter {
        return eliteEncounters.choose(runData.rng)
    }

    open fun bossEncounter(runData: RunData): Encounter {
        return bossEncounters.choose(runData.rng)
    }
}

class PlainsDungeon : Dungeon() {
    override val easyEncounters: MutableList<Encounter> = mutableListOf(
            Encounter(listOf(Pair(FlameTurtle(), Vector2(92f, 420f)))),
            Encounter(listOf(
                    Pair(Slime(), Vector2(90f, 420f)),
                    Pair(Slime(), Vector2(270f, 420f))
            ))
    )
    override val standardEncounters: MutableList<Encounter> = mutableListOf(
            Encounter(listOf(
                    Pair(FlameTurtle(), Vector2(16f, 420f)),
                    Pair(Slime(), Vector2(450f, 420f)))),
            Encounter(listOf(
                    Pair(Slime(), Vector2(90f, 420f)),
                    Pair(Slime(), Vector2(270f, 420f)),
                    Pair(Slime(), Vector2(450f, 420f))
            ))
    )
    override val eliteEncounters: MutableList<Encounter> = mutableListOf()
    override val bossEncounters: MutableList<Encounter> = mutableListOf()
}
