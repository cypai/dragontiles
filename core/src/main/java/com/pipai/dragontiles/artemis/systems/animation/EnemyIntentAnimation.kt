package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.combat.EnemyIntentSystem
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.enemies.Enemy

class EnemyIntentAnimation(private val enemy: Enemy, private val intent: Intent?) : Animation() {

    private lateinit var sEnemyIntent: EnemyIntentSystem

    override fun startAnimation() {
        sEnemyIntent.changeIntent(enemy, intent)
        endAnimation()
    }

}
