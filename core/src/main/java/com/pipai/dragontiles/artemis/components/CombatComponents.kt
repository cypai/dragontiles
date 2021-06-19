package com.pipai.dragontiles.artemis.components

import com.artemis.Component
import com.badlogic.gdx.Input.Keys
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.enemies.Enemy

class TileComponent : Component() {
    lateinit var tile: TileInstance
}

class HandLocationComponent : Component() {
    var location = 0
    var keyShortcut = Keys.Q
    var keyShift = false

    fun setByLocation(location: Int) {
        this.location = location
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

class HeroComponent : Component() {
    var strId = ""
    var hp = 0
    var hpMax = 0
    var flux = 0
    var fluxMax = 0

    fun setByRunData(runData: RunData) {
        hp = runData.hero.hp
        hpMax = runData.hero.hpMax
        flux = runData.hero.flux
        fluxMax = runData.hero.fluxMax
    }
}

class EnemyComponent : Component() {
    var strId = ""
    var hp = 0
    var hpMax = 0
    var flux = 0
    var fluxMax = 0
    lateinit var enemy: Enemy
    var intent: Intent? = null

    fun setByEnemy(enemy: Enemy) {
        strId = enemy.strId
        hp = enemy.hp
        hpMax = enemy.hpMax
        flux = enemy.flux
        fluxMax = enemy.fluxMax
        this.enemy = enemy
    }
}

class TargetHighlightComponent : Component() {
    var xOffset = 0f
    var width = 0f
    var height = 0f
    var padding = 0f
    var alpha = 0f
}
