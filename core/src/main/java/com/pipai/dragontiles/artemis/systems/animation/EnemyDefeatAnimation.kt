package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class EnemyDefeatAnimation(private val enemy: Enemy) : Animation() {

    private lateinit var mEnemy: ComponentMapper<EnemyComponent>

    override fun startAnimation() {
        world.fetch(allOf(EnemyComponent::class)).forEach {
            if (enemy.id == mEnemy.get(it).enemy.id) {
                world.delete(it)
            }
        }
        endAnimation()
    }

}
