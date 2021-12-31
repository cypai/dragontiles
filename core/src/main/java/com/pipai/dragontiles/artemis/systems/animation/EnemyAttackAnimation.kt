package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.enemies.Enemy

abstract class EnemyAttackAnimation : Animation() {

    var enemy: Enemy? = null
    var damage: Int = 0

    companion object {
        val NO_DELAY = object : EnemyAttackAnimation() {
            override fun startAnimation() {
                endAnimation()
            }
        }
        val DELAY = object : EnemyAttackAnimation() {
            override fun startAnimation() {
                endAnimation(0.5f)
            }
        }
    }
}
