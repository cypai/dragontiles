package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.data.TileInstance

class AdjustHandAnimation(private val tileLocations: List<Pair<TileInstance, Int>>) : Animation() {

    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>

    private lateinit var sTileId: TileIdSystem

    override fun startAnimation() {
        tileLocations.forEach { (tile, index) ->
            val entityId = sTileId.getEntityId(tile.id)
            val cPath = mPath.create(entityId)
            cPath.endpoints.add(mXy.get(entityId).toVector2())
            cPath.endpoints.add(Vector2(64f + 32f * index, 0f))
            cPath.interpolation = Interpolation.pow3Out
            cPath.maxT = 20
            cPath.onEnd = EndStrategy.REMOVE
            cPath.onEndpoint = { endAnimation() }
        }
    }

}
