package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileSkin

class DrawToOpenPoolAnimation(private val tile: TileInstance,
                              private val poolLocation: Int,
                              tileSkin: TileSkin) : TileCreationAnimation(tileSkin) {

    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>

    override fun startAnimation() {
        val entityId = createTile(tile, 0f, 0f)
        val cPath = mPath.create(entityId)
        cPath.endpoints.add(Vector2())
        cPath.endpoints.add(Vector2(64f + 32f * poolLocation, 128f))
        cPath.interpolation = Interpolation.pow3Out
        cPath.maxT = 30
        cPath.onEnd = EndStrategy.REMOVE
        cPath.onEndpoint = { endAnimation() }
    }

}
