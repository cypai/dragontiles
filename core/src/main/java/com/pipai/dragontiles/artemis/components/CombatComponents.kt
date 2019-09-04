package com.pipai.dragontiles.artemis.components

import com.artemis.Component
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.enemies.Enemy

class TileComponent : Component() {
    lateinit var tile: Tile
}

class HandLocationComponent : Component() {
    var location: Int = 0
}

class EnemyComponent : Component() {
    var name = ""
    var hp = 0
    var hpMax = 0

    fun setByEnemy(enemy: Enemy) {
        name = enemy.name
        hp = enemy.hp
        hpMax = enemy.hpMax
    }
}
