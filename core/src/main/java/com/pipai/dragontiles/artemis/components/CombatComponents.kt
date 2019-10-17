package com.pipai.dragontiles.artemis.components

import com.artemis.Component
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.combat.CountdownAttack
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy

class TileComponent : Component() {
    lateinit var tile: TileInstance
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
    var strId = ""
    var hp = 0
    var hpMax = 0
    lateinit var enemy: Enemy

    fun setByEnemy(enemy: Enemy) {
        strId = enemy.strId
        hp = enemy.hp
        hpMax = enemy.hpMax
        this.enemy = enemy
    }
}

class AttackCircleComponent : Component() {
    var id = 0
    var enemyId = 0
    var displayedSpellPower = 0
    var damagingAttack = true
    var attackPower = 0
    var effectPower = 0
    var counteredAttackPower = 0
    var counteredEffectPower = 0
    var turnsLeft = 0
    var maxTurns = 0
    lateinit var element: Element
    lateinit var strId: String

    lateinit var color: Color
    var t = 0f
    var up = true

    fun setByCountdown(countdownAttack: CountdownAttack) {
        id = countdownAttack.id
        displayedSpellPower = if (countdownAttack.isDamaging()) {
            countdownAttack.calcAttackPower()
        } else {
            countdownAttack.calcEffectPower()
        }
        damagingAttack = countdownAttack.isDamaging()
        attackPower = countdownAttack.attackPower
        effectPower = countdownAttack.effectPower
        counteredAttackPower = countdownAttack.counteredAttackPower
        counteredEffectPower = countdownAttack.counteredEffectPower
        turnsLeft = countdownAttack.turnsLeft
        maxTurns = countdownAttack.turnsLeft
        element = countdownAttack.element
        strId = countdownAttack.strId

        color = when (element) {
            Element.FIRE -> Color.RED
            Element.ICE -> Color.CYAN
            Element.LIGHTNING -> Color.YELLOW
            else -> Color.WHITE
        }
    }
}
