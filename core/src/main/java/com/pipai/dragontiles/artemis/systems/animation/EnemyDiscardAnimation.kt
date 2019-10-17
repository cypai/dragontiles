package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.components.AttackCircleComponent
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileSkin
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class EnemyDiscardAnimation(
        private val enemyId: Int,
        private val tile: TileInstance,
        private val poolLocation: Int,
        tileSkin: TileSkin) : TileCreationAnimation(tileSkin) {

    private lateinit var mAttackCircle: ComponentMapper<AttackCircleComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>

    override fun startAnimation() {
        val attackEntityId = world.fetch(allOf(AttackCircleComponent::class))
                .first { mAttackCircle.get(it).enemyId == enemyId }
        val cXy = mXy.get(attackEntityId)
        val entityId = createTile(tile, 0f, 0f)
        val cPath = mPath.create(entityId)
        cPath.endpoints.add(cXy.toVector2())
        cPath.endpoints.add(Vector2(64f + 32f * poolLocation, 128f))
        cPath.interpolation = Interpolation.pow3Out
        cPath.maxT = 30
        cPath.onEnd = EndStrategy.REMOVE
        cPath.onEndpoint = { endAnimation() }
    }

}
