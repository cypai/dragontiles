package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.artemis.World
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.data.Tile

class DrawTileAnimation(private val world: World, private val tile: Tile) : Animation() {

    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>
    private lateinit var mTile: ComponentMapper<TileComponent>

    init {
        world.inject(this)
    }

    override fun startAnimation() {
        val entityId = world.create()
        val cTile = mTile.create(entityId)
        cTile.tile = tile
        val cXy = mXy.create(entityId)
        cXy.setXy(0f, 0f)
        val cPath = mPath.create(entityId)
        cPath.endpoints.add(cXy.toVector2())
        cPath.endpoints.add(Vector2(160f, 0f))
        cPath.interpolation = Interpolation.pow3Out
        cPath.maxT = 30
        cPath.onEnd = EndStrategy.REMOVE
        cPath.onEndpoint = { observer.notify(this) }
    }

}
