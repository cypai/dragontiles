package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class EnemyIntentAnimation(private val enemy: Enemy, private val intent: Intent?) : Animation() {

    private lateinit var mEnemy: ComponentMapper<EnemyComponent>

    override fun startAnimation() {
        world.fetch(allOf(EnemyComponent::class)).forEach {
            val cEnemy = mEnemy.get(it)
            if (cEnemy.enemy == enemy) {
                cEnemy.intent = intent
            }
        }
        endAnimation()
    }

}
