package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.artemis.World
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.data.Tile

class AdjustOpenPoolAnimation(world: World, private val openPool: List<Tile>) : Animation(world) {

    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>

    private lateinit var sTileId: TileIdSystem

    init {
        world.inject(this)
    }

    override fun startAnimation() {
        openPool.forEachIndexed { index, tile ->
            val entityId = sTileId.getEntityId(tile.id)
            val cPath = mPath.create(entityId)
            cPath.endpoints.add(mXy.get(entityId).toVector2())
            cPath.endpoints.add(Vector2(64f + 32f * (index + 1), 128f))
            cPath.interpolation = Interpolation.pow3Out
            cPath.maxT = 30
            cPath.onEnd = EndStrategy.REMOVE
            cPath.onEndpoint = { endAnimation() }
        }
    }

}
