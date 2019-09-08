package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.artemis.World
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class DamageAnimation(world: World, private val enemy: Enemy, private val amount: Int) : Animation(world) {

    private lateinit var mEnemy: ComponentMapper<EnemyComponent>

    init {
        world.inject(this)
    }

    override fun startAnimation() {
        world.fetch(allOf(EnemyComponent::class)).forEach {
            val cEnemy = mEnemy.get(it)
            if (cEnemy.enemy == enemy) {
                cEnemy.hp -= amount
            }
        }
        endAnimation()
    }

}
