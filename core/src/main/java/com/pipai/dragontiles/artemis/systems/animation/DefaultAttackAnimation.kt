package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Interpolation
import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.Event
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.systems.PathInterpolationSystem
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class DefaultAttackAnimation(val enemy: Enemy) : Animation() {
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>

    override fun startAnimation() {
        val entityId = world.fetch(allOf(EnemyComponent::class)).first { mEnemy.get(it).enemy == enemy }
        val cXy = mXy.get(entityId)
        val cPath = mPath.create(entityId)
        cPath.setPath(
            cXy.toVector2(),
            cXy.toVector2().add(-32f, 0f),
            0.1f,
            Interpolation.linear,
            EndStrategy.REVERSE_THEN_REMOVE
        )
        cPath.onEndpoint = {
            endAnimation()
        }
    }
}
