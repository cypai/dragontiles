package com.pipai.dragontiles.dungeon

import com.fasterxml.jackson.annotation.JsonIgnore
import com.pipai.dragontiles.data.Seed
import com.pipai.dragontiles.utils.choose
import kotlin.random.Random

data class DungeonMap(
    val dungeonId: String,
    val map: List<List<MapNode>>,
    val encounters: MutableList<String> = mutableListOf(),
    val dungeonEvents: MutableList<String> = mutableListOf(),
    var currentFloor: Int = 0,
    var currentFloorIndex: Int = 0,
    var easyFights: Int = 0,
) {
    companion object {
        fun generateMap(seed: Seed): List<List<MapNode>> {
            val rng = seed.dungeonRng()
            val map: MutableList<List<MapNode>> = mutableListOf()
            var previousFloor: List<MapNode>? = null
            var prevPrevFloor: List<MapNode>? = null
            val elites = mutableListOf(0, 0, 0)
            val combats = mutableListOf(0, 0, 0)
            val events = mutableListOf(0, 0, 0)
            val towns = mutableListOf(0, 0, 0)
            for (floorNum in 0..11) {
                val floor: MutableList<MapNode> = mutableListOf()
                when (floorNum) {
                    0 -> {
                        floor.add(MapNode(MapNodeType.START, false, mutableListOf(), mutableListOf(0, 1, 2)))
                    }
                    1 -> {
                        repeat(3) {
//                        floor.add(MapNode(MapNodeType.EVENT, false, mutableListOf(0), mutableListOf()))
                            floor.add(MapNode(MapNodeType.TOWN, false, mutableListOf(0), mutableListOf()))
//                            floor.add(MapNode(MapNodeType.COMBAT, false, mutableListOf(0), mutableListOf()))
                            //floor.add(MapNode(MapNodeType.ELITE, false, mutableListOf(0), mutableListOf()))
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
                        repeat(3) { i ->
                            val allowedNodes = mutableListOf(MapNodeType.COMBAT, MapNodeType.EVENT)
                            if (floorNum > 3 && towns[i] == 0) {
                                allowedNodes.add(MapNodeType.TOWN)
                            }
                            if (floorNum > 3 && elites[i] < 3 && previousFloor!![i].type != MapNodeType.ELITE) {
                                allowedNodes.add(MapNodeType.ELITE)
                            }
                            if (floorNum >= 3 && prevPrevFloor!![i].type == previousFloor!![i].type) {
                                allowedNodes.remove(previousFloor!![i].type)
                            }
                            if (floorNum == 6 && towns[i] == 0) {
                                allowedNodes.clear()
                                allowedNodes.add(MapNodeType.TOWN)
                            }
                            if (floorNum > 7 && combats[i] > 3) {
                                allowedNodes.remove(MapNodeType.COMBAT)
                            }
                            if (floorNum > 7 && events[i] > 3) {
                                allowedNodes.remove(MapNodeType.EVENT)
                            }
                            if (allowedNodes.contains(MapNodeType.ELITE) && floorNum == 9 && elites[i] == 0) {
                                allowedNodes.clear()
                                allowedNodes.add(MapNodeType.ELITE)
                            }
                            val node = randomNode(rng, allowedNodes)
                            floor.add(node)
                            when (node.type) {
                                MapNodeType.COMBAT -> combats[i]++
                                MapNodeType.EVENT -> events[i]++
                                MapNodeType.ELITE -> elites[i]++
                                MapNodeType.TOWN -> towns[i]++
                                else -> {}
                            }
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
                prevPrevFloor = previousFloor
                previousFloor = floor
            }
            return map
        }

        private fun randomNode(rng: Random, allowedNodes: List<MapNodeType>): MapNode {
            val type = allowedNodes.choose(rng)
            return MapNode(type, false, mutableListOf(), mutableListOf())
        }
    }

    fun changeNode(floorNum: Int, floorIndex: Int): MapNode {
        currentFloor = floorNum
        currentFloorIndex = floorIndex
        return getCurrentNode()
    }

    @JsonIgnore
    fun getCurrentNode(): MapNode {
        return map[currentFloor][currentFloorIndex]
    }
}


data class MapNode(val type: MapNodeType, val hidden: Boolean, val prev: MutableList<Int>, val next: MutableList<Int>)

enum class MapNodeType {
    START, COMBAT, ELITE, BOSS, EVENT, TOWN;
}

