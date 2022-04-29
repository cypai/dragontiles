package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatantStateSystem
import com.pipai.dragontiles.enemies.Enemy

class EnemyHealAnimation(private val enemy: Enemy, private val amount: Int) : Animation() {

    private lateinit var sCombatantState: CombatantStateSystem

    override fun startAnimation() {
        sCombatantState.changeEnemyHp(enemy, amount)
        endAnimation()
    }

}
