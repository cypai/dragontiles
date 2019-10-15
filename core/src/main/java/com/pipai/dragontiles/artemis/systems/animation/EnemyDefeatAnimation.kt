package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class EnemyDefeatAnimation(private val enemy: Enemy) : Animation() {

    override fun startAnimation() {
        world.fetch(allOf(EnemyComponent::class)).forEach {
            world.delete(it)
        }
        endAnimation()
    }

}
