package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatantStateSystem
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.enemies.Enemy

class EnemyIntentAnimation(private val enemy: Enemy, private val intent: Intent?) : Animation() {

    private lateinit var sCombatantState: CombatantStateSystem

    override fun startAnimation() {
        sCombatantState.updateIntent(enemy, intent)
        endAnimation()
    }

}
