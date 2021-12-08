package com.pipai.dragontiles.dungeon

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.enemies.Slime
import com.pipai.dragontiles.utils.choose
import java.util.*

abstract class Dungeon {
    var currentFloor: Int = 0
    var currentFloorIndex: Int = 0

    abstract val easyEncounters: MutableList<Encounter>
    abstract val standardEncounters: MutableList<Encounter>
    abstract val eliteEncounters: MutableList<Encounter>
    abstract val bossEncounters: MutableList<Encounter>

    private val map: MutableList<List<MapNode>> = mutableListOf()

    fun getMap(): List<List<MapNode>> = map

    fun generateMap(rng: Random) {
        var previousFloor: List<MapNode>? = null
        for (i in 0..11) {
            val floor: MutableList<MapNode> = mutableListOf()
            when (i) {
                0 -> {
                    floor.add(MapNode(MapNodeType.START, false, mutableListOf(), mutableListOf(0, 1, 2)))
                }
                1 -> {
                    repeat(3) {
                        floor.add(MapNode(MapNodeType.TOWN, false, mutableListOf(0), mutableListOf()))
                        //floor.add(MapNode(MapNodeType.COMBAT, false, mutableListOf(0), mutableListOf()))
                    }
                }
                10 -> {
                    val node = MapNode(MapNodeType.TOWN, false, mutableListOf(), mutableListOf())
                    previousFloor!!.forEach { it.next.add(0) }
                    node.prev.addAll(previousFloor.indices)
                    floor.add(node)
                }
                11 -> {
                    val node = MapNode(MapNodeType.BOSS, false, mutableListOf(), mutableListOf())
                    previousFloor!!.forEach { it.next.add(0) }
                    node.prev.add(0)
                    floor.add(node)
                }
                else -> {
                    repeat(3) {
                        val allowedNodes = if (i == 5) {
                            listOf(MapNodeType.TOWN)
                        } else {
                            if (i in listOf(4,6,8) && rng.nextBoolean()) {
                                listOf(MapNodeType.ELITE)
                            } else {
                                listOf(MapNodeType.COMBAT, MapNodeType.EVENT)
                            }
                        }
                        val node = randomNode(rng, allowedNodes)
                        floor.add(node)
                    }
                    previousFloor!![0].next.add(0)
                    previousFloor[1].next.add(1)
                    previousFloor[2].next.add(2)
                    floor[0].prev.add(0)
                    floor[1].prev.add(1)
                    floor[2].prev.add(2)
                    if (rng.nextBoolean()) {
                        previousFloor[0].next.add(1)
                        floor[1].prev.add(0)
                    }
                    if (rng.nextBoolean()) {
                        previousFloor[1].next.add(0)
                        floor[0].prev.add(1)
                    }
                    if (rng.nextBoolean()) {
                        previousFloor[1].next.add(2)
                        floor[2].prev.add(1)
                    }
                    if (rng.nextBoolean()) {
                        previousFloor[2].next.add(1)
                        floor[1].prev.add(2)
                    }
                }
            }
            map.add(floor)
            previousFloor = floor
        }
    }

    private fun randomNode(rng: Random, allowedNodes: List<MapNodeType>): MapNode {
        val type = allowedNodes.choose(rng)
        return MapNode(type, false, mutableListOf(), mutableListOf())
    }

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

data class MapNode(val type: MapNodeType, val hidden: Boolean, val prev: MutableList<Int>, val next: MutableList<Int>)

enum class MapNodeType {
    START, COMBAT, ELITE, BOSS, EVENT, TOWN;
}

class PlainsDungeon : Dungeon() {
    override val easyEncounters: MutableList<Encounter> = mutableListOf(
            Encounter(listOf(Pair(FlameTurtle(), Vector2(750f, 420f)))),
            Encounter(listOf(
                    Pair(Slime(), Vector2(740f, 420f)),
                    Pair(Slime(), Vector2(910f, 420f))
            ))
    )
    override val standardEncounters: MutableList<Encounter> = mutableListOf(
            Encounter(listOf(
                    Pair(FlameTurtle(), Vector2(650f, 420f)),
                    Pair(Slime(), Vector2(1000f, 420f))
            ))
    )
    override val eliteEncounters: MutableList<Encounter> = mutableListOf()
    override val bossEncounters: MutableList<Encounter> = mutableListOf()
}
