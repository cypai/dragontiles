package com.pipai.dragontiles.artemis.components

import com.artemis.Component
import com.badlogic.gdx.Input.Keys
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.enemies.Enemy

class TileComponent : Component() {
    lateinit var tile: Tile
}

class HandLocationComponent : Component() {
    var location = 0
    var keyShortcut = Keys.Q
    var keyShift = false
    var x = 0f
    var y = 0f

    fun setByLocation(location: Int) {
        this.location = location
        x = 64f + 32f * location
        keyShortcut = when (location % 10) {
            1 -> Keys.Q
            2 -> Keys.W
            3 -> Keys.E
            4 -> Keys.R
            5 -> Keys.T
            6 -> Keys.Y
            7 -> Keys.U
            8 -> Keys.I
            9 -> Keys.O
            0 -> Keys.P
            else -> Keys.Q
        }
        keyShift = location > 10
    }
}

class EnemyComponent : Component() {
    var name = ""
    var hp = 0
    var hpMax = 0
    lateinit var enemy: Enemy

    fun setByEnemy(enemy: Enemy) {
        name = enemy.name
        hp = enemy.hp
        hpMax = enemy.hpMax
        this.enemy = enemy
    }
}
