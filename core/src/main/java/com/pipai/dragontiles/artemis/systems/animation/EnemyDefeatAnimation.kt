package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.artemis.systems.combat.StatusSystem
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class EnemyDefeatAnimation(private val enemy: Enemy) : Animation() {

    private lateinit var mEnemy: ComponentMapper<EnemyComponent>

    private lateinit var sStatus: StatusSystem

    override fun startAnimation() {
        world.fetch(allOf(EnemyComponent::class)).forEach {
            if (enemy.enemyId == mEnemy.get(it).enemy.enemyId) {
                sStatus.handleEnemyDefeat(it)
                world.delete(it)
            }
        }
        endAnimation()
    }

}
