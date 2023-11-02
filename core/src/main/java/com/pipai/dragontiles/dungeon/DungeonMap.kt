package com.pipai.dragontiles.dungeon

import com.fasterxml.jackson.annotation.JsonIgnore
import com.pipai.dragontiles.data.Seed
import com.pipai.dragontiles.utils.choose
import kotlin.random.Random

data class DungeonMap(
    val dungeonId: String,
    val nextDungeonMap: DungeonMap?,
    val map: List<List<MapNode>>,
    val bossId: String,
    val encounters: MutableList<String> = mutableListOf(),
    val dungeonEvents: MutableList<String> = mutableListOf(),
    var currentFloor: Int = 0,
    var currentFloorIndex: Int = 0,
    var easyFights: Int = 0,
) {
    companion object {
        fun generateMap(seed: Seed): List<List<MapNode>> {
            val map: MutableList<List<MapNode>> = mutableListOf()
            var previousFloor: List<MapNode>? = null
            for (floorNum in 0..8) {
                val floor: MutableList<MapNode> = mutableListOf()
                when (floorNum) {
                    0 -> {
                        floor.add(MapNode(MapNodeType.START, false, mutableListOf(), mutableListOf()))
                    }
                    1 -> {
                        floor.add(MapNode(MapNodeType.COMBAT, false, mutableListOf(), mutableListOf()))
                    }
                    3 -> {
                        floor.add(MapNode(MapNodeType.COMBAT, false, mutableListOf(), mutableListOf()))
                        floor.add(MapNode(MapNodeType.ELITE, false, mutableListOf(), mutableListOf()))
                    }
                    5 -> {
                        floor.add(MapNode(MapNodeType.COMBAT, false, mutableListOf(), mutableListOf()))
                        floor.add(MapNode(MapNodeType.ELITE, false, mutableListOf(), mutableListOf()))
                    }
                    7 -> {
                        floor.add(MapNode(MapNodeType.TOWN, false, mutableListOf(), mutableListOf()))
                    }
                    8 -> {
                        floor.add(MapNode(MapNodeType.BOSS, false, mutableListOf(), mutableListOf()))
                    }
                    else -> {
                        floor.add(MapNode(MapNodeType.EVENT, false, mutableListOf(), mutableListOf()))
                    }
                }
                if (floorNum > 0) {
                    previousFloor!!.forEach { it.next.add(0) }
                    floor.forEach { node ->
                        node.prev.addAll(previousFloor!!.indices)
                    }
                }
                map.add(floor)
                previousFloor = floor
            }
            return map
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
    START, COMBAT, ELITE, BOSS, EVENT, TOWN, NEXT_ACT;
}

